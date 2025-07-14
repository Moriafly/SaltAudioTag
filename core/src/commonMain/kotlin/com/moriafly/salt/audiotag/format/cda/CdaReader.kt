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
