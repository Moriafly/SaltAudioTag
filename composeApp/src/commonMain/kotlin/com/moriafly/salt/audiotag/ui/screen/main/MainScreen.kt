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

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moriafly.salt.ui.Item
import com.moriafly.salt.ui.ItemArrowType
import com.moriafly.salt.ui.ItemTip
import com.moriafly.salt.ui.RoundedColumn
import com.moriafly.salt.ui.UnstableSaltUiApi
import com.moriafly.salt.ui.ext.safeMainIgnoringVisibilityPadding
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@OptIn(UnstableSaltUiApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel { MainViewModel() }
) {
    Column(
        modifier = Modifier
            .safeMainIgnoringVisibilityPadding()
    ) {
        RoundedColumn {
            val launcher = rememberFilePickerLauncher { platformFile ->
                if (platformFile != null) {
                    viewModel.load(platformFile)
                }
            }

            Item(
                onClick = {
                    launcher.launch()
                },
                text = "选择文件",
                arrowType = ItemArrowType.Link
            )
        }

        val metadataList by viewModel.metadataList.collectAsState()

        RoundedColumn {
            ItemTip(
                text = metadataList.joinToString()
            )
        }
    }
}
