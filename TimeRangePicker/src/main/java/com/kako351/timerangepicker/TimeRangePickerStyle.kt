package com.kako351.timerangepicker

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

sealed class TimeRangePickerStyle

class TimeRangePickerRangeBarStyle(
    val centerOffset: TimeRangePickerOffset,

    val radius: Float,

    val startAngle: Float = 0f,

    val sweepAngle: Float = 360f,

    val color: Color = Color(0xFF2196F3),

    val cap: StrokeCap = StrokeCap.Round,

    val width: Float = 70f,

    val alpha: Float = 1f
): TimeRangePickerStyle() {
    val topLeft
        get() = Offset(
            x = centerOffset.x - radius,
            y = centerOffset.y - radius
        )

    val size
        get() = Size(
            width = radius * 2,
            height = radius * 2
        )

    val strokeStyle
        get() = Stroke(
            width = width,
            cap = cap
        )
}