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

package com.moriafly.salt.audiotag.format.cda

import com.moriafly.salt.audiotag.SaltAudioTag
import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.data.seconds
import kotlinx.io.files.Path
import kotlin.test.Test

class CdaTest {
    private val path = Path("E:\\Track03.cda")

    @OptIn(UnstableSaltAudioTagApi::class)
    @Test
    fun testRead() {
        val audioTag = SaltAudioTag.read(
            path = path,
            extension = "cda",
            strategy = ReadStrategy.All
        ).getOrThrow()

        audioTag.streaminfo?.let {
            println("streaminfo = ${audioTag.streaminfo}")
            val secondsInt = it.seconds.toInt()
            println("time = ${secondsInt / 60}:${secondsInt % 60}")
        }

        audioTag.metadatas?.forEach { metadata ->
            println("key = ${metadata.key}, value = ${metadata.value}")
        }
    }
}
