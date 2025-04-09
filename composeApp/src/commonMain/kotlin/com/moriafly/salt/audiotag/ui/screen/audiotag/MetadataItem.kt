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

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriafly.salt.audiotag.ui.icon.More
import com.moriafly.salt.audiotag.ui.icon.SaltAudioTagIcons
import com.moriafly.salt.ui.Icon
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.noRippleClickable
import com.moriafly.salt.ui.popup.rememberPopupState

@Composable
fun MetadataItem(
    onDelete: () -> Unit,
    item: AudioTagUiState.MetadataItemUiState
) {
    val state = rememberPopupState()
    val color = if (state.expend) {
        SaltTheme.colors.stroke
    } else {
        Color.Unspecified
    }
    Row(
        modifier = Modifier
            .background(color)
            .padding(
                horizontal = SaltTheme.dimens.padding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetadataKeyValue(
            key = item.key,
            value = item.value,
            modifier = Modifier
                .weight(1f)
        )
        Box {
            Icon(
                painter = rememberVectorPainter(SaltAudioTagIcons.More),
                contentDescription = null,
                modifier = Modifier
                    .size(SaltTheme.dimens.itemIcon)
                    .noRippleClickable {
                        state.expend()
                    }
                    .padding(4.dp),
                tint = SaltTheme.colors.text.copy(alpha = 0.75f)
            )

            MetadataItemPopup(
                onDelete = onDelete,
                state = state
            )
        }
    }
}

@Composable
private fun MetadataKeyValue(
    key: TextFieldState,
    value: TextFieldState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        MetadataTextField(
            state = key,
            modifier = Modifier
                .weight(1f)
        )
        Spacer(Modifier.width(SaltTheme.dimens.subPadding))
        MetadataTextField(
            state = value,
            modifier = Modifier
                .weight(3f),
            textStyle = SaltTheme.textStyles.main.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun MetadataTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = SaltTheme.textStyles.main,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused.value) {
        SaltTheme.colors.highlight
    } else {
        SaltTheme.colors.subText.copy(alpha = 0.5f)
    }

    val color = textStyle.color.takeOrElse { SaltTheme.colors.text }

    BasicTextField(
        state = state,
        modifier = modifier,
        textStyle = textStyle.copy(
            color = color
        ),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(SaltTheme.colors.highlight),
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = SaltTheme.dimens.subPadding
                    )
            ) {
                innerTextField()
            }
        }
    )
}
