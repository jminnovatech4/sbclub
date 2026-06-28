package com.jminnovatech.sbclub.ui.components

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale

@Composable
fun LoaderPro(){

    val infinite = rememberInfiniteTransition()

    val scale by infinite.animateFloat(
        0.8f, 1.2f,
        infiniteRepeatable(tween(800), RepeatMode.Reverse)
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator(
            modifier = Modifier.scale(scale)
        )
    }
}