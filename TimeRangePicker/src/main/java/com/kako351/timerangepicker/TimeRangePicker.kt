package com.kako351.timerangepicker

import android.content.res.Configuration
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
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
    hourSpan: Int = 6,
    startTimeLabel: String = stringResource(id = R.string.start_time_label),
    endTimeLabel: String = stringResource(id = R.string.start_time_label),
    labelStyle: TimeRangePickerTextStyle = TimeRangePickerLabelTextStyle(),
    timeStyle: TimeRangePickerTextStyle = TimeRangePickerTimeTextStyle(),
    onChangedTimeRange: (startTime: Time, endTime: Time) -> Unit
) = TimeRangePicker(
    modifier = modifier,
    startHour = startTime.hour,
    startMinute = startTime.minute,
    endHour = endTime.hour,
    endMinute = endTime.minute,
    hourSpan = hourSpan,
    clockBarStyle = clockBarStyle,
    rangeBarStyle = rangeBarStyle,
    startTimeLabel = startTimeLabel,
    endTimeLabel = endTimeLabel,
    labelStyle = labelStyle,
    timeStyle = timeStyle,
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
    hourSpan: Int = 6,
    clockBarStyle: TimeRangePickerRangeBarStyle = Default,
    rangeBarStyle: TimeRangePickerRangeBarStyle = RangeBarStyle.Default,
    startTimeLabel: String = stringResource(id = R.string.start_time_label),
    endTimeLabel: String = stringResource(id = R.string.end_time_label),
    labelStyle: TimeRangePickerTextStyle = TimeRangePickerLabelTextStyle(),
    timeStyle: TimeRangePickerTextStyle = TimeRangePickerTimeTextStyle(),
    onChangedTimeRange: (startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) -> Unit
) {
    val binarySearchNearestByOffsetUseCase = BinarySearchNearestByOffsetUseCase()

    var startTime: Time by rememberSaveable {
        mutableStateOf(Time.TimeRangePicker24Time(startHour, startMinute))
    }

    var endTime: Time by rememberSaveable {
        mutableStateOf(Time.TimeRangePicker24Time(endHour, endMinute))
    }

    var centerOffset: TimeRangePickerOffset by rememberSaveable {
        mutableStateOf(TimeRangePickerOffset.Default())
    }

    var startTimeDragOffset: TimeRangePickerOffset by rememberSaveable {
        mutableStateOf(centerOffset.byTime(startHour, startMinute))
    }

    var endTimeDragOffset: TimeRangePickerOffset by rememberSaveable {
        mutableStateOf(centerOffset.byTime(endHour, endMinute))
    }

    /**
     * Allow dragging of start time
     */
    var allowStartTimeDrag by rememberSaveable {
        mutableStateOf(false)
    }
    /**
     * Allow dragging of end time
     */
    var allowEndTimeDrag by rememberSaveable {
        mutableStateOf(false)
    }

    /**
     * Calculate start time angle from xy start time offset
     */
    val startTimeDragAngle by remember(key1 = startTimeDragOffset, key2 = centerOffset) {
        derivedStateOf { startTimeDragOffset.toDegrees(centerOffset) }
    }

    /**
     * Calculate end time angle from xy end time offset
     */
    val endTimeDragAngle by remember(key1 = endTimeDragOffset, key2 = centerOffset) {
        derivedStateOf { endTimeDragOffset.toDegrees(centerOffset) }
    }

    LaunchedEffect(startTime, endTime) {
        onChangedTimeRange(
            startTime.hour,
            startTime.minute,
            endTime.hour,
            endTime.minute
        )
    }

    var size by remember {
        mutableStateOf(Size.Zero)
    }

    val hourOffset: State<List<TimeRangePickerClockOffset>> = remember(key1 = centerOffset, key2 = size) {
        derivedStateOf {
            setHourOffset(centerOffset, size)
        }
    }

    Canvas(
        modifier = modifier
            .then(
                when (LocalConfiguration.current.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> Modifier.fillMaxHeight()
                    else -> Modifier.fillMaxWidth()
                }
            )
            .aspectRatio(1f)
            .onSizeChanged {
                size = Size(width = it.width.toFloat(), height = it.height.toFloat())
                centerOffset = TimeRangePickerOffset.Offset(it.width / 2f, it.height / 2f)
                startTimeDragOffset = centerOffset.byTime(startTime.hour, startTime.minute)
                endTimeDragOffset = centerOffset.byTime(endTime.hour, endTime.minute)
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        if (startTimeDragOffset.inDraggableArea(x = it.x, y = it.y)) {
                            allowStartTimeDrag = true
                            return@detectDragGestures
                        }
                        allowStartTimeDrag = false
                        if (endTimeDragOffset.inDraggableArea(x = it.x, y = it.y)) {
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
                            TimeRangePickerOffset
                                .Offset(change.position.x, change.position.y)
                                .let {
                                    val degrees = it.toDegrees(centerOffset)
                                    val offset = binarySearchNearestByOffsetUseCase(hourOffset.value, degrees)
                                    startTimeDragOffset = offset.let {
                                        centerOffset.byDegrees(it.degrees)
                                    }
                                    startTime = Time.TimeRangePicker24Time.createByDegrees(
                                        startTimeDragOffset.toAngle(centerOffset)
                                    )
                                }
                        } else if (allowEndTimeDrag) {
                            TimeRangePickerOffset
                                .Offset(change.position.x, change.position.y)
                                .let {
                                    val degrees = it.toDegrees(centerOffset)
                                    val offset = binarySearchNearestByOffsetUseCase(hourOffset.value, degrees)
                                    endTimeDragOffset = offset.let {
                                        centerOffset.byDegrees(it.degrees)
                                    }
                                    endTime = Time.TimeRangePicker24Time.createByDegrees(
                                        endTimeDragOffset.toAngle(centerOffset)
                                    )
                                }
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

        DrawClock24Hour(
            centerOffset,
            hourSpan = hourSpan,
        )

        DrawDigitalClockText(
            centerOffset = centerOffset,
            startTime = startTime,
            endTime = endTime,
            startTimeLabel = startTimeLabel,
            endTimeLabel = endTimeLabel,
            labelStyle = labelStyle,
            timeStyle = timeStyle
        )
    }
}

private fun DrawScope.DrawClockArc(
    style: TimeRangePickerRangeBarStyle
) {
    DrawArc(style = style)
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
    centerOffset: TimeRangePickerOffset,
    hourSpan: Int = 6,
    hourTextStyle: TimeRangePickerClockHourTextStyle = TimeRangePickerClockHourTextStyle(),
) {
    for (i in 0..23) {
        val radius = size.width / 2 * 0.8f
        val angle =  i * TimeRangePickerAngle.ANGLE_24HOUR // 360度を24分割
        val radian = Math.toRadians(angle.toDouble()) - TimeRangePickerAngle.RADIAN
        val startX = (centerOffset.x + radius * Math.cos(radian)).toFloat()
        val startY = (centerOffset.y + radius * Math.sin(radian)).toFloat()
        val endX = (centerOffset.x + radius * 0.95 * Math.cos(radian)).toFloat()
        val endY = (centerOffset.y + radius * 0.95 * Math.sin(radian)).toFloat()
        if(i % hourSpan == 0) {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "${i}",
                    endX,
                    endY + 10f,
                    Paint().apply {
                        color = hourTextStyle.color.toArgb()
                        textSize = hourTextStyle.fontSize.toPx()
                        textAlign = hourTextStyle.textAlign
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
            if(i % hourSpan == (hourSpan - 1) && j == 5) continue
            if(i % hourSpan == 0 && j == 1) continue
            // 分針を描画
            val minuteAngle = angle + (j * (TimeRangePickerAngle.ANGLE_24HOUR / 6))  // 360度を24分割
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
    endTime: Time,
    startTimeLabel: String,
    endTimeLabel: String,
    labelStyle: TimeRangePickerTextStyle = TimeRangePickerLabelTextStyle(),
    timeStyle: TimeRangePickerTextStyle = TimeRangePickerTimeTextStyle(),
) {
    drawIntoCanvas {
        it.nativeCanvas.drawText(
            startTimeLabel,
            centerOffset.x,
            centerOffset.y - ((centerOffset.y / 10) * 3),
            Paint().apply {
                color = labelStyle.color.toArgb()
                textSize = labelStyle.fontSize.toPx()
                textAlign = labelStyle.textAlign
            }
        )
        it.nativeCanvas.drawText(
            startTime.formatText,
            centerOffset.x,
            centerOffset.y - (centerOffset.y / 10),
            Paint().apply {
                color = timeStyle.color.toArgb()
                textSize = timeStyle.fontSize.toPx()
                textAlign = timeStyle.textAlign
            }
        )
        it.nativeCanvas.drawText(
            endTimeLabel,
            centerOffset.x,
            centerOffset.y + ((centerOffset.y / 10) * 1),
            Paint().apply {
                color = labelStyle.color.toArgb()
                textSize = labelStyle.fontSize.toPx()
                textAlign = labelStyle.textAlign
            }
        )
        it.nativeCanvas.drawText(
            endTime.formatText,
            centerOffset.x,
            centerOffset.y + ((centerOffset.y / 10) * 3),
            Paint().apply {
                color = timeStyle.color.toArgb()
                textSize = timeStyle.fontSize.toPx()
                textAlign = timeStyle.textAlign
            }
        )
    }
}

@Preview
@Composable
private fun TimeRangePickerPreview() {
    TimeRangePicker(onChangedTimeRange = {_, _, _, _ -> })
}

private fun setHourOffset(
    centerOffset: TimeRangePickerOffset,
    size: Size
) : List<TimeRangePickerClockOffset> {
    val offsets: MutableList<TimeRangePickerClockOffset> = mutableListOf()
    for (i in 0..23) {
        val radius = size.width / 2 * 0.8f
        val angle = i * TimeRangePickerAngle.ANGLE_24HOUR // 360度を24分割
        val radian = Math.toRadians(angle.toDouble()) - (PI / 2)
        val startX = (centerOffset.x + radius * Math.cos(radian)).toFloat()
        val startY = (centerOffset.y + radius * Math.sin(radian)).toFloat()
        val endX = (centerOffset.x + radius * 0.95 * Math.cos(radian)).toFloat()
        val endY = (centerOffset.y + radius * 0.95 * Math.sin(radian)).toFloat()

        val startOffset = TimeRangePickerOffset.Offset(x = startX, y = startY)
        val endOffset = TimeRangePickerOffset.Offset(x = endX, y = endY)

        offsets.add(
            TimeRangePickerClockOffset(
                startOffset = startOffset,
                endOffset = endOffset,
                degrees = startOffset.toDegrees(centerOffset),
                type = TimeRangePickerClockOffsetType.HOUR,
                time = Time.TimeRangePicker24Time.createByDegrees(startOffset.toDegrees(centerOffset))
            )
        )

        for (j in 1..5) {
            val minuteAngle = angle + (j * (TimeRangePickerAngle.ANGLE_24HOUR / 6))  // 360度を24分割
            val radian = Math.toRadians(minuteAngle.toDouble())
            val startX = (centerOffset.x + radius * Math.cos(radian)).toFloat()
            val startY = (centerOffset.y + radius * Math.sin(radian)).toFloat()
            val endX = (centerOffset.x + radius * 0.95 * Math.cos(radian)).toFloat()
            val endY = (centerOffset.y + radius * 0.95 * Math.sin(radian)).toFloat()

            val startOffset = TimeRangePickerOffset.Offset(x = startX, y = startY)
            val endOffset = TimeRangePickerOffset.Offset(x = endX, y = endY)

            offsets.add(
                TimeRangePickerClockOffset(
                    startOffset = startOffset,
                    endOffset = endOffset,
                    degrees = startOffset.toDegrees(centerOffset),
                    type = TimeRangePickerClockOffsetType.MINUTE,
                    time = Time.TimeRangePicker24Time.createByDegrees(startOffset.toDegrees(centerOffset))
                )
            )
        }
    }

    return offsets.sortedBy { it.degrees }
}
