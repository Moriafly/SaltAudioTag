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

package com.moriafly.salt.audiotag.rw

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class Writer {
    /**
     * @param source The source file path.
     * @param destination The destination file path.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun write(
        source: Path,
        destination: Path
    ) {
        val tempFilePath = tempFilePath()

        try {
            SystemFileSystem.source(source).buffered().use { source ->
                SystemFileSystem.atomicMove()
            }
        } finally {
            SystemFileSystem.delete(tempFilePath)
        }
    }

    companion object {
        /**
         * Example:
         *
         * - C:\Users\moria\AppData\Local\Temp\06be772dd5dc49b3af958f698d908c81
         * - /data/user/0/com.moriafly.salt.audiotag/cache/06be772dd5dc49b3af958f698d908c81
         */
        @OptIn(ExperimentalUuidApi::class)
        fun tempFilePath(): Path {
            // 32 chars.
            val fileName = Uuid.random().toHexString()
            return Path(SystemTemporaryDirectory, fileName)
        }
    }
}
