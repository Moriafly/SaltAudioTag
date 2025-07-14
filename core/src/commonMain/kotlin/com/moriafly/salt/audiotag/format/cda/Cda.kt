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

package com.moriafly.salt.audiotag.format.cda

import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readIntLe
import kotlinx.io.readShortLe

/**
 * Stores time in a minutes/seconds/frames format.
 */
@UnstableSaltAudioTagApi
internal data class TrackTime(
    val minutes: UByte,
    val seconds: UByte,
    val frames: UByte
)

/**
 * Represents the core track information pointed to by a CDA file.
 * This is a standalone, multiplatform-compatible utility class.
 *
 * https://en.wikipedia.org/wiki/.cda_file
 */
@UnstableSaltAudioTagApi
internal data class Cda(
    val trackNumber: Short,
    val discId: Int,
    val startOffsetInFrames: Int,
    val totalDurationInFrames: Int,
    val duration: TrackTime
) {
    init {
        require(trackNumber > 0) { "Track number must be greater than 0." }
        require(startOffsetInFrames >= 0) { "Start offset cannot be negative." }
        require(totalDurationInFrames >= 0) { "Total duration cannot be negative." }
    }

    companion object {
        private const val CDA_BLOCK_SIZE = 44L
        private const val RIFF_HEADER = "RIFF"
        private const val CHUNK_SIZE = 36
        private const val CDDA_IDENTIFIER = "CDDA"
        private const val FORMAT_CHUNK_IDENTIFIER = "fmt "
        private const val FORMAT_CHUNK_LENGTH = 24
        private const val FORMAT_VERSION: Short = 1

        /**
         * Creates a Cda instance from a Source.
         *
         * @param source The source containing the raw CDA data.
         * @return The parsed Cda instance.
         */
        fun create(source: Source): Cda {
            require(source.request(CDA_BLOCK_SIZE)) {
                "Source must contain at least $CDA_BLOCK_SIZE bytes"
            }

            // Validate the file header and format chunk. String reading is inlined
            require(source.readByteArray(4).decodeToString() == RIFF_HEADER) {
                "Invalid RIFF header"
            }
            require(source.readIntLe() == CHUNK_SIZE) { "Incorrect chunk size" }
            require(source.readByteArray(4).decodeToString() == CDDA_IDENTIFIER) {
                "File type identifier must be 'CDDA'"
            }
            require(source.readByteArray(4).decodeToString() == FORMAT_CHUNK_IDENTIFIER) {
                "Incorrect format chunk identifier"
            }
            require(source.readIntLe() == FORMAT_CHUNK_LENGTH) { "Incorrect format chunk length" }
            require(source.readShortLe() == FORMAT_VERSION) { "Incorrect format version" }

            // Read the core track data
            val trackNumber = source.readShortLe()
            val discId = source.readIntLe()
            val startOffsetInFrames = source.readIntLe()
            val totalDurationInFrames = source.readIntLe()

            // Skip the 4-byte redundant position info
            source.skip(4)

            // Read the duration in M/S/F format
            val durationFrames = source.readByte().toUByte()
            val durationSeconds = source.readByte().toUByte()
            val durationMinutes = source.readByte().toUByte()

            return Cda(
                trackNumber = trackNumber,
                discId = discId,
                startOffsetInFrames = startOffsetInFrames,
                totalDurationInFrames = totalDurationInFrames,
                duration = TrackTime(
                    minutes = durationMinutes,
                    seconds = durationSeconds,
                    frames = durationFrames
                )
            )
        }
    }
}
