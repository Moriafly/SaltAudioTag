package com.moriafly.salt.audiotag.ui.screen.main

import androidx.compose.foundation.text.input.TextFieldState

data class MainUiState(
    val metadataItems: List<MetadataItem> = emptyList()
) {
    data class MetadataItem(
        val key: TextFieldState,
        val value: TextFieldState
    )
}
