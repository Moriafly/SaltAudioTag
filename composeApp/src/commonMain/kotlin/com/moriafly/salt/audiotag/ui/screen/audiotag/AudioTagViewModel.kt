package com.moriafly.salt.audiotag.ui.screen.audiotag

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moriafly.salt.audiotag.SaltAudioTag
import com.moriafly.salt.audiotag.UnstableSaltAudioTagApi
import com.moriafly.salt.audiotag.rw.AudioFile
import com.moriafly.salt.audiotag.rw.Metadata
import com.moriafly.salt.audiotag.rw.WriteOperation
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.sink
import io.github.vinceglb.filekit.source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.buffered

class AudioTagViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AudioTagUiState())
    val uiState = _uiState.asStateFlow()

    private val _saveResult = MutableSharedFlow<Boolean>()
    val saveResult = _saveResult.asSharedFlow()

    private var platformFile: PlatformFile? = null
    private var audioFile: AudioFile? = null

    @OptIn(UnstableSaltAudioTagApi::class)
    fun load(platformFile: PlatformFile) {
        viewModelScope.launch(Dispatchers.IO) {
            this@AudioTagViewModel.platformFile = platformFile

            val audioFile = SaltAudioTag.create(
                source = platformFile.source().buffered(),
                extension = platformFile.extension
            ).also {
                this@AudioTagViewModel.audioFile = it
            }

            val allMetadata = audioFile.getAllMetadata()
            _uiState.update {
                it.copy(
                    metadataItemUiStates = allMetadata.map { metadata ->
                        AudioTagUiState.MetadataItemUiState(
                            key = TextFieldState(metadata.key),
                            value = TextFieldState(metadata.value)
                        )
                    }
                )
            }
        }
    }

    @OptIn(UnstableSaltAudioTagApi::class)
    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            val platformFile = this@AudioTagViewModel.platformFile
            val audioFile = this@AudioTagViewModel.audioFile

            if (platformFile == null || audioFile == null) {
                _saveResult.emit(false)
                return@launch
            }

            val allMetadata = _uiState.value.metadataItemUiStates.map { metadataItemUiState ->
                Metadata(
                    key = metadataItemUiState.key.text.toString(),
                    value = metadataItemUiState.value.text.toString()
                )
            }

            val source = platformFile.source().buffered()
            val sink = platformFile.sink().buffered()

            audioFile.write(
                input = source,
                output = sink,
                WriteOperation.AllMetadata(
                    allMetadata
                )
            )

            _saveResult.emit(true)
        }
    }
}
