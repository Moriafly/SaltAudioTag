package com.moriafly.salt.audiotag.ui.screen.main

import androidx.lifecycle.ViewModel
import com.moriafly.salt.audiotag.SaltAudioTag
import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.MetadataKeyValue
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.io.buffered

class MainViewModel : ViewModel() {
    private val _platformFile = MutableStateFlow<PlatformFile?>(null)
    val platformFile = _platformFile.asStateFlow()

    private val _metadataList = MutableStateFlow<List<MetadataKeyValue>>(emptyList())
    val metadataList = _metadataList.asStateFlow()

    fun updatePlatformFile(value: PlatformFile?) {
        _platformFile.update { value }
    }

    @OptIn(UnstableSaltAudioTagApi::class)
    fun load(platformFile: PlatformFile) {
        updatePlatformFile(platformFile)

        val audioFile = SaltAudioTag.create(
            source = platformFile.source().buffered(),
            extension = platformFile.extension
        )

        val metadataList = audioFile.getAllMetadata()
        _metadataList.update { metadataList }

        audioFile.close()
    }
}
