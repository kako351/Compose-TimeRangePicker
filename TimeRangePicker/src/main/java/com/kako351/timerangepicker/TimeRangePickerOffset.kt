package com.kako351.timerangepicker

import androidx.compose.runtime.Immutable

interface TimeRangePickerOffset {
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
        val angle = (hour * TimeRangePickerAngle.HOUR_ANGLE) + (minute * TimeRangePickerAngle.MINUTE_ANGLE)
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


    @Immutable
    data class Offset(override val x: Float = 0f, override val y: Float = 0f): TimeRangePickerOffset
}
