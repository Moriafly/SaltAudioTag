package com.moriafly.salt.audiotag.test.format

import com.moriafly.salt.audiotag.SaltAudioTag
import kotlinx.io.files.Path
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
    }
}
