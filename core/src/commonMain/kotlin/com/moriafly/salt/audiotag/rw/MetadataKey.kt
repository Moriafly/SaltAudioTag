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

sealed class MetadataKey<T>(
    vararg val fields: String
) {
    data object Title : MetadataKey<String>("TITLE")

    data object Version : MetadataKey<String>("VERSION")

    data object Album : MetadataKey<String>("ALBUM")

    @Suppress("SpellCheckingInspection")
    data object TrackNumber : MetadataKey<String>("TRACKNUMBER")

    data object Artist : MetadataKey<String>("ARTIST")

    data object Performer : MetadataKey<String>("PERFORMER")

    data object Copyright : MetadataKey<String>("COPYRIGHT")

    data object License : MetadataKey<String>("LICENSE")

    data object Organization : MetadataKey<String>("ORGANIZATION")

    data object Description : MetadataKey<String>("DESCRIPTION")

    data object Genre : MetadataKey<String>("GENRE")

    data object Date : MetadataKey<String>("DATE")

    data object Location : MetadataKey<String>("LOCATION")

    data object Contact : MetadataKey<String>("CONTACT")

    /**
     * ISRC number for the track; see the [ISRC intro page](https://isrc.ifpi.org/) for more
     * information on ISRC numbers.
     */
    data object ISRC : MetadataKey<String>("ISRC")

    data object Lyrics : MetadataKey<String>("LYRICS")

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
    }
}
