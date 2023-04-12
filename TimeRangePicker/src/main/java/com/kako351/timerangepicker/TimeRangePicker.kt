package com.kako351.timerangepicker

import android.graphics.Paint
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
    startHour: Float = 0f,
    startMinute: Float = 0f,
    endHour: Float = 12f,
    endMinute: Float = 0f,
) {
    val vector = ImageVector.vectorResource(id = R.drawable.baseline_access_time_24)
    val painter = rememberVectorPainter(image = vector)

    /**
     * Radius of area to accept drag gesture.
     */
    val dragStartAreaRadius = 100f

    /**
     * Center x Offset of the circle.
     */
    var centerX by remember {
        mutableStateOf(0f)
    }
    /**
     * Center y Offset of the circle.
     */
    var centerY by remember {
        mutableStateOf(0f)
    }
    /**
     * Dragged start time offset x
     */
    var startTimeDragOffsetX by remember {
        mutableStateOf(0f)
    }
    /**
     * Dragged start time offset y
     */
    var startTimeDragOffsetY by remember {
        mutableStateOf(0f)
    }
    /**
     * Dragged end time offset x
     */
    var endTimeDragOffsetX by remember {
        mutableStateOf(0f)
    }
    /**
     * Dragged end time offset y
     */
    var endTimeDragOffsetY by remember {
        mutableStateOf(0f)
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
    val startTimeDragAngle = remember(key1 = startTimeDragOffsetX, key2 = startTimeDragOffsetY) {
        derivedStateOf {
            Math.toDegrees(Math.atan2((startTimeDragOffsetY - centerY).toDouble(), (startTimeDragOffsetX - centerX).toDouble())).toFloat()
        }
    }
    /**
     * Calculate start time x offset from angle
     */
    val startAngleX = remember(key1 = startTimeDragAngle.value, key2 = centerX) {
        derivedStateOf {
            val radius = centerX - 50f
            val angle = Math.toRadians(startTimeDragAngle.value.toDouble())
            (radius * Math.cos(angle)).toFloat() + centerX
        }
    }
    /**
     * Calculate start time y offset from angle
     */
    val startAngleY = remember(key1 = startTimeDragAngle.value, key2 = centerY) {
        derivedStateOf {
            val radius = centerY - 50f
            val angle = Math.toRadians(startTimeDragAngle.value.toDouble())
            (radius * Math.sin(angle)).toFloat() + centerY
        }
    }
    /**
     * Calculate start time degrees from angle
     */
    val startTimeDegrees = remember(key1 = startTimeDragAngle.value) {
        derivedStateOf {
            var angle = startTimeDragAngle.value + Math.toDegrees(PI / 2)
            if (angle < 0) angle += 360f
            if (angle >= 360f) angle -= 360f
            angle
        }
    }
    /**
     * Calculate start hour from start time angle
     */
    val selectedStartHour = remember(key1 = startTimeDegrees.value) {
        derivedStateOf {
            (startTimeDegrees.value / 15).toInt()
        }
    }
    /**
     * Calculate start minute from start time angle
     */
    val selectedStartMinute = remember(key1 = startTimeDegrees.value) {
        derivedStateOf {
            ((startTimeDegrees.value % 15) / 15 * 60).toInt()
        }
    }
    /**
     * Calculate display start time text from start time angle
     */
    val startTime = remember(key1 = selectedStartHour.value, key2 = selectedStartMinute.value) {
        derivedStateOf {
            "%02d:%02d".format(selectedStartHour.value, selectedStartMinute.value)
        }
    }

    /**
     * Calculate end time angle from xy end time offset
     */
    val endTimeDragAngle = remember(key1 = endTimeDragOffsetX, key2 = endTimeDragOffsetY) {
        derivedStateOf { Math.toDegrees(Math.atan2((endTimeDragOffsetY - centerY).toDouble(), (endTimeDragOffsetX - centerX).toDouble())).toFloat() }
    }
    /**
     * Calculate end time x offset from angle
     */
    val endAngleX = remember(key1 = endTimeDragAngle.value, key2 = centerX) {
        derivedStateOf {
            val radius = centerX - 50f
            val angle = Math.toRadians(endTimeDragAngle.value.toDouble())
            (radius * Math.cos(angle)).toFloat() + centerX
        }
    }
    /**
     * Calculate end time y offset from angle
     */
    val endAngleY = remember(key1 = endTimeDragAngle.value, key2 = centerY) {
        derivedStateOf {
            val radius = centerY - 50f
            val angle = Math.toRadians(endTimeDragAngle.value.toDouble())
            (radius * Math.sin(angle)).toFloat() + centerY
        }
    }
    /**
     * Calculate end time degrees from angle
     */
    val endTimeDegrees = remember(key1 = endTimeDragAngle.value) {
        derivedStateOf {
            var angle = endTimeDragAngle.value + Math.toDegrees(PI / 2)
            if (angle < 0) angle += 360f
            if (angle >= 360f) angle -= 360f
            angle
        }
    }
    /**
     * Calculate end hour from end time angle
     */
    val selectedEndHour = remember(key1 = endTimeDegrees.value) {
        derivedStateOf {
            (endTimeDegrees.value / 15).toInt()
        }
    }
    /**
     * Calculate end minute from end time angle
     */
    val selectedEndMinute = remember(key1 = endTimeDegrees.value) {
        derivedStateOf {
            ((endTimeDegrees.value % 15) / 15 * 60).toInt()
        }
    }
    /**
     * Calculate display end time text from end time angle
     */
    val endTime = remember(key1 = selectedEndHour.value, key2 = selectedEndMinute.value) {
        derivedStateOf {
            "%02d:%02d".format(selectedEndHour.value, selectedEndMinute.value)
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .onSizeChanged {
                centerX = it.width / 2f
                centerY = it.height / 2f

                val defaultStartTimeOffset =
                    getOffsetByTime(centerX, centerY, startHour, startMinute)
                startTimeDragOffsetX = defaultStartTimeOffset.x
                startTimeDragOffsetY = defaultStartTimeOffset.y

                val defaultEndTimeOffset = getOffsetByTime(centerX, centerY, endHour, endMinute)
                endTimeDragOffsetX = defaultEndTimeOffset.x
                endTimeDragOffsetY = defaultEndTimeOffset.y
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
                            startTimeDragOffsetX = change.position.x
                            startTimeDragOffsetY = change.position.y
                        } else if (allowEndTimeDrag) {
                            endTimeDragOffsetX = change.position.x
                            endTimeDragOffsetY = change.position.y
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

        val startAngle = startTimeDragAngle.value
        var sweepAngle = endTimeDragAngle.value - startTimeDragAngle.value
        if(endTimeDragAngle.value < startTimeDragAngle.value) sweepAngle += 360f
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
                startTime.value,
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
                endTime.value,
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

/**
 * Calculate offset from time
 * @param centerX Center X
 * @param centerY Center Y
 * @param hour Hour
 * @param minute Minute
 * @return Offset
 */
private fun getOffsetByTime(centerX: Float, centerY: Float, hour: Float, minute: Float): Offset {
    val angle = (hour * 360 / 24) + (minute * (360 / 24 / 60))
    val radian = Math.toRadians(angle.toDouble()) - (PI / 2)
    val radius = centerX
    val x = (centerX + radius * Math.cos(radian)).toFloat()
    val y = (centerY + radius * Math.sin(radian)).toFloat()
    return Offset(x, y)
}

@Preview
@Composable
fun TimeRangePickerPreview() {
    TimeRangePicker()
}
