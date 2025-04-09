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

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moriafly.salt.audiotag.ui.navigation.LocalNavController
import com.moriafly.salt.audiotag.ui.screen.basic.BasicScreenColumn
import com.moriafly.salt.ui.BottomBar
import com.moriafly.salt.ui.BottomBarItem
import com.moriafly.salt.ui.Item
import com.moriafly.salt.ui.ItemInfo
import com.moriafly.salt.ui.ItemInfoType
import com.moriafly.salt.ui.RoundedColumn
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.Text
import com.moriafly.salt.ui.UnstableSaltUiApi
import com.moriafly.salt.ui.icons.SaltIcons
import com.moriafly.salt.ui.icons.Success
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@Composable
fun AudioTagScreen(
    viewModel: AudioTagViewModel = viewModel { AudioTagViewModel() }
) {
    BasicScreenColumn(
        title = "音频标签",
        autoVerticalScroll = false
    ) {
        val navController = LocalNavController.current
        LaunchedEffect(Unit) {
            viewModel.saveResult.collect {
                navController.popBackStack()
            }
        }

        LaunchedEffect(Unit) {
            viewModel.readResult.collect {
            }
        }

        AudioTagScreenContent()
    }
}

@OptIn(UnstableSaltUiApi::class)
@Composable
private fun ColumnScope.AudioTagScreenContent(
    viewModel: AudioTagViewModel = viewModel()
) {
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(
            "flac"
        )
    ) { platformFile ->
        if (platformFile != null) {
            viewModel.load(platformFile)
        }
    }

    LaunchedEffect(viewModel) {
        launcher.launch()
    }

    val uiState by viewModel.uiState.collectAsState()
    val state = uiState.state
    val metadataItems = uiState.metadataItemUiStates

    AnimatedContent(
        targetState = state,
        modifier = Modifier
            .weight(1f)
    ) { targetState ->
        when (targetState) {
            AudioTagUiState.State.Idle -> {
                IdleContent(
                    onPickFile = {
                        launcher.launch()
                    }
                )
            }

            AudioTagUiState.State.Loading -> LoadingContent()

            AudioTagUiState.State.Loaded -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(metadataItems) { item ->
                        MetadataItem(
                            item = item
                        )
                    }
                }
            }

            AudioTagUiState.State.Saving -> SavingContent()

            AudioTagUiState.State.Error -> {
                RoundedColumn {
                    ItemInfo(
                        text = "读取错误",
                        infoType = ItemInfoType.Error
                    )
                }
            }
        }
    }

    BottomBar {
        BottomBarItem(
            state = true,
            onClick = {
                viewModel.save()
            },
            painter = rememberVectorPainter(SaltIcons.Success),
            text = "保存"
        )
    }
}

@Composable
private fun IdleContent(
    onPickFile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        RoundedColumn {
            Item(
                onClick = {
                    onPickFile()
                },
                text = "选择音频文件"
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(SaltTheme.dimens.itemIcon),
                color = SaltTheme.colors.highlight,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "读取中……"
            )
        }
    }
}

@Composable
private fun SavingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(SaltTheme.dimens.itemIcon),
                color = SaltTheme.colors.highlight,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "保存中……"
            )
        }
    }
}

@Composable
private fun MetadataItem(
    item: AudioTagUiState.MetadataItemUiState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = SaltTheme.dimens.padding,
                vertical = 4.dp
            )
            .border(Dp.Hairline, SaltTheme.colors.subText, SaltTheme.shapes.small)
            .clip(SaltTheme.shapes.small)
    ) {
        BasicTextField(
            state = item.key,
            modifier = Modifier
                .fillMaxWidth()
                .background(SaltTheme.colors.stroke),
            textStyle = SaltTheme.textStyles.main.copy(
                color = SaltTheme.colors.text,
                fontWeight = FontWeight.Bold
            ),
            cursorBrush = SolidColor(SaltTheme.colors.text),
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                ) {
                    innerTextField()
                }
            }
        )
        BasicTextField(
            state = item.value,
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = SaltTheme.textStyles.main.copy(
                color = SaltTheme.colors.text
            ),
            cursorBrush = SolidColor(SaltTheme.colors.text),
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                ) {
                    innerTextField()
                }
            }
        )
    }
}
