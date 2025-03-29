package com.moriafly.salt.audiotag

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform