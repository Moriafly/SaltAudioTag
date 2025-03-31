@file:Suppress("unused", "MemberVisibilityCanBePrivate")

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

import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.AudioFile
import com.moriafly.salt.audiotag.rw.AudioPicture
import com.moriafly.salt.audiotag.rw.AudioProperties
import com.moriafly.salt.audiotag.rw.LazyMetadataKey
import com.moriafly.salt.audiotag.rw.MetadataKey
import com.moriafly.salt.audiotag.rw.MetadataKeyValue
import com.moriafly.salt.audiotag.rw.RwStrategy
import com.moriafly.salt.audiotag.rw.WriteOperation
import com.mroiafly.salt.audiotag.BuildKonfig
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.write

/**
 * # FLAC
 *
 * [RFC 9639](https://datatracker.ietf.org/doc/rfc9639/)
 *
 * @author Moriafly 2025/3/29
 */
@OptIn(UnstableSaltAudioTagApi::class)
class FlacAudioFile(
    private val source: Source,
    private val rwStrategy: RwStrategy
) : AudioFile() {
    private val metadataList = mutableListOf<MetadataKeyValue>()
    private val metadataMap = mutableMapOf<MetadataKey, List<String>>()
    private val audioPictures = mutableListOf<AudioPicture>()
    private val metadataBlocks = mutableListOf<MetadataBlock>()

    private var audioProperties: AudioProperties? = null

    override fun getAudioProperties(): AudioProperties? = audioProperties

    override fun getMetadata(key: MetadataKey): List<String> = metadataMap[key] ?: emptyList()

    override fun getAllMetadata(): List<MetadataKeyValue> = metadataList

    override fun <T> getLazyMetadata(key: LazyMetadataKey<T>): List<T> = when (key) {
        is LazyMetadataKey.Picture -> {
            @Suppress("UNCHECKED_CAST")
            audioPictures
                .filter { it.pictureType == key.pictureType }
                .toList() as List<T>
        }
    }

    @UnstableSaltAudioTagApi
    override fun write(path: Path, vararg operation: WriteOperation) {
        require(rwStrategy == RwStrategy.ReadWriteAll) {
            "The rwStrategy does not support writing."
        }

        val operations = operation.toList()

        val sink = SystemFileSystem.sink(path).buffered()
        sink.write(FlacSignature.HEADER)

        val writeMetadataBlockDataList = metadataBlocks.map { it.data }.toMutableList()

        val operationAllMetadata = operations
            .find { it is WriteOperation.AllMetadata }
            as WriteOperation.AllMetadata?
        if (operationAllMetadata != null) {
            val metadataList = operationAllMetadata.metadataList

            val vorbisCommentIndex = writeMetadataBlockDataList.indexOfFirst {
                it is MetadataBlockDataVorbisComment
            }
            val vorbisComment = writeMetadataBlockDataList
                .find { it is MetadataBlockDataVorbisComment }

            if (vorbisComment != null) {
                if (metadataList.isEmpty()) {
                    writeMetadataBlockDataList.remove(vorbisComment)
                } else {
                    // New VorbisComment.
                    val newVorbisComment = MetadataBlockDataVorbisComment(
                        vendorString = (
                            vorbisComment as MetadataBlockDataVorbisComment
                        ).vendorString,
                        userComments = metadataList.map { it.toFlacUserComment() }
                    )
                    writeMetadataBlockDataList[vorbisCommentIndex] = newVorbisComment
                }
            } else {
                if (metadataList.isEmpty()) {
                    // Do nothing.
                } else {
                    // New VorbisComment.
                    val newVorbisComment = MetadataBlockDataVorbisComment(
                        vendorString = "Salt Audio Tag ${BuildKonfig.version}",
                        userComments = metadataList.map { it.toFlacUserComment() }
                    )
                    writeMetadataBlockDataList.add(newVorbisComment)
                }
            }
        }

        writeMetadataBlockDataList.forEachIndexed { index, data ->
            val dataByteString = data.toByteString()

            println(
                "Write MetadataBlockHeader" +
                    "index = $index, blockType = ${data.blockType}, " +
                    "size = ${dataByteString.size}, " +
                    "lastIndex = ${index == writeMetadataBlockDataList.lastIndex}"
            )
            sink.write(
                MetadataBlockHeader(
                    isLastMetadataBlock = index == writeMetadataBlockDataList.lastIndex,
                    blockType = data.blockType,
                    length = dataByteString.size
                ).toByteString()
            )
            sink.write(dataByteString)
        }

        sink.transferFrom(source)

        // Must close the sink.
        sink.close()
    }

    override fun close() {
        source.close()
    }

    init {
        FlacSignature(source)

        var metadataBlockHeader: MetadataBlockHeader
        do {
            metadataBlockHeader = MetadataBlockHeader.create(source)

            val metadataBlockData: MetadataBlockData = when {
                metadataBlockHeader.blockType == BlockType.Streaminfo -> {
                    MetadataBlockDataStreaminfo.create(source).also {
                        audioProperties = AudioProperties(
                            sampleRate = it.sampleRate,
                            channelCount = it.channelCount,
                            bits = it.bits,
                            sampleCount = it.sampleCount
                        )
                    }
                }

                metadataBlockHeader.blockType == BlockType.Padding -> {
                    MetadataBlockDataPadding.create(source, metadataBlockHeader.length)
                }

                metadataBlockHeader.blockType == BlockType.Application -> {
                    MetadataBlockDataApplication.create(source, metadataBlockHeader.length)
                }

                metadataBlockHeader.blockType == BlockType.Seektable -> {
                    MetadataBlockDataSeektable.create(source, metadataBlockHeader.length)
                }

                metadataBlockHeader.blockType == BlockType.VorbisComment &&
                    rwStrategy.canReadMetadata() -> {
                    MetadataBlockDataVorbisComment.create(source).also {
                        val availableMetadataKeys = MetadataKey.OggVorbis +
                            listOf(
                                MetadataKey.Lyrics
                            )

                        val fieldToKeyMap = availableMetadataKeys.associateBy { key -> key.field }

                        it.userComments.forEach { userComment ->
                            val parts = userComment.split('=', limit = 2)
                            if (parts.size == 2) {
                                val rawField = parts[0].trim()
                                val value = parts[1].trim()
                                val normalizedField = rawField.uppercase()

                                fieldToKeyMap[normalizedField]
                                    ?.let { metadataKey ->
                                        metadataList.add(MetadataKeyValue(metadataKey, value))
                                    }
                                    ?: run {
                                        val metadataKey = MetadataKey
                                            .custom<String>(normalizedField)
                                        metadataList.add(MetadataKeyValue(metadataKey, value))
                                    }
                            }
                        }

                        val map = metadataList
                            .groupBy { metadataKeyValue -> metadataKeyValue.key }
                            .mapValues { (_, group) ->
                                group.map { metadataKeyValue -> metadataKeyValue.value }
                            }
                        metadataMap.putAll(map)
                    }
                }

                metadataBlockHeader.blockType == BlockType.Cuesheet -> {
                    MetadataBlockDataCuesheet.create(source, metadataBlockHeader.length)
                }

                metadataBlockHeader.blockType == BlockType.Picture &&
                    rwStrategy.canReadLazyMetadata() -> {
                    MetadataBlockDataPicture.create(source).also {
                        audioPictures.add(it.toAudioPicture())
                    }
                }

                else -> error("Unsupported metadata block type: ${metadataBlockHeader.blockType}.")
            }

            metadataBlocks.add(
                MetadataBlock(
                    header = metadataBlockHeader,
                    data = metadataBlockData
                )
            )
        } while (!metadataBlockHeader.isLastMetadataBlock)
    }
}
