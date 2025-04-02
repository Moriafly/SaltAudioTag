package com.moriafly.salt.audiotag.ui.screen.audiotag

import androidx.compose.foundation.text.input.TextFieldState

data class AudioTagUiState(
    val state: State = State.Idle,
    val metadataItemUiStates: List<MetadataItemUiState> = emptyList()
) {
    enum class State {
        Idle,
        Loading,
        Loaded,
        Saving,
        Error
    }

    data class MetadataItemUiState(
        val key: TextFieldState,
        val value: TextFieldState
    )
}
