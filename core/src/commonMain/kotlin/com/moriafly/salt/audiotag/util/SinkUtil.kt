package com.moriafly.salt.audiotag.util

import kotlinx.io.Sink

/**
 * Writes three bytes containing [int], in the big-endian order, to this sink.
 */
fun Sink.writeInt24(int: Int) {
    require(int in 0..0xFFFFFF) { "The int must fit in 24 bits (0..16777215)." }
    writeByte(((int ushr 16) and 0xFF).toByte())
    writeByte(((int ushr 8) and 0xFF).toByte())
    writeByte((int and 0xFF).toByte())
}
