package com.moriafly.salt.audiotag.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SaltAudioTagIcons.Wrench: ImageVector
    get() {
        if (_Wrench != null) {
            return _Wrench!!
        }
        _Wrench = ImageVector.Builder(
            name = "Wrench",
            defaultWidth = 256.dp,
            defaultHeight = 256.dp,
            viewportWidth = 48f,
            viewportHeight = 48f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(31.5f, 5f)
                curveTo(24.614f, 5f, 19f, 10.614f, 19f, 17.5f)
                curveTo(19f, 18.907f, 19.296f, 20.234f, 19.727f, 21.496f)
                lineTo(7.611f, 33.611f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7.609f, 33.611f)
                curveTo(5.474f, 35.747f, 5.475f, 39.254f, 7.611f, 41.389f)
                curveTo(8.679f, 42.455f, 10.094f, 43f, 11.5f, 43f)
                curveTo(12.906f, 43f, 14.321f, 42.455f, 15.389f, 41.389f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 15.391f, 41.389f)
                lineTo(27.504f, 29.273f)
                curveTo(28.766f, 29.704f, 30.094f, 30f, 31.502f, 30f)
                curveTo(38.388f, 30f, 44.002f, 24.386f, 44.002f, 17.5f)
                curveTo(44.002f, 15.499f, 43.517f, 13.609f, 42.686f, 11.939f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 40.283f, 11.547f)
                lineTo(33.414f, 18.414f)
                curveTo(33.018f, 18.81f, 32.514f, 19f, 32f, 19f)
                curveTo(31.486f, 19f, 30.982f, 18.81f, 30.586f, 18.414f)
                curveTo(29.793f, 17.622f, 29.793f, 16.38f, 30.586f, 15.588f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 30.586f, 15.586f)
                lineTo(37.453f, 8.719f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 37.061f, 6.316f)
                curveTo(35.39f, 5.484f, 33.501f, 5f, 31.5f, 5f)
                close()
                moveTo(31.5f, 8f)
                curveTo(32.249f, 8f, 32.975f, 8.092f, 33.672f, 8.258f)
                lineTo(28.465f, 13.465f)
                curveTo(26.524f, 15.404f, 26.524f, 18.596f, 28.465f, 20.535f)
                curveTo(29.435f, 21.505f, 30.722f, 22f, 32f, 22f)
                curveTo(33.278f, 22f, 34.565f, 21.505f, 35.535f, 20.535f)
                lineTo(40.744f, 15.328f)
                curveTo(40.91f, 16.026f, 41.002f, 16.751f, 41.002f, 17.5f)
                curveTo(41.002f, 22.764f, 36.766f, 27f, 31.502f, 27f)
                curveTo(30.143f, 27f, 28.86f, 26.711f, 27.689f, 26.197f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 26.025f, 26.51f)
                lineTo(13.268f, 39.268f)
                curveTo(12.771f, 39.762f, 12.142f, 40f, 11.5f, 40f)
                curveTo(10.858f, 40f, 10.227f, 39.763f, 9.73f, 39.268f)
                curveTo(8.742f, 38.28f, 8.744f, 36.721f, 9.732f, 35.732f)
                lineTo(22.49f, 22.977f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 22.803f, 21.313f)
                curveTo(22.289f, 20.142f, 22f, 18.858f, 22f, 17.5f)
                curveTo(22f, 12.236f, 26.236f, 8f, 31.5f, 8f)
                close()
            }
        }.build()

        return _Wrench!!
    }

@Suppress("ObjectPropertyName")
private var _Wrench: ImageVector? = null
