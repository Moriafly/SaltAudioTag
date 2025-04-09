package com.moriafly.salt.audiotag.rw

import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.writeString
import kotlin.test.Test

class WriterTest {
    @Test
    fun tempDirectory() {
        // Windows: C:\Users\moria\AppData\Local\Temp
        // Android: /data/user/0/com.moriafly.salt.audiotag/cache
        println("SystemTemporaryDirectory = $SystemTemporaryDirectory")
    }

    @Test
    fun tempFilePath() {
        println("TempFilePath = ${Writer.tempFilePath()}")
    }

    @Test
    fun writeTempFilePath() {
        val tempFilePath = Writer.tempFilePath()
        SystemFileSystem.sink(tempFilePath).buffered().use {
            it.writeString("Hello, Salt Audio Tag!")
        }
        println("TempFilePath = $tempFilePath")
    }
}
