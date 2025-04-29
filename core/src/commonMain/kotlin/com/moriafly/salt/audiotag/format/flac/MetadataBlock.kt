/*
 * Salt Audio Tag
 * Copyright (C) 2025 Moriafly
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 */

@file:Suppress(
    "unused",
    "ktlint:standard:filename",
    "MemberVisibilityCanBePrivate",
    "SpellCheckingInspection"
)

package com.moriafly.salt.audiotag.format.flac

import com.moriafly.salt.audiotag.rw.CanWrite
import com.moriafly.salt.audiotag.rw.data.Picture
import com.moriafly.salt.audiotag.util.writeInt24
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.encodeToByteString
import kotlinx.io.bytestring.hexToByteString
import kotlinx.io.bytestring.toHexString
import kotlinx.io.readByteArray
import kotlinx.io.readByteString
import kotlinx.io.readString
import kotlinx.io.readUIntLe
import kotlinx.io.write
import kotlinx.io.writeIntLe

internal class MetadataBlock(
    val header: MetadataBlockHeader,
    val data: MetadataBlockData
)

internal sealed class BlockType(
    val value: Int
) {
    data object Streaminfo : BlockType(BLOCK_TYPE_STREAMINFO)

    data object Padding : BlockType(BLOCK_TYPE_PADDING)

    data object Application : BlockType(BLOCK_TYPE_APPLICATION)

    data object Seektable : BlockType(BLOCK_TYPE_SEEKTABLE)

    data object VorbisComment : BlockType(BLOCK_TYPE_VORBIS_COMMENT)

    data object Cuesheet : BlockType(BLOCK_TYPE_CUESHEET)

    data object Picture : BlockType(BLOCK_TYPE_PICTURE)

    data object Invalid : BlockType(BLOCK_TYPE_INVALID)

    companion object {
        private const val BLOCK_TYPE_STREAMINFO = 0
        private const val BLOCK_TYPE_PADDING = 1
        private const val BLOCK_TYPE_APPLICATION = 2
        private const val BLOCK_TYPE_SEEKTABLE = 3
        private const val BLOCK_TYPE_VORBIS_COMMENT = 4
        private const val BLOCK_TYPE_CUESHEET = 5
        private const val BLOCK_TYPE_PICTURE = 6

        /**
         * Forbidden (to avoid confusion with a frame sync code).
         */
        private const val BLOCK_TYPE_INVALID = 127

        fun from(value: Int): BlockType = when (value) {
            BLOCK_TYPE_STREAMINFO -> Streaminfo
            BLOCK_TYPE_PADDING -> Padding
            BLOCK_TYPE_APPLICATION -> Application
            BLOCK_TYPE_SEEKTABLE -> Seektable
            BLOCK_TYPE_VORBIS_COMMENT -> VorbisComment
            BLOCK_TYPE_CUESHEET -> Cuesheet
            BLOCK_TYPE_PICTURE -> Picture
            BLOCK_TYPE_INVALID -> Invalid
            else -> error("Unsupported block type: $value.")
        }
    }
}

internal data class MetadataBlockHeader(
    val isLastMetadataBlock: Boolean,
    val blockType: BlockType,
    val length: Int
) : CanWrite {
    override fun toByteString(): ByteString = Buffer()
        .apply {
            writeByte(
                ((if (isLastMetadataBlock) 0x80 else 0x00) or (blockType.value and 0x7F)).toByte()
            )
            writeByte(((length ushr 16) and 0xFF).toByte())
            writeByte(((length ushr 8) and 0xFF).toByte())
            writeByte((length and 0xFF).toByte())
        }
        .readByteString()

    @Suppress("SpellCheckingInspection")
    companion object {
        fun create(source: Source): MetadataBlockHeader {
            val byteString = source.readByteString(4)

            val isLastMetadataBlock = (byteString[0].toInt() and 0b10000000 shr 7) == 1
            val blockType = byteString[0].toInt() and 0b01111111
            val length = (byteString[1].toInt() and 0xFF shl 16) or
                (byteString[2].toInt() and 0xFF shl 8) or
                (byteString[3].toInt() and 0xFF)
            return MetadataBlockHeader(
                isLastMetadataBlock = isLastMetadataBlock,
                blockType = BlockType.from(blockType),
                length = length
            )
        }
    }
}

