package com.moriafly.salt.audiotag

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
