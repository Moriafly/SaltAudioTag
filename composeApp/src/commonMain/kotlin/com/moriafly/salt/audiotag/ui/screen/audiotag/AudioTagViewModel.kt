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
import com.moriafly.salt.audiotag.rw.ReadStrategy
import com.moriafly.salt.audiotag.rw.WriteOperation
import com.moriafly.salt.audiotag.rw.data.Metadata
import com.moriafly.salt.audiotag.util.SystemFileSystemUtil
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
import kotlinx.io.files.SystemFileSystem

class AudioTagViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AudioTagUiState())
    val uiState = _uiState.asStateFlow()

    private val _readResult = MutableSharedFlow<Boolean>()
    val readResult = _readResult.asSharedFlow()

    private val _saveResult = MutableSharedFlow<Boolean>()
    val saveResult = _saveResult.asSharedFlow()

    private val mutex = Mutex()

    private var platformFile: PlatformFile? = null

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
                    val audioTagResult = platformFile.source().buffered().use { source ->
                        SaltAudioTag.read(
                            source = source,
                            extension = platformFile.extension,
                            strategy = ReadStrategy.All
                        )
                    }

                    val audioTag = audioTagResult.getOrThrow()

                    val metadatas = audioTag.metadatas ?: emptyList()

                    _uiState.update {
                        it.copy(
                            metadataItemUiStates = metadatas.map { metadata ->
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

                if (platformFile == null) {
                    _saveResult.emit(false)
                    return@launch
                }

                val metadatas = _uiState.value.metadataItemUiStates.map { metadataItemUiState ->
                    Metadata(
                        key = metadataItemUiState.key.text.toString(),
                        value = metadataItemUiState.value.text.toString()
                    )
                }

                val src = SystemFileSystemUtil.tempFilePath()
                val dst = SystemFileSystemUtil.tempFilePath()
                try {
                    platformFile.source().buffered().use {
                        SystemFileSystemUtil.write(it, src)
                    }

                    SaltAudioTag.write(
                        src = src,
                        dst = dst,
                        extension = platformFile.extension,
                        WriteOperation.AllMetadata.create(metadatas)
                    )

                    platformFile.sink().buffered().use { sink ->
                        SystemFileSystem.source(dst).buffered().use { source ->
                            sink.transferFrom(source)
                        }
                    }

                    _saveResult.emit(true)
                } catch (e: Exception) {
                    // Save error
                    _saveResult.emit(false)
                } finally {
                    SystemFileSystem.delete(src)
                    SystemFileSystem.delete(dst)
                }
            }
        }
    }

    fun addEmptyMetadata() {
        _uiState.update {
            it.copy(
                metadataItemUiStates =
                    it.metadataItemUiStates + AudioTagUiState.MetadataItemUiState(
                        key = TextFieldState(),
                        value = TextFieldState()
                    )
            )
        }
    }

    fun removeMetadata(index: Int) {
        _uiState.update {
            it.copy(
                metadataItemUiStates =
                    it.metadataItemUiStates.toMutableList().apply {
                        removeAt(index)
                    }
            )
        }
    }
}
