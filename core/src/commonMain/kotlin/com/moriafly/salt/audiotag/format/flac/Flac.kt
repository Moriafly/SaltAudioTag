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

@file:Suppress(
    "unused",
    "ktlint:standard:filename",
    "MemberVisibilityCanBePrivate",
    "SpellCheckingInspection"
)

package com.moriafly.salt.audiotag.format.flac

import kotlinx.io.Source
import kotlinx.io.bytestring.ByteString
import kotlinx.io.readByteString

internal class FlacSignature(
    source: Source
) {
    init {
        check(source.readByteString(4) == HEADER) {
            "Invalid FLAC header"
        }
    }

    companion object {
        /**
         * A FLAC bitstream consists of the fLaC (i.e., 0x664C6143) marker at the beginning of the
         * stream.
         */
        val HEADER = ByteString(0x66, 0x4C, 0x61, 0x43)
    }
}
