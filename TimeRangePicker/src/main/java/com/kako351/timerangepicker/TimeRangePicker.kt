package com.kako351.timerangepicker

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
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
 * Jetpack Composeで時刻を選択するコンポーネント
 */
@Composable
fun TimeRangePicker(
    modifier: Modifier = Modifier,
    startHour: Float = 0f,
    startMinute: Float = 0f,
    endHour: Float = 12f,
    endMinute: Float = 0f
) {
    val vector = ImageVector.vectorResource(id = R.drawable.baseline_access_time_24)
    val painter = rememberVectorPainter(image = vector)

    val dragStartAreaRadius = 100f

    var centerX by remember {
        mutableStateOf(0f)
    }
    var centerY by remember {
        mutableStateOf(0f)
    }
    var startTimeDragOffsetX by remember {
        mutableStateOf(0f)
    }
    var startTimeDragOffsetY by remember {
        mutableStateOf(0f)
    }
    var endTimeDragOffsetX by remember {
        mutableStateOf(0f)
    }
    var endTimeDragOffsetY by remember {
        mutableStateOf(0f)
    }
    var allowStartTimeDrag by remember {
        mutableStateOf(false)
    }
    var allowEndTimeDrag by remember {
        mutableStateOf(false)
    }
    // xy座標からAngleを決める
    val startAngleAtan = remember(key1 = startTimeDragOffsetX, key2 = startTimeDragOffsetY) {
        derivedStateOf {
            Math.toDegrees(Math.atan2((startTimeDragOffsetY - centerY).toDouble(), (startTimeDragOffsetX - centerX).toDouble())).toFloat()
        }
    }
    // startAngleAtanと半径からxy座標を決める
    val startAngleX = remember(key1 = startAngleAtan.value, key2 = centerX) {
        derivedStateOf {
            val radius = centerX - 50f
            val angle = Math.toRadians(startAngleAtan.value.toDouble())
            (radius * Math.cos(angle)).toFloat() + centerX
        }
    }
    val startAngleY = remember(key1 = startAngleAtan.value, key2 = centerY) {
        derivedStateOf {
            val radius = centerY - 50f
            val angle = Math.toRadians(startAngleAtan.value.toDouble())
            (radius * Math.sin(angle)).toFloat() + centerY
        }
    }

    // xy座標からAngleを決める
    val endAngleAtan = remember(key1 = endTimeDragOffsetX, key2 = endTimeDragOffsetY) {
        derivedStateOf { Math.toDegrees(Math.atan2((endTimeDragOffsetY - centerY).toDouble(), (endTimeDragOffsetX - centerX).toDouble())).toFloat() }
    }
    val endAngleX = remember(key1 = endAngleAtan.value, key2 = centerX) {
        derivedStateOf {
            val radius = centerX - 50f
            val angle = Math.toRadians(endAngleAtan.value.toDouble())
            (radius * Math.cos(angle)).toFloat() + centerX
        }
    }
    val endAngleY = remember(key1 = endAngleAtan.value, key2 = centerY) {
        derivedStateOf {
            val radius = centerY - 50f
            val angle = Math.toRadians(endAngleAtan.value.toDouble())
            (radius * Math.sin(angle)).toFloat() + centerY
        }
    }
    val startTime = remember(key1 = startAngleAtan.value) {
        derivedStateOf {
            var angle = startAngleAtan.value + Math.toDegrees(PI / 2)
            if (angle < 0) angle += 360f
            if (angle >= 360f) angle -= 360f
            val hour = (angle / 15).toInt()
            val minute = ((angle % 15) / 15 * 60).toInt()
            "%02d:%02d".format(hour, minute)
        }
    }
    val endTime = remember(key1 = endAngleAtan.value) {
        derivedStateOf {
            var angle = endAngleAtan.value + Math.toDegrees(PI / 2)
            if (angle < 0) angle += 360f
            if (angle >= 360f) angle -= 360f
            val hour = (angle / 15).toInt()
            val minute = ((angle % 15) / 15 * 60).toInt()
            "%02d:%02d".format(hour, minute)
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .onSizeChanged {
                centerX = it.width / 2f
                centerY = it.height / 2f

                val defaultStartTimeOffset = getOffsetByTime(centerX, centerY, startHour, startMinute)
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
            // 時計の針を描画
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
                    color = androidx.compose.ui.graphics.Color.Black,
                    start = androidx.compose.ui.geometry.Offset(
                        x = startX,
                        y = startY
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        x = endX,
                        y = endY
                    ),
                    cap = StrokeCap.Round,
                    strokeWidth = 3f
                )
            }
            // 分針を描画
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
                    color = androidx.compose.ui.graphics.Color.Gray,
                    start = androidx.compose.ui.geometry.Offset(
                        x = startX,
                        y = startY
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        x = endX,
                        y = endY
                    ),
                    alpha = 0.5f,
                    cap = StrokeCap.Round,
                    strokeWidth = 2f
                )
            }
        }

        val startAngle = startAngleAtan.value
        var sweepAngle = endAngleAtan.value - startAngleAtan.value
        if(endAngleAtan.value < startAngleAtan.value) sweepAngle += 360f
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

        // time を表示
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
 * 時間から座標を取得
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
