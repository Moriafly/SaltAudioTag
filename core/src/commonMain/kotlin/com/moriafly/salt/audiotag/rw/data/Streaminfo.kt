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

package com.moriafly.salt.audiotag.rw.data

import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi

/**
 * @author Moriafly
 *
 * @property fileLevelMetadataLength file level metadata length, bytes.
 */
data class Streaminfo(
    val sampleRate: Int,
    val channelCount: Int,
    val bits: Int,
    val sampleCount: Long,
    val fileLevelMetadataLength: Long
)

/**
 * Get seconds of streaminfo.
 */
val Streaminfo.seconds: Float
    get() = sampleCount.toFloat() / sampleRate

/**
 * Get duration of streaminfo, ms.
 */
val Streaminfo.duration: Long
    get() = (seconds * 1000).toLong()

/**
 * Guess bitrate of streaminfo.
 *
 * @throws IllegalArgumentException if fileSize is negative or seconds is not positive.
 */
@UnstableSaltAudioTagApi
fun Streaminfo.guessAverageBitrate(fileSize: Long): Float {
    require(fileSize >= 0) { "fileSize must be non-negative" }
    require(seconds > 0) { "seconds must be positive" }

    val metadataLength = fileLevelMetadataLength.coerceAtMost(fileSize)
    val effectiveSize = (fileSize - metadataLength).coerceAtLeast(0L)
    return (effectiveSize * 8) / seconds
}
