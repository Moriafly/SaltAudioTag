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

package com.moriafly.salt.audiotag.util

import kotlinx.io.Sink

/**
 * Writes three bytes containing [int], in the big-endian order, to this sink.
 */
internal fun Sink.writeInt24(int: Int) {
    require(int in 0..0xFFFFFF) { "The int must fit in 24 bits (0..16777215)." }
    writeByte(((int ushr 16) and 0xFF).toByte())
    writeByte(((int ushr 8) and 0xFF).toByte())
    writeByte((int and 0xFF).toByte())
}
