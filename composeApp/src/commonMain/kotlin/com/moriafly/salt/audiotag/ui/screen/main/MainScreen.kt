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

package com.moriafly.salt.audiotag.ui.screen.main

import androidx.compose.runtime.Composable
import com.moriafly.salt.audiotag.ui.navigation.LocalNavController
import com.moriafly.salt.audiotag.ui.navigation.ScreenRoute
import com.moriafly.salt.audiotag.ui.screen.basic.BasicScreenColumn
import com.moriafly.salt.ui.Item
import com.moriafly.salt.ui.ItemOuterLargeTitle
import com.moriafly.salt.ui.ItemValue
import com.moriafly.salt.ui.RoundedColumn
import com.moriafly.salt.ui.UnstableSaltUiApi
import kotlinx.io.files.SystemTemporaryDirectory

@OptIn(UnstableSaltUiApi::class)
@Composable
fun MainScreen() {
    BasicScreenColumn(
        title = "",
        showBackBtn = false
    ) {
        ItemOuterLargeTitle(
            text = "椒盐音频标签",
            sub = "版本"
        )

        RoundedColumn {
            val navController = LocalNavController.current
            Item(
                onClick = {
                    navController.navigate(ScreenRoute.AUDIO_TAG)
                },
                text = "FLAC 元数据读写"
            )
        }

        RoundedColumn {
            ItemValue(
                text = "TempDir",
                sub = SystemTemporaryDirectory.toString()
            )
        }
    }
}
