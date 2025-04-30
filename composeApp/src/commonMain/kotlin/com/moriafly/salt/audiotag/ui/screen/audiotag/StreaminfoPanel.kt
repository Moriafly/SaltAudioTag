package com.moriafly.salt.audiotag.ui.screen.audiotag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moriafly.salt.audiotag.rw.data.Streaminfo
import com.moriafly.salt.audiotag.rw.data.seconds
import com.moriafly.salt.ui.ItemOuterTitle
import com.moriafly.salt.ui.ItemValue
import com.moriafly.salt.ui.RoundedColumn

@Composable
fun StreaminfoPanel(
    streaminfo: Streaminfo
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ItemOuterTitle(text = "流信息")
        RoundedColumn {
            Row {
                ItemValue(
                    text = "采样率",
                    sub = "${streaminfo.sampleRate.toFloat() / 1000} kHz",
                    modifier = Modifier
                        .weight(1f)
                )
                ItemValue(
                    text = "声道数",
                    sub = "${streaminfo.channelCount}",
                    modifier = Modifier
                        .weight(1f)
                )
            }
            Row {
                ItemValue(
                    text = "位深度",
                    sub = "${streaminfo.bits}",
                    modifier = Modifier
                        .weight(1f)
                )
                ItemValue(
                    text = "时长",
                    sub = "${streaminfo.seconds} 秒",
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
    }
}
