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

import com.moriafly.salt.audiotag.format.cda.CdaReader
import com.moriafly.salt.audiotag.format.flac.FlacReader
import com.moriafly.salt.audiotag.format.flac.FlacWriter
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.WriteOperation
import com.moriafly.salt.audiotag.rw.data.AudioTag
import com.mroiafly.salt.audiotag.BuildKonfig
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
    fun getVersionName() = BuildKonfig.versionName

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
            "cda" -> CdaReader().read(source, strategy)
            else -> throw UnsupportedOperationException("Unsupported file extension $extension")
        }
    }

    /**
     * Read audio file.
     *
     * @param path Audio file path in SystemFileSystem.
     * @param extension Audio file extension.
     * @param strategy Read strategy.
     */
    @UnstableSaltAudioTagApi
    fun read(
        path: Path,
        extension: String,
        strategy: ReadStrategy
    ): Result<AudioTag> = runCatching {
        SystemFileSystem.source(path).buffered().use { source ->
            read(source, extension, strategy).getOrThrow()
        }
    }

    /**
     * Write audio file.
     *
     * @param src Source file path.
     * @param dst Destination file path.
     * @param extension Extension of the audio file, such as "flac".
     * @param operation Write operations.
     */
    @UnstableSaltAudioTagApi
    fun write(
        src: Path,
        dst: Path,
        extension: String,
        vararg operation: WriteOperation
    ): Result<Unit> = runCatching {
        when (extension) {
            "flac" -> FlacWriter().write(src, dst, *operation)
            else -> throw UnsupportedOperationException("Unsupported file extension $extension")
        }
    }
}
