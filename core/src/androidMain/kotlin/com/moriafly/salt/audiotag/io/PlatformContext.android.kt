package com.moriafly.salt.audiotag.io

import android.content.Context

actual class PlatformContext(
    context: Context
) {
    val android = context
}
