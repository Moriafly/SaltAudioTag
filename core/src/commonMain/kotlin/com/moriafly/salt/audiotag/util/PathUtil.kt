package com.moriafly.salt.audiotag.util

import kotlinx.io.files.Path

val Path.extension: String
    get() = if (this.name.contains(".")) {
        this.name.substringAfterLast(".")
    } else {
        ""
    }
