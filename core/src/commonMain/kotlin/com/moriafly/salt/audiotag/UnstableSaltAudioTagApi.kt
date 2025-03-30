@file:Suppress("unused")

package com.moriafly.salt.audiotag

@RequiresOptIn(
    message = "This Salt Audio Tag API is experimental and is likely to change or be removed in " +
        "the future.",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
annotation class UnstableSaltAudioTagApi
