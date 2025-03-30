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
import com.moriafly.salt.audiotag.rw.RwStrategy
import com.moriafly.salt.audiotag.rw.WriteOperation
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
    private val metadataMap = mutableMapOf<MetadataKey<*>, MutableList<*>>()
    private val audioPictures = mutableListOf<AudioPicture>()
    private val metadataBlockHeaders = mutableListOf<FlacMetadataBlockHeader>()

    private var streaminfo: FlacMetadataBlockStreaminfo? = null
    private var audioProperties: AudioProperties? = null

    private fun <T> putMetadata(key: MetadataKey<T>, value: T) {
        val list = metadataMap.getOrPut(key) { mutableListOf<T>() }
        @Suppress("UNCHECKED_CAST")
        (list as MutableList<T>).add(value)
    }

    override fun getAudioProperties(): AudioProperties? = audioProperties

    override fun <T> getMetadata(key: MetadataKey<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return metadataMap[key]
            ?.let {
                it as List<T>
            }
            ?: emptyList()
    }

    override fun getAllMetadata(): Map<MetadataKey<*>, List<*>> = metadataMap

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

        require(streaminfo != null) {
            "The streaminfo is null."
        }

        val sink = SystemFileSystem.sink(path).buffered()
        sink.write(FlacSignature.HEADER)

        metadataBlockHeaders.forEach { metadataBlockHeader ->
            if (metadataBlockHeader.blockType == FlacMetadataBlockHeader.BLOCK_TYPE_STREAMINFO) {
                sink.write(metadataBlockHeader.reWrite(true))
                sink.write(streaminfo!!.byteString)
            }
        }

        sink.transferFrom(source)
    }

    override fun close() {
        source.close()
    }

    init {
        FlacSignature(source)

        var metadataBlockHeader: FlacMetadataBlockHeader
        do {
            metadataBlockHeader = FlacMetadataBlockHeader(source)
            metadataBlockHeaders.add(metadataBlockHeader)

            println(
                "BlockType = ${metadataBlockHeader.blockType}"
            )

            when {
                metadataBlockHeader.blockType == FlacMetadataBlockHeader.BLOCK_TYPE_STREAMINFO -> {
                    streaminfo = FlacMetadataBlockStreaminfo(source).also {
                        audioProperties = AudioProperties(
                            sampleRate = it.sampleRate,
                            channelCount = it.channelCount,
                            bits = it.bits,
                            sampleCount = it.sampleCount
                        )
                    }
                }

                metadataBlockHeader.blockType
                    == FlacMetadataBlockHeader.BLOCK_TYPE_VORBIS_COMMENT &&
                    rwStrategy.canReadMetadata() -> {
                    val vorbisComment = FlacVorbisComment(source)

                    val availableMetadataKeys = MetadataKey.OggVorbis +
                        listOf(
                            MetadataKey.Lyrics
                        )

                    val fieldToKeyMap = availableMetadataKeys.associateBy { it.field }

                    vorbisComment.userComments.forEach { userComment ->
                        val parts = userComment.split('=', limit = 2)
                        if (parts.size == 2) {
                            val rawField = parts[0].trim()
                            val value = parts[1].trim()
                            val normalizedField = rawField.uppercase()

                            fieldToKeyMap[normalizedField]
                                ?.let { metadataKey ->
                                    putMetadata(metadataKey, value)
                                }
                                ?: run {
                                    putMetadata(MetadataKey.custom(normalizedField), value)
                                }
                        }
                    }
                }

                metadataBlockHeader.blockType == FlacMetadataBlockHeader.BLOCK_TYPE_PICTURE &&
                    rwStrategy.canReadLazyMetadata() -> {
                    val flacPicture = FlacPicture(source)
                    audioPictures.add(flacPicture.toAudioPicture())
                }

                else -> {
                    source.skip(metadataBlockHeader.length.toLong())
                }
            }
        } while (!metadataBlockHeader.isLastMetadataBlock)
    }
}
