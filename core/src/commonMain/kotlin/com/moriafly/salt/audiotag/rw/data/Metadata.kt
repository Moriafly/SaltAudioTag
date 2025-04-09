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

@Suppress("SpellCheckingInspection")
data class Metadata(
    val key: String,
    val value: String
) {
    fun toFlacUserComment(): String = "$key=$value"

    fun isValid(): Boolean = key.isNotBlank() && value.isNotBlank()

    /**
     * Format the metadata.
     *
     * @return The formatted metadata, null if the metadata is invalid.
     */
    fun format(): Metadata? = if (this.isValid()) {
        Metadata(
            key = key.trim(),
            value = value.trim()
        )
    } else {
        null
    }

    companion object {
        const val TITLE = "TITLE"
        const val VERSION = "VERSION"
        const val ALBUM = "ALBUM"
        const val TRACKNUMBER = "TRACKNUMBER"
        const val ARTIST = "ARTIST"
        const val PERFORMER = "PERFORMER"
        const val COPYRIGHT = "COPYRIGHT"
        const val LICENSE = "LICENSE"
        const val ORGANIZATION = "ORGANIZATION"
        const val DESCRIPTION = "DESCRIPTION"
        const val GENRE = "GENRE"
        const val DATE = "DATE"
        const val LOCATION = "LOCATION"
        const val CONTACT = "CONTACT"
        const val ISRC = "ISRC"
        const val LYRICS = "LYRICS"
    }
}
