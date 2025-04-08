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

data class Picture(
    val pictureType: PictureType,
    val mediaType: String,
    val description: String,
    val width: Int,
    val height: Int,
    val colorDepth: Int,
    val colorsNumber: Int,
    val pictureData: ByteArray
) {
    enum class PictureType {
        /**
         * Other.
         */
        Other,

        /**
         * PNG file icon of 32x32 pixels.
         *
         * See [RFC2083](https://www.rfc-editor.org/rfc/rfc9639.html#RFC2083).
         */
        PngFileIcon32x32,

        /**
         * 	General file icon.
         */
        GeneralFileIcon,

        /**
         * Front cover.
         */
        FrontCover,

        /**
         * Back cover.
         */
        BackCover,

        /**
         * Liner notes page.
         */
        LinerNotesPage,

        /**
         * Media label (e.g., CD, Vinyl or Cassette label).
         */
        MediaLabel,

        /**
         * Lead artist, lead performer, or soloist.
         */
        Lead,

        /**
         * Artist or performer.
         */
        Artist,

        /**
         * Conductor.
         */
        Conductor,

        /**
         * Band or orchestra.
         */
        Band,

        /**
         * Composer.
         */
        Composer,

        /**
         * Lyricist or text writer.
         */
        Lyricist,

        /**
         * Recording location.
         */
        RecordingLocation,

        /**
         * During recording.
         */
        DuringRecording,

        /**
         * During performance.
         */
        DuringPerformance,

        /**
         * Movie or video screen capture.
         */
        MovieScreenCapture,

        /**
         * A bright colored fish.
         */
        BrightColoredFish,

        /**
         * Illustration.
         */
        Illustration,

        /**
         * Band or artist logotype.
         */
        BandLogo,

        /**
         * Publisher or studio logotype.
         */
        PublisherLogotype,

        Unknown
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Picture

        if (pictureType != other.pictureType) return false
        if (mediaType != other.mediaType) return false
        if (description != other.description) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (colorDepth != other.colorDepth) return false
        if (colorsNumber != other.colorsNumber) return false
        if (!pictureData.contentEquals(other.pictureData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pictureType.hashCode()
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + colorDepth
        result = 31 * result + colorsNumber
        result = 31 * result + pictureData.contentHashCode()
        return result
    }
}
