package com.moriafly.salt.audiotag.ui.screen.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltUiApi
import com.moriafly.salt.ui.ext.safeMainIgnoringVisibilityPadding
import com.moriafly.salt.ui.thenIf

@OptIn(UnstableSaltUiApi::class)
@Composable
fun BasicScreenColumn(
    title: String,
    showBackBtn: Boolean = true,
    autoVerticalScroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .safeMainIgnoringVisibilityPadding()
    ) {
        TitleBar(
            onBack = {
            },
            text = title,
            showBackBtn = showBackBtn
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .thenIf(autoVerticalScroll) {
                    verticalScroll(rememberScrollState())
                },
            content = content
        )
    }
}
