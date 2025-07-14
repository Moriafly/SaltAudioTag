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

import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.Reader
import com.moriafly.salt.audiotag.rw.data.AudioTag
import com.moriafly.salt.audiotag.rw.data.Metadata
import com.moriafly.salt.audiotag.rw.data.Streaminfo
import kotlinx.io.Source

@UnstableSaltAudioTagApi
class CdaReader : Reader {
    override fun read(source: Source, strategy: ReadStrategy): AudioTag {
        val cda = Cda.create(source)

        val sampleCount = (cda.totalDurationInFrames / 75f) * 44100

        val streaminfo = Streaminfo(
            sampleRate = 44100,
            channelCount = 2,
            bits = 16,
            sampleCount = sampleCount.toLong(),
            fileLevelMetadataLength = 0
        )

        val metadatas = listOf(
            Metadata(
                key = Metadata.DISCID,
                value = cda.discId.toString()
            ),
            Metadata(
                key = Metadata.TRACKNUMBER,
                value = cda.trackNumber.toString()
            )
        )

        return AudioTag(
            streaminfo = streaminfo,
            metadatas = metadatas,
            pictures = null
        )
    }
}
