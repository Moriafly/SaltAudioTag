@file:Suppress("ktlint:standard:filename")

package com.moriafly.salt.audiotag

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SaltAudioTag",
    ) {
    }
}
