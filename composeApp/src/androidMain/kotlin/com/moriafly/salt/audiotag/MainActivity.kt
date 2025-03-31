package com.moriafly.salt.audiotag

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

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import com.moriafly.salt.audiotag.ui.theme.AppTheme
import com.moriafly.salt.ui.UnstableSaltUiApi
import com.moriafly.salt.ui.ext.edgeToEdge
import com.moriafly.salt.ui.util.WindowUtil

class MainActivity : ComponentActivity() {
    @OptIn(UnstableSaltUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        edgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(isDarkTheme) {
                if (isDarkTheme) {
                    WindowUtil.setStatusBarForegroundColor(window, WindowUtil.BarColor.White)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowUtil.setNavigationBarForegroundColor(
                            window,
                            WindowUtil.BarColor.White
                        )
                    }
                } else {
                    WindowUtil.setStatusBarForegroundColor(window, WindowUtil.BarColor.Black)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        WindowUtil.setNavigationBarForegroundColor(
                            window,
                            WindowUtil.BarColor.Black
                        )
                    }
                }
            }

            AppTheme {
                MainActivityContent()
            }
        }
    }
}
