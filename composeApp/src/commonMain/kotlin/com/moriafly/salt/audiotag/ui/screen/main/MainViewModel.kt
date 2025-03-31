package com.moriafly.salt.audiotag.ui.screen.main

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moriafly.salt.audiotag.SaltAudioTag
import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.buffered

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    @OptIn(UnstableSaltAudioTagApi::class)
    fun load(platformFile: PlatformFile) {
        viewModelScope.launch(Dispatchers.IO) {
            val audioFile = SaltAudioTag.create(
                source = platformFile.source().buffered(),
                extension = platformFile.extension
            )

            val allMetadata = audioFile.getAllMetadata()
            _uiState.update {
                it.copy(
                    metadataItems = allMetadata.map { metadata ->
                        MainUiState.MetadataItem(
                            key = TextFieldState(metadata.key.field),
                            value = TextFieldState(metadata.value)
                        )
                    }
                )
            }

            audioFile.close()
        }
    }
}
