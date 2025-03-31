package com.moriafly.salt.audiotag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moriafly.salt.audiotag.ui.screen.MainScreen
import com.moriafly.salt.ui.SaltTheme

@Composable
fun MainActivityContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SaltTheme.colors.background)
    ) {
        MainScreen()
    }
}
