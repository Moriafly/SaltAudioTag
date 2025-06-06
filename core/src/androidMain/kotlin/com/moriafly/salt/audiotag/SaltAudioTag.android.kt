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

package com.moriafly.salt.audiotag

import android.content.Context
import android.net.Uri
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.data.AudioTag
import kotlinx.io.asSource
import kotlinx.io.buffered

/**
 * Read audio tag from [uri].
 */
@UnstableSaltAudioTagApi
fun SaltAudioTag.read(
    context: Context,
    uri: Uri,
    extension: String,
    strategy: ReadStrategy
): Result<AudioTag> = runCatching {
    val inputStream = context.contentResolver.openInputStream(uri)

    require(inputStream != null) {
        "Failed to open input stream form $uri"
    }

    inputStream.asSource().buffered().use { source ->
        read(source, extension, strategy).getOrThrow()
    }
}
