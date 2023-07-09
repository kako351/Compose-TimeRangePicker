package com.kako351.timerangepicker.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.kako351.timerangepicker.TimeRangePicker
import com.kako351.timerangepicker.sample.ui.theme.TimeRangePickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeRangePickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    TimeRangePicker { startHour, startMinutes, endHour, endMinutes ->
                        Log.i("MainActivity", "startHour: $startHour, startMinutes: $startMinutes, endHour: $endHour, endMinutes: $endMinutes")
                    }
                }
            }
        }
    }
}
