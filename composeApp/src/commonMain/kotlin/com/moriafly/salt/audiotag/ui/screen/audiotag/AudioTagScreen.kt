package com.moriafly.salt.audiotag.ui.screen.audiotag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moriafly.salt.audiotag.ui.navigation.LocalNavController
import com.moriafly.salt.audiotag.ui.screen.basic.BasicScreenColumn
import com.moriafly.salt.ui.BottomBar
import com.moriafly.salt.ui.BottomBarItem
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.UnstableSaltUiApi
import com.moriafly.salt.ui.icons.SaltIcons
import com.moriafly.salt.ui.icons.Success
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@OptIn(UnstableSaltUiApi::class)
@Composable
fun AudioTagScreen(
    viewModel: AudioTagViewModel = viewModel { AudioTagViewModel() }
) {
    BasicScreenColumn(
        title = "音频标签",
        autoVerticalScroll = false
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
        val metadataItems = uiState.metadataItemUiStates

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

        BottomBar {
            val navController = LocalNavController.current
            BottomBarItem(
                state = true,
                onClick = {
                    viewModel.save {
                        navController.popBackStack()
                    }
                },
                painter = rememberVectorPainter(SaltIcons.Success),
                text = "保存"
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
                vertical = 2.dp
            )
            .border(Dp.Hairline, SaltTheme.colors.subText, RoundedCornerShape(2.dp))
    ) {
        BasicTextField(
            state = item.key,
            modifier = Modifier
                .fillMaxWidth()
                .background(SaltTheme.colors.stroke),
            textStyle = SaltTheme.textStyles.main.copy(
                color = SaltTheme.colors.text
            ),
            cursorBrush = SolidColor(SaltTheme.colors.text),
            decorator = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 4.dp,
                            vertical = 2.dp
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
                            horizontal = 4.dp,
                            vertical = 2.dp
                        )
                ) {
                    innerTextField()
                }
            }
        )
    }
}