internal abstract class MetadataBlockData(
    val blockType: BlockType
) : CanWrite

/**
 * The streaminfo metadata block has information about the whole stream, such as sample rate,
 * number of channels, total number of samples, etc. It MUST be present as the first metadata block
 * in the stream. Other metadata blocks MAY follow. There MUST be no more than one streaminfo
 * metadata block per FLAC stream.
 *
 * @property minBlockSize Samples.
 * @property maxBlockSize Samples.
 * @property minFrameSize Bytes.
 * @property maxFrameSize Bytes.
 * @property sampleRate Max 655350 Hz.
 * @property channelCount 2 to 8.
 * @property bits Bits per sample, 4 to 32.
 * @property sampleCount TODO.
 * @property unencodedAudioDataMd5Checksum MD5 checksum of the unencoded audio data. This allows the
 * decoder to determine if an error exists in the audio data even when, despite the error, the
 * bitstream itself is valid. A value of 0 signifies that the value is not known.
 *
 * 16 bytes (128 bits) HASH.
 */
internal data class MetadataBlockDataStreaminfo(
    val minBlockSize: Int,
    val maxBlockSize: Int,
    val minFrameSize: Int,
    val maxFrameSize: Int,
    val sampleRate: Int,
    val channelCount: Int,
    val bits: Int,
    val sampleCount: Long,
    val unencodedAudioDataMd5Checksum: String
) : MetadataBlockData(BlockType.Streaminfo) {
    override fun toByteString(): ByteString = Buffer()
        .apply {
            writeShort(minBlockSize.toShort())
            writeShort(maxBlockSize.toShort())

            writeInt24(minFrameSize)
            writeInt24(maxFrameSize)

            // Write sampleRate (20 bits), channelCount (3 bits), and bits (5 bits).
            val sampleRateHigh = (sampleRate shr 12).toByte()
            val sampleRateMid = (sampleRate shr 4).toByte()
            val sampleRateLow = (sampleRate and 0x0F) shl 4
            val channelBits = ((channelCount - 1) shl 1) or ((bits - 1) shr 4)
            writeByte(sampleRateHigh)
            writeByte(sampleRateMid)
            writeByte((sampleRateLow or channelBits).toByte())

            // Write bits lower 4 bits and sampleCount higher 4 bits.
            val bitsLower = ((bits - 1) and 0x0F) shl 4
            val sampleCountHigher = (sampleCount shr 32).toInt() and 0x0F
            writeByte((bitsLower or sampleCountHigher).toByte())

            // Write remaining sampleCount (32 bits).
            writeInt((sampleCount and 0xFFFFFFFFL).toInt())

            // Write MD5 checksum (16 bytes).
            @OptIn(ExperimentalStdlibApi::class)
            val md5 = unencodedAudioDataMd5Checksum.hexToByteString()
            write(md5)
        }
        .readByteString()

    init {
        @Suppress("ConvertTwoComparisonsToRangeCheck")
        require(
            16 <= minBlockSize &&
                minBlockSize <= maxBlockSize &&
                maxBlockSize <= 65535
        ) {
            "The minimum block size and the maximum block size MUST be in the 16-65535 range. " +
                "The minimum block size MUST be equal to or less than the maximum block size"
        }

        require(unencodedAudioDataMd5Checksum.length == 32) {
            "MD5 checksum must be 32 characters long."
        }
    }

    companion object {
        fun create(source: Source): MetadataBlockDataStreaminfo {
            val byteString = source.readByteString(34)

            val minBlockSize = (byteString[0].toInt() and 0xFF shl 8) or
                (byteString[1].toInt() and 0xFF)
            val maxBlockSize = (byteString[2].toInt() and 0xFF shl 8) or
                (byteString[3].toInt() and 0xFF)

            val minFrameSize = (byteString[4].toInt() and 0xFF shl 16) or
                (byteString[5].toInt() and 0xFF shl 8) or
                (byteString[6].toInt() and 0xFF)
            val maxFrameSize = (byteString[7].toInt() and 0xFF shl 16) or
                (byteString[8].toInt() and 0xFF shl 8) or
                (byteString[9].toInt() and 0xFF)
            val sampleRate = (byteString[10].toInt() and 0xFF shl 12) or
                (byteString[11].toInt() and 0xFF shl 4) or
                (byteString[12].toInt() and 0xFF shr 4)
            val channelCount = (byteString[12].toInt() and 0x0F shr 1) + 1
            val bits = (
                (byteString[12].toInt() and 0x1 shl 4) or
                    (byteString[13].toInt() and 0xF0 shr 4)
            ) + 1
            val sampleCount = (byteString[13].toLong() and 0x0F shl 32) or
                (byteString[14].toLong() and 0xFF shl 24) or
                (byteString[15].toLong() and 0xFF shl 16) or
                (byteString[16].toLong() and 0xFF shl 8) or
                (byteString[17].toLong() and 0xFF)

            @OptIn(ExperimentalStdlibApi::class)
            val unencodedAudioDataMd5Checksum = byteString.substring(18, 34).toHexString()

            return MetadataBlockDataStreaminfo(
                minBlockSize = minBlockSize,
                maxBlockSize = maxBlockSize,
                minFrameSize = minFrameSize,
                maxFrameSize = maxFrameSize,
                sampleRate = sampleRate,
                channelCount = channelCount,
                bits = bits,
                sampleCount = sampleCount,
                unencodedAudioDataMd5Checksum = unencodedAudioDataMd5Checksum
            )
        }
    }
}

