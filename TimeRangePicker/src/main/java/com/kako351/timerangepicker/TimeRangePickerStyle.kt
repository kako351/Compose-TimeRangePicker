package com.kako351.timerangepicker

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

interface TimeRangePickerRangeBarStyle {
    val centerOffset: TimeRangePickerOffset
        get() = TimeRangePickerOffset.Offset(x = 0f, y = 0f)

    val radius: Float
        get() = 0f

    val startAngle: Float
        get() = 0f

    val sweepAngle: Float
        get() = TimeRangePickerAngle.MAX_ANGLE

    val color: Color
        get() = Color.LightGray

    val cap: StrokeCap
        get() = StrokeCap.Round

    val width: Float
        get() = 70f

    val alpha: Float
        get() = 1f

    val topLeft: Offset
        get() = Offset(
            x = centerOffset.x - radius,
            y = centerOffset.y - radius
        )

    val size: Size
        get() = Size(
            width = radius * 2,
            height = radius * 2
        )

    val strokeStyle: Stroke
        get() = Stroke(
            width = width,
            cap = cap
        )

    fun copy(
        centerOffset: TimeRangePickerOffset = this.centerOffset,
        radius: Float = this.radius,
        startAngle: Float = this.startAngle,
        sweepAngle: Float = this.sweepAngle,
        color: Color = this.color,
        cap: StrokeCap = this.cap,
        width: Float = this.width,
        alpha: Float = this.alpha
    ) = RangeBarStyle(
        centerOffset = centerOffset,
        radius = radius,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        color = color,
        cap = cap,
        width = width,
        alpha = alpha
    )
}

object Default : TimeRangePickerRangeBarStyle

class RangeBarStyle(
    override val centerOffset: TimeRangePickerOffset = TimeRangePickerOffset.Offset(x = 0f, y = 0f),

    override val radius: Float = 0f,

    override val startAngle: Float = 0f,

    override val sweepAngle: Float = 360f,

    override val color: Color = Color(0xFF2196F3),

    override val cap: StrokeCap = StrokeCap.Round,

    override val width: Float = 70f,

    override val alpha: Float = 1f
) : TimeRangePickerRangeBarStyle {
    companion object {
        val Default = RangeBarStyle()
    }
}
