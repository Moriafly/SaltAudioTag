package com.moriafly.salt.audiotag.ui.screen.audiotag

import androidx.compose.foundation.text.input.TextFieldState

data class AudioTagUiState(
    val metadataItemUiStates: List<MetadataItemUiState> = emptyList()
) {
    data class MetadataItemUiState(
        val key: TextFieldState,
        val value: TextFieldState
    )
}
