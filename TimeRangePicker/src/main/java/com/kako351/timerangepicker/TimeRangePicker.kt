package com.kako351.timerangepicker

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import java.lang.Math.PI

/**
 * TimeRangePicker is a UI component that allows the user to select a time range.
 * It represents a selected time range between the start and end times.
 * @property modifier Modifier to be applied to the layout.
 * @property startTime The start time of the time range.
 * @property endTime The end time of the time range.
 * @property onChangedTimeRange Callback that is invoked when the time range is changed.
 */
@Composable
fun TimeRangePicker(
    modifier: Modifier = Modifier,
    startTime: Time,
    endTime: Time,
    clockBarStyle: TimeRangePickerRangeBarStyle = Default,
    rangeBarStyle: TimeRangePickerRangeBarStyle = RangeBarStyle.Default,
    onChangedTimeRange: (startTime: Time, endTime: Time) -> Unit
) = TimeRangePicker(
    modifier = modifier,
    startHour = startTime.hour,
    startMinute = startTime.minute,
    endHour = endTime.hour,
    endMinute = endTime.minute,
    clockBarStyle = clockBarStyle,
    rangeBarStyle = rangeBarStyle,
    onChangedTimeRange = { startHour, startMinute, endHour, endMinute ->
        onChangedTimeRange(
            Time.TimeRangePicker24Time(startHour, startMinute),
            Time.TimeRangePicker24Time(endHour, endMinute)
        )
    }
)
/**
 * TimeRangePicker is a UI component that allows the user to select a time range.
 * It represents a selected time range between the start and end times.
 * @property modifier Modifier to be applied to the layout.
 * @property startHour The start hour of the time range.
 * @property startMinute The start minute of the time range.
 * @property endHour The end hour of the time range.
 * @property endMinute The end minute of the time range.
 * @property onChangedTimeRange Callback that is invoked when the time range is changed.
 */
