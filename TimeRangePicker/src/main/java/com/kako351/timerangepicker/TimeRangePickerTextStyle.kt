package com.kako351.timerangepicker

import android.graphics.Paint.Align
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

interface TimeRangePickerTextStyle {
    val color: Color
    val fontSize: TextUnit
    val fontWeight: FontWeight
    val textAlign: Align
    val alpha: Float
}

@Immutable
data class TimeRangePickerLabelTextStyle(
    override val color: Color = Color.Black,

    override val fontSize: TextUnit = 12.sp,

    override val fontWeight: FontWeight = FontWeight.Normal,

    override val textAlign: Align = Align.CENTER,

    override val alpha: Float = 1f
): TimeRangePickerTextStyle

@Immutable
data class TimeRangePickerTimeTextStyle(
    override val color: Color = Color.Black,

    override val fontSize: TextUnit = 24.sp,

    override val fontWeight: FontWeight = FontWeight.Normal,

    override val textAlign: Align = Align.CENTER,

    override val alpha: Float = 1f
): TimeRangePickerTextStyle

@Immutable
data class TimeRangePickerClockHourTextStyle(
    override val color: Color = Color.Black,

    override val fontSize: TextUnit = 10.sp,

    override val fontWeight: FontWeight = FontWeight.Normal,

    override val textAlign: Align = Align.CENTER,

    override val alpha: Float = 1f
): TimeRangePickerTextStyle
