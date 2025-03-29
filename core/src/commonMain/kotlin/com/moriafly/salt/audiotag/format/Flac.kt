@file:Suppress(
    "unused",
    "ktlint:standard:filename",
    "MemberVisibilityCanBePrivate",
    "SpellCheckingInspection"
)

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

package com.moriafly.salt.audiotag.format

import kotlinx.io.Source
import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.toHexString
import kotlinx.io.readByteString

internal class Signature(
    source: Source
) {
    init {
        check(source.readByteString(4) == HEADER) {
            "Invalid FLAC header"
        }
    }

    companion object {
        /**
         * A FLAC bitstream consists of the fLaC (i.e., 0x664C6143) marker at the beginning of the
         * stream.
         */
        private val HEADER = ByteString(0x66, 0x4C, 0x61, 0x43)
    }
}

internal class MetadataBlockHeader(
    source: Source
) {
    val isLastMetadataBlock: Boolean
    val blockType: Int
    val length: Int

    @Suppress("SpellCheckingInspection")
    companion object {
        const val BLOCK_TYPE_STREAMINFO = 0
        const val BLOCK_TYPE_PADDING = 1
        const val BLOCK_TYPE_APPLICATION = 2
        const val BLOCK_TYPE_SEEKTABLE = 3
        const val BLOCK_TYPE_VORBIS_COMMENT = 4
        const val BLOCK_TYPE_CUESHEET = 5
        const val BLOCK_TYPE_PICTURE = 6
        const val BLOCK_TYPE_INVALID = 127
    }

    init {
        val byteString = source.readByteString(4)

        isLastMetadataBlock = (byteString[0].toInt() and 0b10000000 shr 7) == 1
        blockType = byteString[0].toInt() and 0b01111111
        length = (byteString[1].toInt() and 0xFF shl 16) or
            (byteString[2].toInt() and 0xFF shl 8) or
            (byteString[3].toInt() and 0xFF)
    }
}

/**
 * The streaminfo metadata block has information about the whole stream, such as sample rate,
 * number of channels, total number of samples, etc. It MUST be present as the first metadata block
 * in the stream. Other metadata blocks MAY follow. There MUST be no more than one streaminfo
 * metadata block per FLAC stream.
 */
internal class MetadataBlockStreaminfo(
    source: Source
) {
    /**
     * Samples.
     */
    val minBlockSize: Int

    /**
     * Samples.
     */
    val maxBlockSize: Int

    /**
     * Bytes.
     */
    val minFrameSize: Int

    /**
     * Bytes.
     */
    val maxFrameSize: Int

    /**
     * Max 655350 Hz.
     */
    val sampleRate: Int

    /**
     * 2 to 8.
     */
    val channelCount: Int

    /**
     * Bits per sample, 4 to 32.
     */
    val bits: Int

    val sampleCount: Long

    /**
     * 	MD5 checksum of the unencoded audio data. This allows the decoder to determine if an error
     * 	exists in the audio data even when, despite the error, the bitstream itself is valid. A
     * 	value of 0 signifies that the value is not known.
     *
     * 	16 bytes (128 bits) HASH.
     */
    val unencodedAudioDataMd5Checksum: String

    init {
        val byteString = source.readByteString(34)

        minBlockSize = (byteString[0].toInt() and 0xFF shl 8) or
            (byteString[1].toInt() and 0xFF)
        maxBlockSize = (byteString[2].toInt() and 0xFF shl 8) or
            (byteString[3].toInt() and 0xFF)

        @Suppress("ConvertTwoComparisonsToRangeCheck")
        require(
            16 <= minBlockSize &&
                minBlockSize <= maxBlockSize &&
                maxBlockSize <= 65535
        ) {
            "The minimum block size and the maximum block size MUST be in the 16-65535 range. " +
                "The minimum block size MUST be equal to or less than the maximum block size"
        }

        minFrameSize = (byteString[4].toInt() and 0xFF shl 16) or
            (byteString[5].toInt() and 0xFF shl 8) or
            (byteString[6].toInt() and 0xFF)
        maxFrameSize = (byteString[7].toInt() and 0xFF shl 16) or
            (byteString[8].toInt() and 0xFF shl 8) or
            (byteString[9].toInt() and 0xFF)
        sampleRate = (byteString[10].toInt() and 0xFF shl 12) or
            (byteString[11].toInt() and 0xFF shl 4) or
            (byteString[12].toInt() and 0xFF shr 4)
        channelCount = (byteString[12].toInt() and 0x0F shr 1) + 1
        bits =
            ((byteString[12].toInt() and 0x1 shl 4) or (byteString[13].toInt() and 0xF0 shr 4)) + 1
        sampleCount = (byteString[13].toLong() and 0x0F shl 32) or
            (byteString[14].toLong() and 0xFF shl 24) or
            (byteString[15].toLong() and 0xFF shl 16) or
            (byteString[16].toLong() and 0xFF shl 8) or
            (byteString[17].toLong() and 0xFF)
        @OptIn(ExperimentalStdlibApi::class)
        unencodedAudioDataMd5Checksum = byteString.substring(18, 34).toHexString()
    }

    override fun toString(): String =
        "MetadataBlockStreamInfo(minBlockSize=$minBlockSize, maxBlockSize=$maxBlockSize, " +
            "minFrameSize=$minFrameSize, maxFrameSize=$maxFrameSize, sampleRate=$sampleRate, " +
            "channelCount=$channelCount, bits=$bits, sampleCount=$sampleCount, " +
            "unencodedAudioDataMd5Checksum='$unencodedAudioDataMd5Checksum')"
}
