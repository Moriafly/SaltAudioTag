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

/**
 * The strategy for reading audio files.
 *
 * @property streaminfo Whether to read the streaminfo block.
 * @property metadatas Whether to read the metadata block.
 * @property pictures Whether to read the picture block.
 *
 * @author Moriafly
 */
data class ReadStrategy(
    val streaminfo: Boolean,
    val metadatas: Boolean,
    val pictures: Boolean
) {
    companion object {
        val IgnorePicture = ReadStrategy(
            streaminfo = true,
            metadatas = true,
            pictures = false
        )

        val OnlyPicture = ReadStrategy(
            streaminfo = false,
            metadatas = false,
            pictures = true
        )

        val All = ReadStrategy(
            streaminfo = true,
            metadatas = true,
            pictures = true
        )
    }
}