internal data class MetadataBlockDataPadding(
    val length: Int
) : MetadataBlockData(BlockType.Padding) {
    override fun toByteString(): ByteString = Buffer()
        .apply {
            repeat(length) {
                writeByte(0)
            }
        }
        .readByteString()

    companion object {
        fun create(source: Source, length: Int): MetadataBlockDataPadding {
            source.skip(length.toLong())
            return MetadataBlockDataPadding(length)
        }
    }
}

internal data class MetadataBlockDataApplication(
    val id: Int,
    val data: ByteString
) : MetadataBlockData(BlockType.Application) {
    override fun toByteString(): ByteString = Buffer()
        .apply {
            writeInt(id)
            write(data)
        }
        .readByteString()

    companion object {
        fun create(source: Source, length: Int): MetadataBlockDataApplication {
            val id = source.readInt()
            val data = source.readByteString(length - 4)
            return MetadataBlockDataApplication(id, data)
        }
    }
}

internal data class MetadataBlockDataSeektable(
    val seekPoints: List<SeekPoint>
) : MetadataBlockData(BlockType.Seektable) {
    override fun toByteString(): ByteString = Buffer()
        .apply {
            seekPoints.forEach {
                write(it.toByteString())
            }
        }
        .readByteString()

    data class SeekPoint(
        val sampleNumber: Long,
        val offset: Long,
        val number: Short
    ) : CanWrite {
        override fun toByteString(): ByteString = Buffer()
            .apply {
                writeLong(sampleNumber)
                writeLong(offset)
                writeShort(number)
            }
            .readByteString()
    }

    companion object {
        fun create(source: Source, length: Int): MetadataBlockDataSeektable {
            val size = length / 18
            return MetadataBlockDataSeektable(
                seekPoints = (0 until size).map {
                    SeekPoint(
                        sampleNumber = source.readLong(),
                        offset = source.readLong(),
                        number = source.readShort()
                    )
                }
            )
        }
    }
}

/**
 * FLAC tags, without the framing bit.
 *
 * [Ogg Vorbis](https://www.xiph.org/vorbis/doc/v-comment.html)
 */
internal data class MetadataBlockDataVorbisComment(
    val vendorString: String,
    val userComments: List<String>
) : MetadataBlockData(BlockType.VorbisComment) {
    override fun toByteString(): ByteString = Buffer()
        .apply {
            val vendorByteString = vendorString.encodeToByteString()
            writeIntLe(vendorByteString.size)
            write(vendorByteString)

            writeIntLe(userComments.size)

            userComments.forEach { comment ->
                val commentByteString = comment.encodeToByteString()
                writeIntLe(commentByteString.size)
                write(commentByteString)
            }
        }
        .readByteString()

    companion object {
        fun create(source: Source): MetadataBlockDataVorbisComment {
            val vendorLength = source.readUIntLe().toLong()
            val vendorString = source.readString(vendorLength)

            val userCommentListLength = source.readUIntLe().toLong()
            val userComments = ArrayList<String>(userCommentListLength.toInt())

            for (i in 0 until userCommentListLength) {
                val userCommentLength = source.readUIntLe().toLong()
                val userComment = source.readString(userCommentLength)
                userComments.add(userComment)
            }

            return MetadataBlockDataVorbisComment(vendorString, userComments)
        }
    }
}

