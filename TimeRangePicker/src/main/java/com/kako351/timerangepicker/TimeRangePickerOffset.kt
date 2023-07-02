package com.kako351.timerangepicker

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.android.parcel.Parcelize

@Parcelize
sealed interface TimeRangePickerOffset : Parcelable {
    val x: Float
    val y: Float

    private val dragStartAreaRadius: Float
        get() = 100f

    /**
     * Calculate offset from time
     * @param hour Hour
     * @param minute Minute
     * @return TimeRangePickerOffset
     */
    fun byTime(hour: Int, minute: Int): TimeRangePickerOffset {
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

    fun toAngle(other: TimeRangePickerOffset): Float {
        var angle = this.toDegrees(other) + Math.toDegrees(TimeRangePickerAngle.RADIAN)
        if (angle < TimeRangePickerAngle.Zero) angle += TimeRangePickerAngle.MAX_ANGLE
        if (angle >= TimeRangePickerAngle.MAX_ANGLE) angle -= TimeRangePickerAngle.MAX_ANGLE
        return angle.toFloat()
    }

    fun inDraggableArea(x: Float, y: Float): Boolean =
        x in (this.x - dragStartAreaRadius)..(this.x + dragStartAreaRadius) && y in (this.y - dragStartAreaRadius)..(this.y + dragStartAreaRadius)


    @Immutable
    data class Offset(override val x: Float, override val y: Float): TimeRangePickerOffset

    @Immutable
    data class Default(override val x: Float = 0f, override val y: Float = 0f): TimeRangePickerOffset
}
