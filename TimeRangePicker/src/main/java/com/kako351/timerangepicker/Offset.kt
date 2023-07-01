package com.kako351.timerangepicker

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

sealed interface Offset {
    val x: Float
    val y: Float

    @Immutable
    data class CenterOffset(override val x: Float = 0f, override val y: Float = 0f) : Offset

    @Stable
    data class StartTimeDragOffset(override val x: Float, override val y: Float) : Offset

    @Stable
    data class EndTimeDragOffset(override val x: Float, override val y: Float) : Offset
}
