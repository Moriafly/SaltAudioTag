package com.moriafly.salt.audiotag.test.format

import com.moriafly.salt.audiotag.SaltAudioTag
import com.moriafly.salt.audiotag.rw.LazyMetadataKey
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.test.Test

class FlacTest {
    @Test
    fun test() {
        val path = Path("C:\\Users\\moria\\Music\\G.E.M.邓紫棋 - 桃花诺.flac")
        val audioFile = SaltAudioTag.read(path)
        audioFile.getAllMetadata().forEach { keyWithValues ->
            keyWithValues.value.forEach { value ->
                println("${keyWithValues.key}=$value")
            }
        }

        val frontCover = audioFile.getLazyMetadataFirst(LazyMetadataKey.FrontCover)
        if (frontCover != null) {
            // 写入文件
            val sinkPath = Path("C:\\Users\\moria\\Desktop\\frontCover.jpg")
            val sink = SystemFileSystem.sink(sinkPath).buffered()
            sink.write(frontCover)
        }

        audioFile.close()
    }
}
