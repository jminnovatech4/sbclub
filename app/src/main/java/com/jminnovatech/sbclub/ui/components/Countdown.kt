package com.jminnovatech.sbclub.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

@Composable
fun Countdown(seconds:Int) {

    var time by remember { mutableStateOf(seconds) }

    LaunchedEffect(Unit) {
        while(time > 0){
            delay(1000)
            time--
        }
    }

    Text("Time Left: $time")
}