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
     * Radius of area to accept drag gesture.
     */
    val dragStartAreaRadius = 100f

    /**
     * Center x Offset of the circle.
     */
    val centerX by remember(centerOffset) {
        derivedStateOf {
            centerOffset.x
        }
    }
    /**
     * Center y Offset of the circle.
     */
    val centerY by remember(centerOffset) {
        derivedStateOf {
            centerOffset.y
        }
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
     * Calculate start time x offset from angle
     */
    val startAngleX = remember(key1 = startAngle) {
        derivedStateOf {
//            val radius = centerOffset.x - 50f
            startAngle.x
        }
    }
    /**
     * Calculate start time y offset from angle
     */
    val startAngleY = remember(key1 = startAngle) {
        derivedStateOf {
//            val radius = centerY - 50f
            startAngle.y
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
     * Calculate start hour from start time angle
     */
    val selectedStartHour = remember(key1 = startTime) {
        derivedStateOf {
            startTime.hour
        }
    }
    /**
     * Calculate start minute from start time angle
     */
    val selectedStartMinute = remember(key1 = startTime) {
        derivedStateOf {
            startTime.minute
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
     * Calculate end time x offset from angle
     */
    val endAngleX = remember(key1 = endAngle) {
        derivedStateOf {
            endAngle.x
        }
    }
    /**
     * Calculate end time y offset from angle
     */
    val endAngleY = remember(key1 = endAngle) {
        derivedStateOf {
            endAngle.y
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
    /**
     * Calculate end hour from end time angle
     */
    val selectedEndHour = remember(key1 = endTime) {
        derivedStateOf {
            endTime.hour
        }
    }
    /**
     * Calculate end minute from end time angle
     */
    val selectedEndMinute = remember(key1 = endTime) {
        derivedStateOf {
            endTime.minute
        }
    }

    LaunchedEffect(selectedStartHour, selectedStartMinute, selectedEndHour, selectedEndMinute) {
        onChangedTimeRange(
            selectedStartHour.value,
            selectedStartMinute.value,
            selectedEndHour.value,
            selectedEndMinute.value
        )
    }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .onSizeChanged {
                centerOffset = TimeRangePickerOffset.Offset(it.width / 2f, it.height / 2f)

                val defaultStartTimeOffset =
                    centerOffset.byTime(startHour.toFloat(), startMinute.toFloat())
                startTimeDragOffset =
                    TimeRangePickerOffset.Offset(defaultStartTimeOffset.x, defaultStartTimeOffset.y)

                endTimeDragOffset = centerOffset.byTime(endHour.toFloat(), endMinute.toFloat())

            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        if (it.x in (startAngleX.value - dragStartAreaRadius)..(startAngleX.value + dragStartAreaRadius) && it.y in (startAngleY.value - dragStartAreaRadius)..(startAngleY.value + dragStartAreaRadius)) {
                            allowStartTimeDrag = true
                            return@detectDragGestures
                        }
                        allowStartTimeDrag = false
                        if (it.x in (endAngleX.value - dragStartAreaRadius)..(endAngleX.value + dragStartAreaRadius) && it.y in (endAngleY.value - dragStartAreaRadius)..(endAngleY.value + dragStartAreaRadius)) {
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
        drawArc(
            color = Color.LightGray,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(
                x = centerX - radius,
                y = centerY - radius
            ),
            size = Size(
                width = radius * 2,
                height = radius * 2
            ),
            style = Stroke(
                width = 70f
            ),
            alpha = 0.5f
        )
        for (i in 0..23) {
            val radius = size.width / 2 * 0.8f
            val angle =  i * (360 / 24) // 360度を24分割
            val radian = Math.toRadians(angle.toDouble()) - (PI / 2)
            val startX = (centerX + radius * Math.cos(radian)).toFloat()
            val startY = (centerY + radius * Math.sin(radian)).toFloat()
            val endX = (centerX + radius * 0.95 * Math.cos(radian)).toFloat()
            val endY = (centerY + radius * 0.95 * Math.sin(radian)).toFloat()
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
                val startX = (centerX + radius * Math.cos(radian)).toFloat()
                val startY = (centerY + radius * Math.sin(radian)).toFloat()
                val endX = (centerX + radius * 0.95 * Math.cos(radian)).toFloat()
                val endY = (centerY + radius * 0.95 * Math.sin(radian)).toFloat()
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

        val startAngle = startTimeDragAngle
        var sweepAngle = endTimeDragAngle - startTimeDragAngle
        if(endTimeDragAngle < startTimeDragAngle) sweepAngle += 360f
        drawArc(
            color = Color(0xFF2196F3),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(
                x = centerX - radius,
                y = centerY - radius
            ),
            size = Size(
                width = radius * 2,
                height = radius * 2
            ),
            style = Stroke(
                width = 70f,
                cap = StrokeCap.Round
            ),
        )

        translate(startAngleX.value - (painter.intrinsicSize.width / 2), startAngleY.value - (painter.intrinsicSize.height / 2.25f)) {
            with(painter) {
                draw(
                    painter.intrinsicSize,
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                )
            }
        }

        translate(endAngleX.value - (painter.intrinsicSize.width / 2), endAngleY.value - (painter.intrinsicSize.height / 1.75f)) {
            with(painter) {
                draw(
                    painter.intrinsicSize,
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                )
            }
        }

        drawIntoCanvas {
            it.nativeCanvas.drawText(
                "開始時間",
                centerX,
                centerY - ((centerY / 10) * 3),
                Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 30f
                    textAlign = Paint.Align.CENTER
                }
            )
            it.nativeCanvas.drawText(
                startTime.formatText,
                centerX,
                centerY - (centerY / 10),
                Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 100f
                    textAlign = Paint.Align.CENTER
                }
            )
            it.nativeCanvas.drawText(
                "終了時間",
                centerX,
                centerY + ((centerY / 10) * 1),
                Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 30f
                    textAlign = Paint.Align.CENTER
                }
            )
            it.nativeCanvas.drawText(
                endTime.formatText,
                centerX,
                centerY + ((centerY / 10) * 3),
                Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 100f
                    textAlign = Paint.Align.CENTER
                }
            )
        }
    }
}

@Preview
@Composable
fun TimeRangePickerPreview() {
    TimeRangePicker(onChangedTimeRange = {_, _, _, _ -> })
}

