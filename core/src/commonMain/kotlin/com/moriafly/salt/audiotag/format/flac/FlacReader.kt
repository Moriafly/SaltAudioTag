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
import kotlinx.io.readByteArray

class FlacReader : Reader {
    override fun read(source: Source, strategy: ReadStrategy): AudioTag {
        var streaminfo: Streaminfo? = null
        var metadatas: List<Metadata>? = null
        var pictures: MutableList<Picture>? = null

        var metadataBlockDataStreaminfo: MetadataBlockDataStreaminfo? = null

        // Global offset tracker for lazy loading support
        // Assumes source starts at 0. If reading from a slice, caller needs to adjust offset.
        var currentGlobalOffset = 0L
        var fileLevelMetadataLength = 0L

        // Check FLAC signature
        FlacSignature(source)
        currentGlobalOffset += 4
        fileLevelMetadataLength += 4

        var metadataBlockHeader: MetadataBlockHeader

        // Smart Mode State
        var bestPicture: Picture? = null
        var currentMaxPriority = -1

        do {
            metadataBlockHeader = MetadataBlockHeader.create(source)

            // Calculate offsets
            // The block content starts after the 4-byte header
            val blockStartOffset = currentGlobalOffset
            val blockContentOffset = blockStartOffset + 4

            currentGlobalOffset += 4
            fileLevelMetadataLength += 4

            val blockLength = metadataBlockHeader.length.toLong()

            when (metadataBlockHeader.blockType) {
                BlockType.Streaminfo if strategy.streaminfo -> {
                    metadataBlockDataStreaminfo = MetadataBlockDataStreaminfo.create(source)
                }

                BlockType.VorbisComment if strategy.metadatas -> {
                    MetadataBlockDataVorbisComment.create(source).also { block ->
                        metadatas = block.userComments.mapNotNull { userComment ->
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

                BlockType.Picture if strategy.pictureReadMode !is PictureReadMode.None -> {
                    // 1. Read Type (4 bytes)
                    val pictureTypeInt = source.readInt()
                    val pictureType = MetadataBlockDataPicture.mapPictureType(pictureTypeInt)
                    val remainingBlockLength = blockLength - 4

                    // 2. Check if we should process this block based on Filter Strategy
                    val shouldProcess = when (val mode = strategy.pictureReadMode) {
                        is PictureReadMode.All -> true
                        is PictureReadMode.Custom -> mode.filter(pictureType)
                        is PictureReadMode.SmartFrontCover -> {
                            val priority = getCoverPriority(pictureType)
                            priority > currentMaxPriority
                        }
                        else -> false
                    }

                    if (shouldProcess) {
                        // 3. Read Header (Metadata only, no binary data)
                        // This moves the stream cursor to the start of picture data
                        val header = MetadataBlockDataPicture.readHeader(source)

                        // Calculate absolute offset of the image binary data
                        // blockContentOffset + 4 (Type) + bytes consumed by header
                        val absoluteDataOffset = blockContentOffset + 4 + header.bytesRead

                        // 4. Determine Data Loading Strategy
                        val pictureData: ByteArray

                        if (strategy.loadPictureBinary) {
                            // Eager Mode: Read the full binary data into memory
                            pictureData = source.readByteArray(header.dataLength)
                        } else {
                            // Lazy Mode: Skip the data, keep array empty
                            source.skip(header.dataLength.toLong())
                            pictureData = byteArrayOf()
                        }

                        val picture = Picture(
                            pictureType = pictureType,
                            mediaType = header.mediaType,
                            description = header.description,
                            width = header.width,
                            height = header.height,
                            colorDepth = header.colorDepth,
                            colorsNumber = header.colorsNumber,
                            pictureData = pictureData,
                            globalFileOffset = absoluteDataOffset,
                            dataLength = header.dataLength
                        )

                        // 5. Store the picture
                        if (strategy.pictureReadMode is PictureReadMode.SmartFrontCover) {
                            bestPicture = picture
                            currentMaxPriority = getCoverPriority(pictureType)
                        } else {
                            if (pictures == null) pictures = mutableListOf()
                            pictures.add(picture)
                        }
                    } else {
                        // Filter rejected this picture (or lower priority in Smart mode)
                        // Skip the entire remaining block content efficiently
                        source.skip(remainingBlockLength)
                    }
                }

                else -> source.skip(blockLength)
            }

            // Advance global offset by the body length
            currentGlobalOffset += blockLength
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

        // Apply Smart mode result
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
