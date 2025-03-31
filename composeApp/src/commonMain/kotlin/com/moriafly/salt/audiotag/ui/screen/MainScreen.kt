package com.moriafly.salt.audiotag.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moriafly.salt.ui.Text
import com.moriafly.salt.ui.UnstableSaltUiApi
import com.moriafly.salt.ui.ext.safeMainIgnoringVisibilityPadding

@OptIn(UnstableSaltUiApi::class)
@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .safeMainIgnoringVisibilityPadding()
    ) {
        Text(
            text = "Hello Salt Audio Tag"
        )
    }
}