@Composable
fun TimeRangePicker(
    modifier: Modifier = Modifier,
    startHour: Int = TimeRangePickerDateTime.Zero,
    startMinute: Int = TimeRangePickerDateTime.Zero,
    endHour: Int = TimeRangePickerDateTime.NoonHour,
    endMinute: Int = TimeRangePickerDateTime.Zero,
    clockBarStyle: TimeRangePickerRangeBarStyle = Default,
    rangeBarStyle: TimeRangePickerRangeBarStyle = RangeBarStyle.Default,
    onChangedTimeRange: (startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) -> Unit
) {
    val vector = ImageVector.vectorResource(id = R.drawable.baseline_access_time_24)
    val painter = rememberVectorPainter(image = vector)

    var centerOffset: TimeRangePickerOffset by remember {
        mutableStateOf(TimeRangePickerOffset.Default())
    }

    var startTimeDragOffset: TimeRangePickerOffset by remember {
        mutableStateOf(TimeRangePickerOffset.Default())
    }

    var endTimeDragOffset: TimeRangePickerOffset by remember {
        mutableStateOf(TimeRangePickerOffset.Default())
    }

    /**
     * Allow dragging of start time
     */
    var allowStartTimeDrag by remember {
        mutableStateOf(false)
    }
    /**
     * Allow dragging of end time
     */
    var allowEndTimeDrag by remember {
        mutableStateOf(false)
    }
    /**
     * Calculate start time angle from xy start time offset
     */
    val startTimeDragAngle by remember(key1 = startTimeDragOffset, key2 = centerOffset) {
        derivedStateOf {
            startTimeDragOffset.toDegrees(centerOffset)
        }
    }

    val startAngle by remember(key1 = centerOffset, key2 = startTimeDragAngle) {
        derivedStateOf {
            centerOffset.byDegrees(startTimeDragAngle)
        }
    }
    /**
     * Calculate start time degrees from angle
     */
    val startTimeDegrees = remember(key1 = startAngle, key2 = centerOffset) {
        derivedStateOf {
            startAngle.toAngle(centerOffset)
        }
    }

    val startTime by remember(startTimeDegrees) {
        derivedStateOf {
            Time.TimeRangePicker24Time.createByDegrees(startTimeDegrees.value)
        }
    }

    /**
     * Calculate end time angle from xy end time offset
     */
    val endTimeDragAngle by remember(key1 = endTimeDragOffset, key2 = centerOffset) {
        derivedStateOf { endTimeDragOffset.toDegrees(centerOffset) }
    }
    val endAngle by remember(key1 = centerOffset, key2 = endTimeDragAngle) {
        derivedStateOf {
            centerOffset.byDegrees(endTimeDragAngle)
        }
    }
    /**
     * Calculate end time degrees from angle
     */
    val endTimeDegrees = remember(key1 = endAngle, key2 = centerOffset) {
        derivedStateOf {
            endAngle.toAngle(centerOffset)
        }
    }
    val endTime by remember(endTimeDegrees) {
        derivedStateOf {
            Time.TimeRangePicker24Time.createByDegrees(endTimeDegrees.value)
        }
    }

    LaunchedEffect(startTime, endTime) {
        onChangedTimeRange(
            startTime.hour,
            startTime.minute,
            endTime.hour,
            endTime.minute
        )
    }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .onSizeChanged {
                centerOffset = TimeRangePickerOffset.Offset(it.width / 2f, it.height / 2f)
                startTimeDragOffset = centerOffset.byTime(startHour.toFloat(), startMinute.toFloat())
                endTimeDragOffset = centerOffset.byTime(endHour.toFloat(), endMinute.toFloat())
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        if (startAngle.inDraggableArea(x = it.x, y = it.y)) {
                            allowStartTimeDrag = true
                            return@detectDragGestures
                        }
                        allowStartTimeDrag = false
                        if (endAngle.inDraggableArea(x = it.x, y = it.y)) {
                            allowEndTimeDrag = true
                            return@detectDragGestures
                        }
                        allowEndTimeDrag = false
                    },
                    onDragEnd = {},
                    onDragCancel = { },
                    onDrag = { change, dragAmount ->
                        if (!allowStartTimeDrag && !allowEndTimeDrag) return@detectDragGestures
                        change.consume()
                        if (allowStartTimeDrag) {
                            startTimeDragOffset =
                                TimeRangePickerOffset.Offset(change.position.x, change.position.y)
                        } else if (allowEndTimeDrag) {
                            endTimeDragOffset =
                                TimeRangePickerOffset.Offset(change.position.x, change.position.y)
                        }
                    }
                )
            }
    ) {
        val radius = size.width / 2 * 0.9f
        DrawClockArc(
            style = clockBarStyle.copy(
                centerOffset = centerOffset,
                radius = radius,
            )
        )
        DrawTimeRangeArc(
            style = rangeBarStyle.copy(
                centerOffset = centerOffset,
                radius = radius,
            ),
            startTimeDragAngle = startTimeDragAngle,
            endTimeDragAngle = endTimeDragAngle
        )

        DrawClock24Hour(centerOffset)

        translate(startAngle.x - (painter.intrinsicSize.width / 2), startAngle.y - (painter.intrinsicSize.height / 2.25f)) {
            with(painter) {
                draw(
                    painter.intrinsicSize,
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                )
            }
        }

        translate(endAngle.x - (painter.intrinsicSize.width / 2), endAngle.y - (painter.intrinsicSize.height / 1.75f)) {
            with(painter) {
                draw(
                    painter.intrinsicSize,
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                )
            }
        }
        DrawDigitalClockText(
            centerOffset = centerOffset,
            startTime = startTime,
            endTime = endTime
        )
    }
}

private fun DrawScope.DrawClockArc(
    centerOffset: TimeRangePickerOffset,
    radius: Float,
    color: Color,
    strokeWidth: Float,
    alpha: Float = 0.5f,
) {
    DrawClockArc(
        RangeBarStyle(
            centerOffset = centerOffset,
            radius = radius,
            color = color,
            width = strokeWidth,
            alpha = alpha
        )
    )
}

private fun DrawScope.DrawClockArc(
    style: TimeRangePickerRangeBarStyle
) {
    DrawArc(style = style)
}

private fun DrawScope.DrawTimeRangeArc(
    centerOffset: TimeRangePickerOffset,
    startTimeDragAngle: Float,
    endTimeDragAngle: Float,
    radius: Float,
    color: Color,
    strokeWidth: Float,
    cap: StrokeCap = StrokeCap.Round,
) {
    DrawTimeRangeArc(
        style = RangeBarStyle(
            centerOffset = centerOffset,
            radius = radius,
            color = color,
            width = strokeWidth,
            cap = cap
        ),
        startTimeDragAngle = startTimeDragAngle,
        endTimeDragAngle = endTimeDragAngle
    )
}

