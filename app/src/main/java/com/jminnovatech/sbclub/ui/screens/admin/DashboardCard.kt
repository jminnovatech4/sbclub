package com.jminnovatech.sbclub.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardCard(title:String, onClick:()->Unit){

    val scale = remember { Animatable(1f) }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth() // ✅ replace weight
            .scale(scale.value)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                    )
                )
                .height(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, color = Color.White, fontSize = 16.sp)
        }
    }
}