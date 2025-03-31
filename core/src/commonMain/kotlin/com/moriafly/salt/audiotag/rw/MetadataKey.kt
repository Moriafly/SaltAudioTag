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

package com.moriafly.salt.audiotag.rw

sealed class MetadataKey(
    val field: String
) {
    data object Title : MetadataKey("TITLE")

    data object Version : MetadataKey("VERSION")

    data object Album : MetadataKey("ALBUM")

    @Suppress("SpellCheckingInspection")
    data object TrackNumber : MetadataKey("TRACKNUMBER")

    data object Artist : MetadataKey("ARTIST")

    data object Performer : MetadataKey("PERFORMER")

    data object Copyright : MetadataKey("COPYRIGHT")

    data object License : MetadataKey("LICENSE")

    data object Organization : MetadataKey("ORGANIZATION")

    data object Description : MetadataKey("DESCRIPTION")

    data object Genre : MetadataKey("GENRE")

    data object Date : MetadataKey("DATE")

    data object Location : MetadataKey("LOCATION")

    data object Contact : MetadataKey("CONTACT")

    /**
     * ISRC number for the track; see the [ISRC intro page](https://isrc.ifpi.org/) for more
     * information on ISRC numbers.
     */
    data object ISRC : MetadataKey("ISRC")

    data object Lyrics : MetadataKey("LYRICS")

    class Custom(
        field: String
    ) : MetadataKey(field) {
        override fun toString(): String = field.uppercase()
    }

    /**
     * Returns true if this key is a custom key.
     */
    fun isCustom(): Boolean = this is Custom

    companion object {
        /**
         * The keys that are used by the VorbisComment specification.
         */
        val OggVorbis = listOf(
            Title,
            Version,
            Album,
            TrackNumber,
            Artist,
            Performer,
            Copyright,
            License,
            Organization,
            Description,
            Genre,
            Date,
            Location,
            Contact,
            ISRC
        )

        fun <T> custom(field: String) = Custom(field)
    }
}

data class MetadataKeyValue(
    val key: MetadataKey,
    val value: String
) {
    fun toFlacUserComment(): String = "${key.field}=$value"
}
