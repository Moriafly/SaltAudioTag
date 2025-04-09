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

class FlacTest {
    private val path = Path("C:\\Users\\moria\\Music\\G.E.M.邓紫棋 - 桃花诺.flac")

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun test() {
//        val audioFile = SaltAudioTag.create(
//            path = path,
//            rwStrategy = RwStrategy.ReadWriteAll
//        )
//        audioFile.getAllMetadata().forEach { metadataKeyValue ->
//            println("${metadataKeyValue.key}=${metadataKeyValue.value}")
//        }
//
//        val picture = audioFile.getLazyMetadataFirst(
//            LazyMetadataKey.Picture(Picture.PictureType.BackCover)
//        )
//        if (picture != null) {
//            val sinkPath = Path("C:\\Users\\moria\\Desktop\\frontCover.jpg")
//            val sink = SystemFileSystem.sink(sinkPath).buffered()
//            println(
//                """
//                mediaType = ${picture.mediaType}
//                description = ${picture.description}
//                wh = ${picture.width}x${picture.height}
//                colorDepth = ${picture.colorDepth}bpp
//                colorsNumber = ${picture.colorsNumber}
//                pictureData.size = ${picture.pictureData.size}
//                """.trimIndent()
//            )
//            sink.write(picture.pictureData)
//        }
    }

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun testWrite() {
//        val outputPath = Path("C:\\Users\\moria\\Desktop\\G.E.M.邓紫棋 - 桃花诺_output.flac")
//        val audioFile = SaltAudioTag.create(
//            path = path,
//            rwStrategy = RwStrategy.ReadWriteAll
//        )
        // audioFile.write(outputPath)
    }

    @OptIn(UnstableSaltAudioTagApi::class)
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
                WriteOperation.AllMetadata(
                    metadatas = audioTag.metadatas + Metadata(
                        key = "ARTIST",
                        value = "Salt Audio Tag"
                    )
                )
            )
        }
    }

    @OptIn(UnstableSaltAudioTagApi::class)
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
}
