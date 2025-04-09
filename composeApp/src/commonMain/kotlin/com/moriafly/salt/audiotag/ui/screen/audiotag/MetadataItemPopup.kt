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

package com.moriafly.salt.audiotag.ui.screen.audiotag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.moriafly.salt.ui.UnstableSaltUiApi
import com.moriafly.salt.ui.dialog.YesNoDialog
import com.moriafly.salt.ui.popup.PopupMenu
import com.moriafly.salt.ui.popup.PopupMenuItem
import com.moriafly.salt.ui.popup.PopupState

@Suppress("DEPRECATION")
@OptIn(UnstableSaltUiApi::class)
@Composable
fun MetadataItemPopup(
    onDelete: () -> Unit,
    state: PopupState
) {
    var deleteDialog by remember { mutableStateOf(false) }
    if (deleteDialog) {
        YesNoDialog(
            onDismissRequest = {
                deleteDialog = false
            },
            onConfirm = {
                deleteDialog = false
                onDelete()
            },
            title = "删除此元数据项目",
            content = "是否确认删除？"
        )
    }

    PopupMenu(
        expanded = state.expend,
        onDismissRequest = { state.dismiss() }
    ) {
        PopupMenuItem(
            onClick = {
                state.dismiss()
                deleteDialog = true
            },
            text = "删除"
        )
    }
}
