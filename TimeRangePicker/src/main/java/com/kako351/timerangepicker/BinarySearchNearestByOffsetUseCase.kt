package com.kako351.timerangepicker

class BinarySearchNearestByOffsetUseCase {
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
