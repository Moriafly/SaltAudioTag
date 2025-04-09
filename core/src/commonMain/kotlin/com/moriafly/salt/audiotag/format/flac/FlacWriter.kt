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

import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.WriteOperation
import com.moriafly.salt.audiotag.rw.Writer
import com.moriafly.salt.audiotag.util.SystemFileSystemUtil
import com.mroiafly.salt.audiotag.BuildKonfig
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.write

class FlacWriter : Writer {
    private val metadataBlocks = mutableListOf<MetadataBlock>()

    @UnstableSaltAudioTagApi
    private fun getWriteMetadataBlockDatas(
        operation: WriteOperation.AllMetadata?
    ): List<MetadataBlockData> {
        val writeMetadataBlockDatas = metadataBlocks.map { it.data }.toMutableList()

        if (operation == null) {
            return writeMetadataBlockDatas
        }

        val metadatas = operation.metadatas

        val vorbisCommentIndex = writeMetadataBlockDatas.indexOfFirst {
            it is MetadataBlockDataVorbisComment
        }
        val vorbisComment = writeMetadataBlockDatas
            .find { it is MetadataBlockDataVorbisComment }

        if (vorbisComment != null) {
            if (metadatas.isEmpty()) {
                writeMetadataBlockDatas.remove(vorbisComment)
            } else {
                // New VorbisComment
                val newVorbisComment = MetadataBlockDataVorbisComment(
                    vendorString = (
                        vorbisComment as MetadataBlockDataVorbisComment
                    ).vendorString,
                    userComments = metadatas.map { it.toFlacUserComment() }
                )
                writeMetadataBlockDatas[vorbisCommentIndex] = newVorbisComment
            }
        } else {
            if (metadatas.isEmpty()) {
                // Do nothing
            } else {
                // New VorbisComment
                val newVorbisComment = MetadataBlockDataVorbisComment(
                    vendorString = VENDOR_STRING,
                    userComments = metadatas.map { it.toFlacUserComment() }
                )
                writeMetadataBlockDatas.add(newVorbisComment)
            }
        }

        return writeMetadataBlockDatas
    }

    @UnstableSaltAudioTagApi
    override fun write(src: Path, dst: Path, vararg operation: WriteOperation) {
        val tempFilePath = SystemFileSystemUtil.tempFilePath()
        try {
            SystemFileSystemUtil.copy(src, tempFilePath)

            SystemFileSystem.source(src).buffered().use { source ->
                // Read and create new metadata blocks
                FlacSignature(source)

                var metadataBlockHeader: MetadataBlockHeader
                do {
                    metadataBlockHeader = MetadataBlockHeader.create(source)

                    val metadataBlockData = when (metadataBlockHeader.blockType) {
                        BlockType.Streaminfo ->
                            MetadataBlockDataStreaminfo.create(source)
                        BlockType.Padding ->
                            MetadataBlockDataPadding.create(source, metadataBlockHeader.length)
                        BlockType.Application ->
                            MetadataBlockDataApplication.create(source, metadataBlockHeader.length)
                        BlockType.Seektable ->
                            MetadataBlockDataSeektable.create(source, metadataBlockHeader.length)
                        BlockType.VorbisComment ->
                            MetadataBlockDataVorbisComment.create(source)
                        BlockType.Cuesheet ->
                            MetadataBlockDataCuesheet.create(source, metadataBlockHeader.length)
                        BlockType.Picture ->
                            MetadataBlockDataPicture.create(source)
                        else ->
                            error(
                                "Unsupported metadata block type: ${metadataBlockHeader.blockType}"
                            )
                    }

                    metadataBlocks.add(
                        MetadataBlock(
                            header = metadataBlockHeader,
                            data = metadataBlockData
                        )
                    )
                } while (!metadataBlockHeader.isLastMetadataBlock)

                val operationAllMetadata = operation
                    .find { it is WriteOperation.AllMetadata }
                    as WriteOperation.AllMetadata?

                val writeMetadataBlockDatas = getWriteMetadataBlockDatas(operationAllMetadata)

                // Write to temp
                SystemFileSystem.sink(tempFilePath).buffered().use { sink ->
                    sink.write(FlacSignature.HEADER)
                    writeMetadataBlockDatas.forEachIndexed { index, data ->
                        val dataByteString = data.toByteString()

                        sink.write(
                            MetadataBlockHeader(
                                isLastMetadataBlock = index == writeMetadataBlockDatas.lastIndex,
                                blockType = data.blockType,
                                length = dataByteString.size
                            ).toByteString()
                        )
                        sink.write(dataByteString)
                    }

                    sink.transferFrom(source)
                }

                // Atomic move
                SystemFileSystem.atomicMove(tempFilePath, dst)
            }
        } finally {
            if (SystemFileSystem.exists(tempFilePath)) {
                SystemFileSystem.delete(tempFilePath)
            }
        }
    }

    companion object {
        private val VENDOR_STRING = "Salt Audio Tag ${BuildKonfig.version}"
    }
}