/**
 * TODO.
 */
internal data class MetadataBlockDataCuesheet(
    val byteString: ByteString
) : MetadataBlockData(BlockType.Cuesheet) {
    override fun toByteString(): ByteString = byteString

    companion object {
        fun create(
            source: Source,
            length: Int
        ): MetadataBlockDataCuesheet = MetadataBlockDataCuesheet(source.readByteString(length))
    }
}

internal data class MetadataBlockDataPicture(
    val pictureType: Int,
    val mediaType: String,
    val description: String,
    val width: Int,
    val height: Int,
    val colorDepth: Int,
    val colorsNumber: Int,
    val pictureData: ByteArray
) : MetadataBlockData(BlockType.Picture) {
    override fun toByteString(): ByteString = Buffer()
        .apply {
            writeInt(pictureType)
            val mediaTypeByteString = mediaType.encodeToByteString()
            writeInt(mediaTypeByteString.size)
            write(mediaTypeByteString)
            val descriptionByteString = description.encodeToByteString()
            writeInt(descriptionByteString.size)
            write(descriptionByteString)
            writeInt(width)
            writeInt(height)
            writeInt(colorDepth)
            writeInt(colorsNumber)
            writeInt(pictureData.size)
            write(pictureData)
        }
        .readByteString()

    fun toPicture(): Picture = Picture(
        pictureType = when (pictureType) {
            0 -> Picture.PictureType.Other
            1 -> Picture.PictureType.PngFileIcon32x32
            2 -> Picture.PictureType.GeneralFileIcon
            3 -> Picture.PictureType.FrontCover
            4 -> Picture.PictureType.BackCover
            5 -> Picture.PictureType.LinerNotesPage
            6 -> Picture.PictureType.MediaLabel
            7 -> Picture.PictureType.Lead
            8 -> Picture.PictureType.Artist
            9 -> Picture.PictureType.Conductor
            10 -> Picture.PictureType.Band
            11 -> Picture.PictureType.Composer
            12 -> Picture.PictureType.Lyricist
            13 -> Picture.PictureType.RecordingLocation
            14 -> Picture.PictureType.DuringRecording
            15 -> Picture.PictureType.DuringPerformance
            16 -> Picture.PictureType.MovieScreenCapture
            17 -> Picture.PictureType.BrightColoredFish
            18 -> Picture.PictureType.Illustration
            19 -> Picture.PictureType.BandLogo
            20 -> Picture.PictureType.PublisherLogotype
            else -> Picture.PictureType.Unknown
        },
        mediaType = mediaType,
        description = description,
        width = width,
        height = height,
        colorDepth = colorDepth,
        colorsNumber = colorsNumber,
        pictureData = pictureData
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MetadataBlockDataPicture

        if (pictureType != other.pictureType) return false
        if (mediaType != other.mediaType) return false
        if (description != other.description) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (colorDepth != other.colorDepth) return false
        if (colorsNumber != other.colorsNumber) return false
        if (!pictureData.contentEquals(other.pictureData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pictureType
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + colorDepth
        result = 31 * result + colorsNumber
        result = 31 * result + pictureData.contentHashCode()
        return result
    }

    companion object {
        fun create(source: Source): MetadataBlockDataPicture {
            val pictureType = source.readInt()
            val mediaTypeLength = source.readInt()
            val mediaType = source.readString(mediaTypeLength.toLong())
            val descriptionLength = source.readInt()
            val description = source.readString(descriptionLength.toLong())
            val width = source.readInt()
            val height = source.readInt()
            val colorDepth = source.readInt()
            val colorsNumber = source.readInt()
            val pictureDataLength = source.readInt()
            val pictureData = source.readByteArray(pictureDataLength)

            return MetadataBlockDataPicture(
                pictureType = pictureType,
                mediaType = mediaType,
                description = description,
                width = width,
                height = height,
                colorDepth = colorDepth,
                colorsNumber = colorsNumber,
                pictureData = pictureData
            )
        }
    }
}