private fun DrawScope.DrawTimeRangeArc(
    style: TimeRangePickerRangeBarStyle,
    startTimeDragAngle: Float,
    endTimeDragAngle: Float,
) {
    val startAngle = startTimeDragAngle
    var sweepAngle = endTimeDragAngle - startTimeDragAngle
    if(endTimeDragAngle < startTimeDragAngle) sweepAngle += TimeRangePickerAngle.MAX_ANGLE
    DrawArc(style = style.copy(
        startAngle = startAngle,
        sweepAngle = sweepAngle
    ))
}

private fun DrawScope.DrawArc(
    style: TimeRangePickerRangeBarStyle
) {
    drawArc(
        color = style.color,
        startAngle = style.startAngle,
        sweepAngle = style.sweepAngle,
        useCenter = false,
        topLeft = style.topLeft,
        size = style.size,
        style = style.strokeStyle,
        alpha = style.alpha
    )
}

private fun DrawScope.DrawClock24Hour(
    centerOffset: TimeRangePickerOffset
) {
    for (i in 0..23) {
        val radius = size.width / 2 * 0.8f
        val angle =  i * (360 / 24) // 360度を24分割
        val radian = Math.toRadians(angle.toDouble()) - (PI / 2)
        val startX = (centerOffset.x + radius * Math.cos(radian)).toFloat()
        val startY = (centerOffset.y + radius * Math.sin(radian)).toFloat()
        val endX = (centerOffset.x + radius * 0.95 * Math.cos(radian)).toFloat()
        val endY = (centerOffset.y + radius * 0.95 * Math.sin(radian)).toFloat()
        if(i % 6 == 0) {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "${i}",
                    endX,
                    endY + 10f,
                    Paint().apply {
                        color = Color.Black.toArgb()
                        textSize = 45f
                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        } else {
            drawLine(
                color = Color.Black,
                start = Offset(
                    x = startX,
                    y = startY
                ),
                end = Offset(
                    x = endX,
                    y = endY
                ),
                cap = StrokeCap.Round,
                strokeWidth = 3f
            )
        }
        for (j in 1..5) {
            if(i % 6 == 5 && j == 5) continue
            if(i % 6 == 0 && j == 1) continue
            // 分針を描画
            val minuteAngle = angle + (j * (360f / 24 / 6))  // 360度を24分割
            val radian = Math.toRadians(minuteAngle.toDouble())
            val startX = (centerOffset.x + radius * Math.cos(radian)).toFloat()
            val startY = (centerOffset.y + radius * Math.sin(radian)).toFloat()
            val endX = (centerOffset.x + radius * 0.95 * Math.cos(radian)).toFloat()
            val endY = (centerOffset.y + radius * 0.95 * Math.sin(radian)).toFloat()
            drawLine(
                color = Color.Gray,
                start = Offset(
                    x = startX,
                    y = startY
                ),
                end = Offset(
                    x = endX,
                    y = endY
                ),
                alpha = 0.5f,
                cap = StrokeCap.Round,
                strokeWidth = 2f
            )
        }
    }
}

private fun DrawScope.DrawDigitalClockText(
    centerOffset: TimeRangePickerOffset,
    startTime: Time,
    endTime: Time
) {
    drawIntoCanvas {
        it.nativeCanvas.drawText(
            "開始時間",
            centerOffset.x,
            centerOffset.y - ((centerOffset.y / 10) * 3),
            Paint().apply {
                color = Color.Black.toArgb()
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }
        )
        it.nativeCanvas.drawText(
            startTime.formatText,
            centerOffset.x,
            centerOffset.y - (centerOffset.y / 10),
            Paint().apply {
                color = Color.Black.toArgb()
                textSize = 100f
                textAlign = Paint.Align.CENTER
            }
        )
        it.nativeCanvas.drawText(
            "終了時間",
            centerOffset.x,
            centerOffset.y + ((centerOffset.y / 10) * 1),
            Paint().apply {
                color = Color.Black.toArgb()
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }
        )
        it.nativeCanvas.drawText(
            endTime.formatText,
            centerOffset.x,
            centerOffset.y + ((centerOffset.y / 10) * 3),
            Paint().apply {
                color = Color.Black.toArgb()
                textSize = 100f
                textAlign = Paint.Align.CENTER
            }
        )
    }
}

@Preview
@Composable
private fun TimeRangePickerPreview() {
    TimeRangePicker(onChangedTimeRange = {_, _, _, _ -> })
}

