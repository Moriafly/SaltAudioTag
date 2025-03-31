package com.moriafly.salt.audiotag.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import com.moriafly.salt.ui.SaltConfigs
import com.moriafly.salt.ui.SaltDynamicColors
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.darkSaltColors
import com.moriafly.salt.ui.lightSaltColors

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    SaltTheme(
        configs = SaltConfigs(
            isDarkTheme = isSystemInDarkTheme(),
            indication = ripple()
        ),
        dynamicColors = SaltDynamicColors(
            light = lightSaltColors(),
            dark = darkSaltColors()
        ),
        content = content
    )
}
