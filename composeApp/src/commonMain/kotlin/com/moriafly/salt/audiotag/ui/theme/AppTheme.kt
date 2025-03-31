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
