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
import com.moriafly.salt.audiotag.rw.AudioPicture
import com.moriafly.salt.audiotag.rw.LazyMetadataKey
import com.moriafly.salt.audiotag.rw.MetadataKey
import com.moriafly.salt.audiotag.rw.MetadataKeyValue
import com.moriafly.salt.audiotag.rw.RwStrategy
import com.moriafly.salt.audiotag.rw.WriteOperation
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

        val audioPicture = audioFile.getLazyMetadataFirst(
            LazyMetadataKey.Picture(AudioPicture.PictureType.BackCover)
        )
        if (audioPicture != null) {
            val sinkPath = Path("C:\\Users\\moria\\Desktop\\frontCover.jpg")
            val sink = SystemFileSystem.sink(sinkPath).buffered()
            println(
                """
                mediaType = ${audioPicture.mediaType}
                description = ${audioPicture.description}
                wh = ${audioPicture.width}x${audioPicture.height}
                colorDepth = ${audioPicture.colorDepth}bpp
                colorsNumber = ${audioPicture.colorsNumber}
                pictureData.size = ${audioPicture.pictureData.size}
                """.trimIndent()
            )
            sink.write(audioPicture.pictureData)
        }

        audioFile.close()
    }

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun testWrite() {
        val outputPath = Path("C:\\Users\\moria\\Desktop\\G.E.M.邓紫棋 - 桃花诺_output.flac")
        val audioFile = SaltAudioTag.create(
            path = path,
            rwStrategy = RwStrategy.ReadWriteAll
        )
        audioFile.write(outputPath)
        audioFile.close()
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
            outputPath,
            WriteOperation.AllMetadata(
                metadataList = allMetadata + MetadataKeyValue(
                    key = MetadataKey.Artist,
                    value = "Salt Audio Tag"
                )
            )
        )
        audioFile.close()
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
            outputPath,
            WriteOperation.AllMetadata(
                metadataList = emptyList()
            )
        )
        audioFile.close()
    }
}
