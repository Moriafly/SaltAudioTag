@file:Suppress("unused")

package com.moriafly.salt.audiotag.rw

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

/**
 * Base class for auto-closable audio files with metadata support.
 *
 * @author Moriafly
 */
abstract class AudioFile : AutoCloseable {
    /**
     * Returns detected audio properties or `null` if unavailable.
     */
    abstract fun getAudioProperties(): AudioProperties?

    /**
     * Returns all pre-loaded metadata values for [key], empty list if none exist.
     */
    abstract fun <T> getMetadata(key: MetadataKey<T>): List<T>

    /**
     * Returns metadata values for [key] parsed on read, empty list if none exist.
     */
    abstract fun <T> getLazyMetadata(key: LazyMetadataKey<T>): List<T>

    /**
     * First value from [getMetadata] for [key], or `null` if empty.
     */
    fun <T> getFirstMetadata(key: MetadataKey<T>): T? = getMetadata(key).firstOrNull()

    /**
     * First value from [getLazyMetadata] for [key], triggers parsing on read.
     */
    fun <T> getFirstLazyMetadata(key: LazyMetadataKey<T>): T? = getLazyMetadata(key).firstOrNull()
}
