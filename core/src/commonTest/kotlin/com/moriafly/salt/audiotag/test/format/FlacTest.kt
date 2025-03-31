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
            // 写入文件
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
        val allMetadata = audioFile.getAllMetadata()

        audioFile.write(
            outputPath,
            WriteOperation.AllMetadata(
                metadataList = emptyList()
            )
        )
        audioFile.close()
    }
}
