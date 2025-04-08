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

package com.moriafly.salt.audiotag.format.flac

import com.moriafly.salt.audiotag.format.BlockType
import com.moriafly.salt.audiotag.format.FlacSignature
import com.moriafly.salt.audiotag.format.MetadataBlockDataPicture
import com.moriafly.salt.audiotag.format.MetadataBlockDataStreaminfo
import com.moriafly.salt.audiotag.format.MetadataBlockDataVorbisComment
import com.moriafly.salt.audiotag.format.MetadataBlockHeader
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.Reader
import com.moriafly.salt.audiotag.rw.data.AudioTag
import com.moriafly.salt.audiotag.rw.data.Metadata
import com.moriafly.salt.audiotag.rw.data.Picture
import com.moriafly.salt.audiotag.rw.data.Streaminfo
import kotlinx.io.Source

class FlacReader : Reader {
    override fun read(
        source: Source,
        strategy: ReadStrategy
    ): AudioTag {
        var streaminfo: Streaminfo? = null
        var metadatas: List<Metadata>? = null
        var pictures: MutableList<Picture>? = null

        FlacSignature(source)

        var metadataBlockHeader: MetadataBlockHeader
        do {
            metadataBlockHeader = MetadataBlockHeader.create(source)

            when (metadataBlockHeader.blockType) {
                BlockType.Streaminfo if strategy.streaminfo -> {
                    MetadataBlockDataStreaminfo.create(source).also {
                        streaminfo = Streaminfo(
                            sampleRate = it.sampleRate,
                            channelCount = it.channelCount,
                            bits = it.bits,
                            sampleCount = it.sampleCount
                        )
                    }
                }

                BlockType.VorbisComment if strategy.metadatas -> {
                    MetadataBlockDataVorbisComment.create(source).also {
                        metadatas = it.userComments.mapNotNull { userComment ->
                            val parts = userComment.split('=', limit = 2)
                            if (parts.size == 2) {
                                val rawField = parts[0].trim()
                                val value = parts[1].trim()
                                val normalizedField = rawField.uppercase()

                                Metadata(normalizedField, value)
                            } else {
                                null
                            }
                        }
                    }
                }

                BlockType.Picture if strategy.pictures -> {
                    MetadataBlockDataPicture.create(source).also {
                        if (pictures == null) {
                            pictures = mutableListOf()
                        } else {
                            pictures.add(it.toPicture())
                        }
                    }
                }

                else -> source.skip(metadataBlockHeader.length.toLong())
            }
        } while (!metadataBlockHeader.isLastMetadataBlock)

        return AudioTag(
            streaminfo = streaminfo,
            metadatas = metadatas,
            pictures = pictures
        )
    }
}
