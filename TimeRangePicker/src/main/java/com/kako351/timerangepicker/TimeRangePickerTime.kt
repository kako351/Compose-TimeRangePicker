package com.kako351.timerangepicker

import androidx.compose.runtime.Stable

interface TimeFactory {
    fun createByDegrees(degrees: Double): Time
}

interface Time {
    var hour: Int
    var minute: Int
    val formatText: String

    @Stable
    data class TimeRangePicker24Time(override var hour: Int, override var minute: Int): Time {
        override val formatText: String
            get() = "%02d:%02d".format(hour, minute)

        companion object: TimeFactory {
            override fun createByDegrees(degrees: Double): Time {
                val hour = degrees / TimeRangePickerAngle.ANGLE_24HOUR
                val minute = (degrees % TimeRangePickerAngle.ANGLE_24HOUR) / TimeRangePickerAngle.ANGLE_24HOUR_MINUTE
                return TimeRangePicker24Time(hour = hour.toInt(), minute = minute.toInt())
            }
        }
    }
}
