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
