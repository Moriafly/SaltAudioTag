package com.moriafly.salt.audiotag.test.format

import com.moriafly.salt.audiotag.format.FlacAudioFile
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.test.Test

class FlacTest {
    @Test
    fun test() {
        val path = Path("C:\\Users\\moria\\Music\\G.E.M.邓紫棋 - 桃花诺.flac")
        FlacAudioFile().read(SystemFileSystem.source(path).buffered())
    }
}
