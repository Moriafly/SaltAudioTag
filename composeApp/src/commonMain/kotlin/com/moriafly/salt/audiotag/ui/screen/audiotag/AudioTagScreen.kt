package com.moriafly.salt.audiotag.ui.screen.audiotag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moriafly.salt.audiotag.ui.screen.basic.BasicScreenColumn
import com.moriafly.salt.ui.Item
import com.moriafly.salt.ui.ItemArrowType
import com.moriafly.salt.ui.ItemDivider
import com.moriafly.salt.ui.RoundedColumn
import com.moriafly.salt.ui.SaltTheme
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@Composable
fun AudioTagScreen(
    viewModel: AudioTagViewModel = androidx.lifecycle.viewmodel.compose.viewModel {
        AudioTagViewModel()
    }
) {
    BasicScreenColumn(
        title = "音频标签",
        autoVerticalScroll = false
    ) {
        RoundedColumn {
            val launcher = rememberFilePickerLauncher(
                type = FileKitType.File(
                    "flac"
                )
            ) { platformFile ->
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

        val uiState by viewModel.uiState.collectAsState()
        val metadataItems = uiState.metadataItemUiStates

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(metadataItems) { item ->
                MetadataItem(
                    item = item
                )
            }

            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
            }
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
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = SaltTheme.dimens.padding,
                    vertical = 2.dp
                )
        ) {
            BasicTextField(
                state = item.key,
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Max),
                textStyle = SaltTheme.textStyles.main
            )
            Spacer(
                modifier = Modifier
                    .padding(
                        horizontal = SaltTheme.dimens.subPadding
                    )
            )
            BasicTextField(
                state = item.value,
                modifier = Modifier
                    .weight(3f)
                    .height(IntrinsicSize.Max),
                textStyle = SaltTheme.textStyles.main
            )
        }
        ItemDivider(
            color = SaltTheme.colors.subText
        )
    }
}
