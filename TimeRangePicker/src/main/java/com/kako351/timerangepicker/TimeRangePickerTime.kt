package com.kako351.timerangepicker

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

interface TimeFactory {
    fun createByDegrees(degrees: Float, minuteSpan: Float = TimeRangePickerAngle.ANGLE_24HOUR_MINUTE): Time
}

@Parcelize
sealed interface Time: Parcelable {
    var hour: Int
    var minute: Int
    val formatText: String

    @Parcelize
    @Stable
    data class TimeRangePicker24Time(override var hour: Int, override var minute: Int): Time, Parcelable {
        override val formatText: String
            get() = "%02d:%02d".format(hour, minute)

        companion object: TimeFactory {
            override fun createByDegrees(degrees: Float, minuteSpan: Float): Time {
                val hour = degrees / TimeRangePickerAngle.ANGLE_24HOUR
                val minute = degrees % TimeRangePickerAngle.ANGLE_24HOUR / minuteSpan
                return TimeRangePicker24Time(hour = hour.toInt(), minute = minute.toInt())
            }
        }
    }
}
