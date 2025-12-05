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

import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.data.Picture.PictureType
import kotlin.jvm.JvmStatic

/**
 * The strategy for reading audio files.
 *
 * @property streaminfo Whether to read the streaminfo block.
 * @property metadatas Whether to read the metadata block.
 * @property pictureReadMode The mode for determining which picture blocks to process.
 * @property loadPictureBinary Whether to load the actual picture binary data into memory.
 * If false, [com.moriafly.salt.audiotag.rw.data.Picture.pictureData] will be empty, and
 * [com.moriafly.salt.audiotag.rw.data.Picture.globalFileOffset] should be used (Lazy Loading).
 * @property pictures Helper property to check if any pictures are read (for backward compatibility).
 *
 * @author Moriafly
 */
data class ReadStrategy(
    val streaminfo: Boolean,
    val metadatas: Boolean,
    val pictureReadMode: PictureReadMode,
    val loadPictureBinary: Boolean
) {
    /**
     * Secondary constructor to maintain binary compatibility with older versions.
     * This ensures that code compiling against the old (Boolean, Boolean, Boolean) signature still works.
     * Old code implies eager loading (loadPictureBinary = true).
     */
    @Deprecated("Use the new constructor with PictureReadMode")
    constructor(streaminfo: Boolean, metadatas: Boolean, pictures: Boolean) : this(
        streaminfo,
        metadatas,
        if (pictures) PictureReadMode.All else PictureReadMode.None,
        true
    )

    /**
     * Backward compatibility property.
     * Returns true if the mode is not [PictureReadMode.None].
     */
    @Deprecated("Use the new constructor with PictureReadMode")
    val pictures: Boolean
        get() = pictureReadMode !is PictureReadMode.None

    companion object {
        @JvmStatic
        val IgnorePicture = ReadStrategy(
            streaminfo = true,
            metadatas = true,
            pictureReadMode = PictureReadMode.None,
            loadPictureBinary = false
        )

        @JvmStatic
        val OnlyPicture = ReadStrategy(
            streaminfo = false,
            metadatas = false,
            pictureReadMode = PictureReadMode.All,
            loadPictureBinary = true
        )

        @UnstableSaltAudioTagApi
        @JvmStatic
        val OnlyPictureLazy = ReadStrategy(
            streaminfo = false,
            metadatas = false,
            pictureReadMode = PictureReadMode.All,
            loadPictureBinary = false
        )

        @JvmStatic
        val All = ReadStrategy(
            streaminfo = true,
            metadatas = true,
            pictureReadMode = PictureReadMode.All,
            loadPictureBinary = true
        )

        /**
         * Intelligent mode:
         * 1. Prioritizes FrontCover.
         * 2. If not found, falls back to BackCover.
         * 3. If not found, falls back to other types.
         * Only keeps the best match in memory.
         *
         * Note: This loads the binary data into memory.
         */
        @UnstableSaltAudioTagApi
        @JvmStatic
        val SmartFrontCover = ReadStrategy(
            streaminfo = false,
            metadatas = false,
            pictureReadMode = PictureReadMode.SmartFrontCover,
            loadPictureBinary = true
        )

        /**
         * Intelligent mode with Lazy Loading:
         * Same priority logic as [SmartFrontCover], but does NOT load the binary data into memory.
         * Use [com.moriafly.salt.audiotag.rw.data.Picture.globalFileOffset] to read data later.
         */
        @UnstableSaltAudioTagApi
        @JvmStatic
        val SmartFrontCoverLazy = ReadStrategy(
            streaminfo = false,
            metadatas = false,
            pictureReadMode = PictureReadMode.SmartFrontCover,
            loadPictureBinary = false
        )
    }
}

/**
 * Sealed interface defining how pictures should be read.
 */
sealed interface PictureReadMode {
    /**
     * Read all pictures (Standard behavior).
     */
    data object All : PictureReadMode

    /**
     * Do not read any pictures.
     */
    data object None : PictureReadMode

    /**
     * Smart mode: Only keeps the "best" picture based on priority (e.g., FrontCover > BackCover).
     * Significantly reduces memory usage.
     */
    data object SmartFrontCover : PictureReadMode

    /**
     * Custom filter mode.
     * @param filter Returns true if the picture type should be read.
     */
    data class Custom(
        val filter: (PictureType) -> Boolean
    ) : PictureReadMode
}
