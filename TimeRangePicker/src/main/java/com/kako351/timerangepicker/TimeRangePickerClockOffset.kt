package com.kako351.timerangepicker

data class TimeRangePickerClockOffset(
    val startOffset: TimeRangePickerOffset.Offset,
    val endOffset: TimeRangePickerOffset.Offset,
    val degrees: Float,
    val type: TimeRangePickerClockOffsetType,
    val time: Time
)

enum class TimeRangePickerClockOffsetType {
    HOUR,
    MINUTE
}
