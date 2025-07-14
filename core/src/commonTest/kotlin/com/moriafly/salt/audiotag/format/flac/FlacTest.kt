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

import com.moriafly.salt.audiotag.SaltAudioTag
import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.WriteOperation
import com.moriafly.salt.audiotag.rw.data.Metadata
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.test.Test

@OptIn(UnstableSaltAudioTagApi::class)
class FlacTest {
    private val path = Path("C:\\Users\\moria\\Music\\Pig小优 - 画舫烟中浅.flac")

    @Test
    fun testRead() {
        val audioTag = SaltAudioTag.read(
            path = path,
            extension = "flac",
            strategy = ReadStrategy.All
        ).getOrThrow()

        audioTag.metadatas?.forEach { metadata ->
            println("key = ${metadata.key} value = ${metadata.value}")
        }
    }

    @Test
    fun testWrite() {
//        val outputPath = Path("C:\\Users\\moria\\Desktop\\G.E.M.邓紫棋 - 桃花诺_output.flac")
//        val audioFile = SaltAudioTag.create(
//            path = path,
//            rwStrategy = RwStrategy.ReadWriteAll
//        )
        // audioFile.write(outputPath)
    }

    @Test
    fun testWriteAddArtist() {
        val result = SystemFileSystem.source(path).buffered().use {
            SaltAudioTag.read(
                source = it,
                extension = "flac",
                strategy = ReadStrategy.All
            )
        }

        val audioTag = result.getOrThrow()

        val outputPath = Path("C:\\Users\\moria\\Desktop\\G.E.M.邓紫棋 - 桃花诺_output.flac")

        if (audioTag.metadatas != null) {
            SaltAudioTag.write(
                src = path,
                dst = outputPath,
                extension = "flac",
                WriteOperation.AllMetadata.create(
                    metadatas = audioTag.metadatas + Metadata(
                        key = "ARTIST",
                        value = "Salt Audio Tag"
                    )
                )
            )
        }
    }

    @Test
    fun testWriteRemoveAllMetadata() {
//        val outputPath = Path("C:\\Users\\moria\\Desktop\\G.E.M.邓紫棋 - 桃花诺_output.flac")
//        val audioFile = SaltAudioTag.create(
//            path = path,
//            rwStrategy = RwStrategy.ReadWriteAll
//        )
//        audioFile.write(
//            input = path,
//            output = outputPath,
//            WriteOperation.AllMetadata(
//                metadatas = emptyList()
//            )
//        )
    }

    @Test
    fun readPictures() {
        val result = SystemFileSystem.source(path).buffered().use {
            SaltAudioTag.read(
                source = it,
                extension = "flac",
                strategy = ReadStrategy.OnlyPicture
            )
        }

        val audioTag = result.getOrThrow()

        if (audioTag.pictures != null) {
            if (audioTag.pictures.isEmpty()) {
                println("Pictures is empty")
            }

            audioTag.pictures.forEach { picture ->
                println(
                    """
                    pictureType = ${picture.pictureType}
                    mediaType = ${picture.mediaType}
                    description = ${picture.description}
                    width = ${picture.width}
                    height = ${picture.height}
                    colorDepth = ${picture.colorDepth}
                    colorsNumber = ${picture.colorsNumber}
                    pictureData.size = ${picture.pictureData.size} Bytes
                    """.trimIndent()
                )
            }
        } else {
            println("Pictures is null")
        }
    }

    companion object {
    }
}
