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

import com.moriafly.salt.audiotag.rw.PictureReadMode
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.Reader
import com.moriafly.salt.audiotag.rw.data.AudioTag
import com.moriafly.salt.audiotag.rw.data.Metadata
import com.moriafly.salt.audiotag.rw.data.Picture
import com.moriafly.salt.audiotag.rw.data.Picture.PictureType
import com.moriafly.salt.audiotag.rw.data.Streaminfo
import kotlinx.io.Source

class FlacReader : Reader {
    override fun read(source: Source, strategy: ReadStrategy): AudioTag {
        var streaminfo: Streaminfo? = null
        var metadatas: List<Metadata>? = null
        var pictures: MutableList<Picture>? = null

        var metadataBlockDataStreaminfo: MetadataBlockDataStreaminfo? = null
        var fileLevelMetadataLength = 0L

        // Check FLAC signature
        FlacSignature(source)
        fileLevelMetadataLength += 4

        var metadataBlockHeader: MetadataBlockHeader

        // Variables for SmartFrontCover mode
        var bestPicture: Picture? = null
        var currentMaxPriority = -1

        do {
            metadataBlockHeader = MetadataBlockHeader.create(source)
            fileLevelMetadataLength += 4

            // The length of the block data excluding the header
            val blockLength = metadataBlockHeader.length.toLong()

            when (metadataBlockHeader.blockType) {
                BlockType.Streaminfo if strategy.streaminfo -> {
                    metadataBlockDataStreaminfo = MetadataBlockDataStreaminfo.create(source)
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

                BlockType.Picture if strategy.pictureReadMode != PictureReadMode.None -> {
                    // Read Picture Type (4 bytes) to determine priority
                    // Note: blockLength includes these 4 bytes
                    val pictureTypeInt = source.readInt()

                    val pictureType = MetadataBlockDataPicture.mapPictureType(pictureTypeInt)

                    // Calculate remaining length to read or skip
                    val remainingLength = blockLength - 4

                    when (val mode = strategy.pictureReadMode) {
                        is PictureReadMode.All -> {
                            // Delegate to the overloaded create method
                            val pictureBlock = MetadataBlockDataPicture.create(
                                source,
                                pictureTypeInt
                            )
                            if (pictures == null) pictures = mutableListOf()
                            pictures.add(pictureBlock.toPicture())
                        }

                        is PictureReadMode.Custom -> {
                            if (mode.filter(pictureType)) {
                                val pictureBlock = MetadataBlockDataPicture.create(
                                    source,
                                    pictureTypeInt
                                )
                                if (pictures == null) pictures = mutableListOf()
                                pictures.add(pictureBlock.toPicture())
                            } else {
                                source.skip(remainingLength)
                            }
                        }

                        is PictureReadMode.SmartFrontCover -> {
                            val priority = getCoverPriority(pictureType)

                            // Competition Logic:
                            // If the new picture has higher priority than what we have, read it.
                            // Otherwise, skip it to save memory.
                            if (priority > currentMaxPriority) {
                                val pictureBlock = MetadataBlockDataPicture.create(
                                    source,
                                    pictureTypeInt
                                )
                                bestPicture = pictureBlock.toPicture()
                                currentMaxPriority = priority
                            } else {
                                source.skip(remainingLength)
                            }
                        }

                        else -> {
                            // Do nothing
                        }
                    }
                }

                else -> source.skip(blockLength)
            }
            fileLevelMetadataLength += metadataBlockHeader.length
        } while (!metadataBlockHeader.isLastMetadataBlock)

        // Assemble results
        if (metadataBlockDataStreaminfo != null) {
            streaminfo = Streaminfo(
                sampleRate = metadataBlockDataStreaminfo.sampleRate,
                channelCount = metadataBlockDataStreaminfo.channelCount,
                bits = metadataBlockDataStreaminfo.bits,
                sampleCount = metadataBlockDataStreaminfo.sampleCount,
                fileLevelMetadataLength = fileLevelMetadataLength
            )
        }

        // Assign the best picture found in Smart mode
        if (strategy.pictureReadMode is PictureReadMode.SmartFrontCover && bestPicture != null) {
            pictures = mutableListOf(bestPicture)
        }

        return AudioTag(
            streaminfo = streaminfo,
            metadatas = metadatas,
            pictures = pictures
        )
    }

    /**
     * Determines priority for SmartFrontCover mode.
     * Higher value means higher priority.
     */
    private fun getCoverPriority(type: PictureType): Int = when (type) {
        PictureType.FrontCover -> 100
        PictureType.BackCover -> 80
        PictureType.GeneralFileIcon -> 60
        // All other types serve as a fallback with low priority
        else -> 10
    }
}
