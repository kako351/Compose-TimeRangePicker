package com.kako351.timerangepicker

import org.junit.Test
import kotlin.math.abs
import kotlin.math.round


class TimeRangePicker24TimeTest {
    private lateinit var timeRangePicker24Time: Time
    private val minutespan = 60f / 10

    @Test
    fun testCreateByDegrees_byDegrees() {
        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(0.0f, minutespan)
        assert(timeRangePicker24Time.hour == 0)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "00:00")

        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(45.0f, minutespan)
        assert(timeRangePicker24Time.hour == 3)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "03:00")

        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(90.0f, minutespan)
        assert(timeRangePicker24Time.hour == 6)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "06:00")

        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(135.0f, minutespan)
        assert(timeRangePicker24Time.hour == 9)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "09:00")

        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(180.0f, minutespan)
        assert(timeRangePicker24Time.hour == 12)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "12:00")

        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(225.0f, minutespan)
        assert(timeRangePicker24Time.hour == 15)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "15:00")

        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(270.0f, minutespan)
        assert(timeRangePicker24Time.hour == 18)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "18:00")

        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(360.0f, minutespan)
        println("360f = $timeRangePicker24Time")
        assert(timeRangePicker24Time.hour == 24)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "24:00")
    }

    @Test
    fun testCreateByDegrees_byOffset() {
        val degrees = abs(calcDegrees(3, 0))

        timeRangePicker24Time = Time.TimeRangePicker24Time.createByDegrees(degrees, minutespan)
        assert(timeRangePicker24Time.hour == 3)
        assert(timeRangePicker24Time.minute == 0)
        assert(timeRangePicker24Time.formatText == "03:00")
    }

    private fun calcDegrees(hour: Int, minute: Int): Float {
        val centerOffset = TimeRangePickerOffset.Offset(x = 540f, y = 540f)
        val radius = centerOffset.x * 0.8f
        val angle = hour * TimeRangePickerAngle.ANGLE_24HOUR // 360度を24分割
        val radian = Math.toRadians(angle.toDouble()) - TimeRangePickerAngle.RADIAN
        val startX = round((centerOffset.x + radius * Math.cos(radian)).toFloat() * 10.0f) / 10.0f
        val startY = round((centerOffset.y + radius * Math.sin(radian)).toFloat() * 10.0f) / 10.0f
        val startOffset = TimeRangePickerOffset.Offset(x = startX, y = startY)
        return startOffset.toDegrees(centerOffset)
    }
}