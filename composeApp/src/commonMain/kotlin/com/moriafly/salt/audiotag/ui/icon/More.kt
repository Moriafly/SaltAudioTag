package com.moriafly.salt.audiotag.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SaltAudioTagIcons.More: ImageVector
    get() {
        if (_More != null) {
            return _More!!
        }
        _More = ImageVector.Builder(
            name = "More",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 30f,
            viewportHeight = 30f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(5f, 12f)
                curveTo(3.343f, 12f, 2f, 13.343f, 2f, 15f)
                curveTo(2f, 16.657f, 3.343f, 18f, 5f, 18f)
                curveTo(6.657f, 18f, 8f, 16.657f, 8f, 15f)
                curveTo(8f, 13.343f, 6.657f, 12f, 5f, 12f)
                close()
                moveTo(15f, 12f)
                curveTo(13.343f, 12f, 12f, 13.343f, 12f, 15f)
                curveTo(12f, 16.657f, 13.343f, 18f, 15f, 18f)
                curveTo(16.657f, 18f, 18f, 16.657f, 18f, 15f)
                curveTo(18f, 13.343f, 16.657f, 12f, 15f, 12f)
                close()
                moveTo(25f, 12f)
                curveTo(23.343f, 12f, 22f, 13.343f, 22f, 15f)
                curveTo(22f, 16.657f, 23.343f, 18f, 25f, 18f)
                curveTo(26.657f, 18f, 28f, 16.657f, 28f, 15f)
                curveTo(28f, 13.343f, 26.657f, 12f, 25f, 12f)
                close()
            }
        }.build()

        return _More!!
    }

@Suppress("ObjectPropertyName")
private var _More: ImageVector? = null
