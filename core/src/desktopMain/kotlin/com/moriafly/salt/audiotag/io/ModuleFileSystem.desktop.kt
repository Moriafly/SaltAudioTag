package com.moriafly.salt.audiotag.io

import kotlinx.io.files.Path
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal actual object ModuleFileSystem {
    @OptIn(ExperimentalUuidApi::class)
    actual fun createTempPath(): Path {
        val tempFile = File.createTempFile(Uuid.random().toHexString(), null)
        return Path(tempFile.absolutePath)
    }
}
