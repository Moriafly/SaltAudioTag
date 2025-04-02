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

package com.moriafly.salt.audiotag.ui.screen.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moriafly.salt.audiotag.ui.navigation.LocalNavController
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltUiApi
import com.moriafly.salt.ui.ext.safeMainIgnoringVisibilityPadding
import com.moriafly.salt.ui.thenIf
import com.moriafly.salt.ui.util.SystemUtil

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
            .thenIf(SystemUtil.os.isAndroid()) {
                safeMainIgnoringVisibilityPadding()
            }
    ) {
        val navController = LocalNavController.current
        TitleBar(
            onBack = {
                navController.popBackStack()
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
