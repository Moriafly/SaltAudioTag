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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.buffered

class AudioTagViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AudioTagUiState())
    val uiState = _uiState.asStateFlow()

    private val _readResult = MutableSharedFlow<Boolean>()
    val readResult = _readResult.asSharedFlow()

    private val _saveResult = MutableSharedFlow<Boolean>()
    val saveResult = _saveResult.asSharedFlow()

    private val mutex = Mutex()

    private var platformFile: PlatformFile? = null
    private var audioFile: AudioFile? = null

    private var loadJob: Job? = null
    private var saveJob: Job? = null

    @OptIn(UnstableSaltAudioTagApi::class)
    fun load(platformFile: PlatformFile) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            mutex.withLock {
                _uiState.update { it.copy(state = AudioTagUiState.State.Loading) }

                this@AudioTagViewModel.platformFile = platformFile

                try {
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

                    _readResult.emit(true)
                    _uiState.update { it.copy(state = AudioTagUiState.State.Loaded) }
                } catch (e: Exception) {
                    _readResult.emit(false)
                    _uiState.update { it.copy(state = AudioTagUiState.State.Error) }

                    // TODO
                    e.printStackTrace()
                }
            }
        }
    }

    @OptIn(UnstableSaltAudioTagApi::class)
    fun save() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch(Dispatchers.IO) {
            mutex.withLock {
                _uiState.update { it.copy(state = AudioTagUiState.State.Saving) }

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

                audioFile.write(
                    input = { platformFile.source().buffered() },
                    output = { platformFile.sink().buffered() },
                    WriteOperation.AllMetadata(
                        allMetadata
                    )
                )

                _saveResult.emit(true)
            }
        }
    }
}
