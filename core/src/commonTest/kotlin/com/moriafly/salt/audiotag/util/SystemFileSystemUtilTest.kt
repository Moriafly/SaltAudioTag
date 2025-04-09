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

package com.moriafly.salt.audiotag.util

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.writeString
import kotlin.test.Test

class SystemFileSystemUtilTest {
    @Test
    fun tempDirectory() {
        // Windows: C:\Users\moria\AppData\Local\Temp
        // Android: /data/user/0/com.moriafly.salt.audiotag/cache
        println("SystemTemporaryDirectory = $SystemTemporaryDirectory")
    }

    @Test
    fun tempFilePath() {
        println("TempFilePath = ${SystemFileSystemUtil.tempFilePath()}")
    }

    @Test
    fun writeTempFilePath() {
        val tempFilePath = SystemFileSystemUtil.tempFilePath()
        SystemFileSystem.sink(tempFilePath).buffered().use {
            it.writeString("Hello, Salt Audio Tag!")
        }
        println("TempFilePath = $tempFilePath")
    }

    @Test
    fun copy() {
        val tempFilePath = SystemFileSystemUtil.tempFilePath()
        SystemFileSystem.sink(tempFilePath).buffered().use {
            it.writeString("Hello, Salt Audio Tag!")
        }
        SystemFileSystemUtil.copy(
            src = tempFilePath,
            dst = Path("C:\\Users\\moria\\1")
        )
    }
}
