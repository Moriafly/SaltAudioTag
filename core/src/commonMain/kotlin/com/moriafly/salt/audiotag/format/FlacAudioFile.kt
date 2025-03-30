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
import com.moriafly.salt.audiotag.rw.ReadStrategy
import kotlinx.io.Source

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
    readStrategy: ReadStrategy
) : AudioFile() {
    private var audioProperties: AudioProperties? = null
    private val metadataMap = mutableMapOf<MetadataKey<*>, MutableList<*>>()
    private val audioPictures = mutableListOf<AudioPicture>()

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
        LazyMetadataKey.FrontCover -> {
            @Suppress("UNCHECKED_CAST")
            audioPictures
                .filter { it.pictureType == AudioPicture.PictureType.FrontCover }
                .map { it.pictureData }
                .toList() as List<T>
        }
    }

    override fun close() {
        source.close()
    }

    init {
        Signature(source)

        var metadataBlockHeader: MetadataBlockHeader
        do {
            metadataBlockHeader = MetadataBlockHeader(source)

            println(
                "BlockType = ${metadataBlockHeader.blockType}"
            )

            when {
                metadataBlockHeader.blockType == MetadataBlockHeader.BLOCK_TYPE_STREAMINFO -> {
                    val streaminfo = MetadataBlockStreaminfo(source)
                    audioProperties = AudioProperties(
                        sampleRate = streaminfo.sampleRate,
                        channelCount = streaminfo.channelCount,
                        bits = streaminfo.bits,
                        sampleCount = streaminfo.sampleCount
                    )
                }

                metadataBlockHeader.blockType == MetadataBlockHeader.BLOCK_TYPE_VORBIS_COMMENT &&
                    readStrategy.canReadMetadata() -> {
                    val vorbisComment = VorbisComment(source)

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

                metadataBlockHeader.blockType == MetadataBlockHeader.BLOCK_TYPE_PICTURE &&
                    readStrategy.canReadLazyMetadata() -> {
                    val picture = Picture(source)
                    audioPictures.add(picture.toAudioPicture())
                }

                else -> {
                    source.skip(metadataBlockHeader.length.toLong())
                }
            }
        } while (!metadataBlockHeader.isLastMetadataBlock)
    }
}
