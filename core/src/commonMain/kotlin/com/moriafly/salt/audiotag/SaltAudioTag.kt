package com.moriafly.salt.audiotag

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

import com.moriafly.salt.audiotag.format.FlacAudioFile
import com.moriafly.salt.audiotag.util.extension
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

object SaltAudioTag {
    fun read(path: Path) {
        val source = SystemFileSystem.source(path).buffered()

        when (path.extension) {
            "flac" -> FlacAudioFile().read(source)
            else -> throw UnsupportedOperationException("Unsupported file format")
        }
    }
}
