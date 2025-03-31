package com.moriafly.salt.audiotag.test.format

import com.moriafly.salt.audiotag.SaltAudioTag
import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.AudioPicture
import com.moriafly.salt.audiotag.rw.LazyMetadataKey
import com.moriafly.salt.audiotag.rw.RwStrategy
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteString
import kotlin.test.Test

class FlacTest {
    private val path = Path("C:\\Users\\moria\\Music\\G.E.M.邓紫棋 - 桃花诺.flac")

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun test() {
        SystemFileSystem.source(path).buffered().readByteString().size.let {
            println("size=$it")
        }

        return

        val audioFile = SaltAudioTag.create(
            path = path,
            rwStrategy = RwStrategy.ReadWriteAll
        )
        audioFile.getAllMetadata().forEach { keyWithValues ->
            keyWithValues.value.forEach { value ->
                println("${keyWithValues.key}=$value")
            }
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
}
