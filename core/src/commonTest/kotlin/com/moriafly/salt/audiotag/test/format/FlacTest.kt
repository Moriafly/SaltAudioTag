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

package com.moriafly.salt.audiotag.test.format

import com.moriafly.salt.audiotag.SaltAudioTag
import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.LazyMetadataKey
import com.moriafly.salt.audiotag.rw.RwStrategy
import com.moriafly.salt.audiotag.rw.WriteOperation
import com.moriafly.salt.audiotag.rw.data.Metadata
import com.moriafly.salt.audiotag.rw.data.Picture
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.test.Test

class FlacTest {
    private val path = Path("C:\\Users\\moria\\Music\\G.E.M.邓紫棋 - 桃花诺.flac")

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun test() {
        val audioFile = SaltAudioTag.create(
            path = path,
            rwStrategy = RwStrategy.ReadWriteAll
        )
        audioFile.getAllMetadata().forEach { metadataKeyValue ->
            println("${metadataKeyValue.key}=${metadataKeyValue.value}")
        }

        val picture = audioFile.getLazyMetadataFirst(
            LazyMetadataKey.Picture(Picture.PictureType.BackCover)
        )
        if (picture != null) {
            val sinkPath = Path("C:\\Users\\moria\\Desktop\\frontCover.jpg")
            val sink = SystemFileSystem.sink(sinkPath).buffered()
            println(
                """
                mediaType = ${picture.mediaType}
                description = ${picture.description}
                wh = ${picture.width}x${picture.height}
                colorDepth = ${picture.colorDepth}bpp
                colorsNumber = ${picture.colorsNumber}
                pictureData.size = ${picture.pictureData.size}
                """.trimIndent()
            )
            sink.write(picture.pictureData)
        }
    }

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun testWrite() {
        val outputPath = Path("C:\\Users\\moria\\Desktop\\G.E.M.邓紫棋 - 桃花诺_output.flac")
        val audioFile = SaltAudioTag.create(
            path = path,
            rwStrategy = RwStrategy.ReadWriteAll
        )
        // audioFile.write(outputPath)
    }

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun testWriteAddArtist() {
        val outputPath = Path("C:\\Users\\moria\\Desktop\\G.E.M.邓紫棋 - 桃花诺_output.flac")
        val audioFile = SaltAudioTag.create(
            path = path,
            rwStrategy = RwStrategy.ReadWriteAll
        )
        val allMetadata = audioFile.getAllMetadata()

        audioFile.write(
            input = path,
            output = outputPath,
            WriteOperation.AllMetadata(
                metadatas = allMetadata + Metadata(
                    key = "ARTIST",
                    value = "Salt Audio Tag"
                )
            )
        )
    }

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun testWriteRemoveAllMetadata() {
        val outputPath = Path("C:\\Users\\moria\\Desktop\\G.E.M.邓紫棋 - 桃花诺_output.flac")
        val audioFile = SaltAudioTag.create(
            path = path,
            rwStrategy = RwStrategy.ReadWriteAll
        )
        audioFile.write(
            input = path,
            output = outputPath,
            WriteOperation.AllMetadata(
                metadatas = emptyList()
            )
        )
    }
}
