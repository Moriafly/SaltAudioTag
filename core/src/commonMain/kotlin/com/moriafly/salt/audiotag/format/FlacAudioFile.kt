@file:Suppress("unused", "MemberVisibilityCanBePrivate")

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

package com.moriafly.salt.audiotag.format

import com.moriafly.salt.audiotag.rw.AudioFile
import com.moriafly.salt.audiotag.rw.AudioProperties
import com.moriafly.salt.audiotag.rw.LazyMetadataKey
import com.moriafly.salt.audiotag.rw.MetadataKey
import kotlinx.io.Source

/**
 * # FLAC
 *
 * [RFC 9639](https://datatracker.ietf.org/doc/rfc9639/)
 *
 * @author Moriafly 2025/3/29
 */
class FlacAudioFile(
    private val source: Source
) : AudioFile() {
    override fun <T> getMetadata(key: MetadataKey<T>): List<T>? {
        TODO("Not yet implemented")
    }

    override fun <T> getLazyMetadata(key: LazyMetadataKey<T>): List<T>? {
        TODO("Not yet implemented")
    }

    override fun getAudioProperties(): AudioProperties? {
        TODO("Not yet implemented")
    }

    override fun close() {
        source.close()
    }

    init {
        Signature(source)

        var metadataBlockHeader: MetadataBlockHeader
        do {
            metadataBlockHeader = MetadataBlockHeader(source)
            println("blocktype = " + metadataBlockHeader.blockType)

            if (metadataBlockHeader.blockType == MetadataBlockHeader.BLOCK_TYPE_STREAMINFO) {
                val streaminfo = MetadataBlockStreaminfo(source)
                println("streaminfo = $streaminfo")
            } else {
                source.skip(metadataBlockHeader.length.toLong())
            }
        } while (!metadataBlockHeader.isLastMetadataBlock)
    }
}
