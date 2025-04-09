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

@file:Suppress("unused")

package com.moriafly.salt.audiotag.util

import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.files.FileNotFoundException
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * System file system utilities.
 *
 * @author Moriafly
 */
internal object SystemFileSystemUtil {
    /**
     * Copy regular file [src] to [dst].
     *
     * TODO: https://github.com/Kotlin/kotlinx-io/issues/233
     *
     * @throws FileNotFoundException When [src] does not exist.
     * @throws IOException When failed to read [src] or [src] is not a regular file. When it's not
     * possible to open the [dst] for writing. When some I/O error occurs.
     */
    fun copy(src: Path, dst: Path) {
        val metadata = SystemFileSystem.metadataOrNull(src)
            ?: throw IOException("Failed to read metadata of $src")

        if (!metadata.isRegularFile) {
            throw IOException("Source $src must be a regular file")
        }

        SystemFileSystem.source(src).buffered().use { source ->
            SystemFileSystem.sink(dst).buffered().use { sink ->
                sink.write(source, metadata.size)
            }
        }
    }
}
