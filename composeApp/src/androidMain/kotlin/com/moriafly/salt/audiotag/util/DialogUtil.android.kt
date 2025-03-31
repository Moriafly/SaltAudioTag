package com.moriafly.salt.audiotag.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun getDefaultDialogContentHeight(): Dp = LocalConfiguration.current
    .screenHeightDp.dp * 0.67f
