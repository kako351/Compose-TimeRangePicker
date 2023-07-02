package com.kako351.timerangepicker

import androidx.compose.runtime.Immutable

sealed interface TimeRangePickerOffset {
    val x: Float
    val y: Float

    /**
     * Calculate offset from time
     * @param centerX Center X
     * @param centerY Center Y
     * @param hour Hour
     * @param minute Minute
     * @return TimeRangePickerOffset
     */
    fun byTime(hour: Float, minute: Float): TimeRangePickerOffset {
        val angle = (hour * TimeRangePickerAngle.ANGLE_24HOUR) + (minute * TimeRangePickerAngle.ANGLE_24HOUR_MINUTE)
        val radian = Math.toRadians(angle.toDouble()) - TimeRangePickerAngle.RADIAN
        val radius = this.x
        val x = (this.x + radius * Math.cos(radian)).toFloat()
        val y = (this.y + radius * Math.sin(radian)).toFloat()
        return Offset(x = x, y = y)
    }

    /**
     * Calculate degrees from offset
     * @param other TimeRangePickerOffset
     * @return Float
     */
    fun toDegrees(other: TimeRangePickerOffset) =
        Math.toDegrees(Math.atan2((this.y - other.y).toDouble(), (this.x - other.y).toDouble())).toFloat()

    /**
     * Calculate offset by degrees
     * @param degrees Float
     * @return TimeRangePickerOffset
     */
    fun byDegrees(degrees: Float): TimeRangePickerOffset {
        val angle = Math.toRadians(degrees.toDouble())
        val x = (this.x * Math.cos(angle)).toFloat() + this.x
        val y = (this.y * Math.sin(angle)).toFloat() + this.y
        return Offset(x = x, y = y)
    }

    fun toAngle(other: TimeRangePickerOffset): Double {
        var angle = this.toDegrees(other) + Math.toDegrees(TimeRangePickerAngle.RADIAN)
        if (angle < TimeRangePickerAngle.Zero) angle += TimeRangePickerAngle.MAX_ANGLE
        if (angle >= TimeRangePickerAngle.MAX_ANGLE) angle -= TimeRangePickerAngle.MAX_ANGLE
        return angle
    }


    @Immutable
    data class Offset(override val x: Float, override val y: Float): TimeRangePickerOffset

    @Immutable
    data class Default(override val x: Float = 0f, override val y: Float = 0f): TimeRangePickerOffset
}
