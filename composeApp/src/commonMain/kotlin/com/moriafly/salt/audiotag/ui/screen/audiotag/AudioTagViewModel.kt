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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.buffered

class AudioTagViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AudioTagUiState())
    val uiState = _uiState.asStateFlow()

    private var platformFile: PlatformFile? = null
    private var audioFile: AudioFile? = null

    @OptIn(UnstableSaltAudioTagApi::class)
    fun load(platformFile: PlatformFile) {
        viewModelScope.launch(Dispatchers.IO) {
            this@AudioTagViewModel.platformFile = platformFile

            val audioFile = SaltAudioTag.create(
                source = platformFile.source().buffered(),
                extension = platformFile.extension
            )
            this@AudioTagViewModel.audioFile = audioFile

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
    fun save(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val allMetadata = _uiState.value.metadataItemUiStates.map { metadataItemUiState ->
                Metadata(
                    key = metadataItemUiState.key.text.toString(),
                    value = metadataItemUiState.value.text.toString()
                )
            }

            platformFile?.let {
                val source = it.source().buffered()
                val sink = it.sink().buffered()

                audioFile?.write(
                    input = source,
                    output = sink,
                    WriteOperation.AllMetadata(
                        allMetadata
                    )
                )
            }

            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}
