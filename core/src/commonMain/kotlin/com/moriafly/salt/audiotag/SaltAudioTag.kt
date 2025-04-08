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

package com.moriafly.salt.audiotag

import com.moriafly.salt.audiotag.format.FlacAudioFile
import com.moriafly.salt.audiotag.format.flac.FlacReader
import com.moriafly.salt.audiotag.rw.AudioFile
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.RwStrategy
import com.moriafly.salt.audiotag.rw.data.AudioTag
import com.moriafly.salt.audiotag.util.extension
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * Salt Audio Tag
 *
 * @author Moriafly
 */
object SaltAudioTag {
    @UnstableSaltAudioTagApi
    fun create(
        path: Path,
        rwStrategy: RwStrategy = RwStrategy.ReadWriteAll
    ): AudioFile = create(
        source = SystemFileSystem.source(path).buffered(),
        extension = path.extension,
        rwStrategy = rwStrategy
    )

    @UnstableSaltAudioTagApi
    fun create(
        source: Source,
        extension: String,
        rwStrategy: RwStrategy = RwStrategy.ReadWriteAll
    ): AudioFile = when (extension) {
        "flac" -> FlacAudioFile(source, rwStrategy)
        else -> throw UnsupportedOperationException("Unsupported file format.")
    }

    /**
     * Read audio file.
     *
     * Sample:
     * ```kotlin
     * source.use {
     *     val result = SaltAudioTag.read(
     *         source = it,
     *         extension = "flac",
     *         strategy = ReadStrategy.All
     *     )
     * }
     * ```
     *
     * @param source Audio file source.
     * @param extension Audio file extension.
     * @param strategy Read strategy.
     */
    @UnstableSaltAudioTagApi
    fun read(
        source: Source,
        extension: String,
        strategy: ReadStrategy
    ): Result<AudioTag> = runCatching {
        when (extension) {
            "flac" -> FlacReader().read(source, strategy)
            else -> throw UnsupportedOperationException("Unsupported file format.")
        }
    }
}
