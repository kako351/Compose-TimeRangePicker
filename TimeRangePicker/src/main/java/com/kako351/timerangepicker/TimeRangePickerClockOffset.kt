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

class BinarySearchOffsetUseCase() {
    operator fun invoke(
        list: List<TimeRangePickerClockOffset>,
        degrees: Float
    ): TimeRangePickerClockOffset {
        val degreesList = list.map { it.degrees }
        val upperIndex = degreesList.binarySearch(degrees).let {
            when {
                it < -1 -> -it - 2
                it == -1 -> 0
                else -> it
            }
        }
        val lowerIndex = degreesList.binarySearch(degrees).let {
            when {
                it < -1 -> -it - 2
                it == -1 -> 0
                else -> it
            }
        }
        val upper = degreesList.elementAt(upperIndex)
        val lower = degreesList.elementAt(lowerIndex)
        val nealyIndex = when(Math.abs(degrees - upper).compareTo(Math.abs(degrees - lower))) {
            1 -> lowerIndex
            else -> upperIndex
        }

        return list.elementAt(nealyIndex)
    }
}
