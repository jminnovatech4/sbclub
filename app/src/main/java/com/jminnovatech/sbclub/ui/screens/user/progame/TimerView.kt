package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
@Composable
fun TimerView(

    endTime: String

) {

    val formatter = remember {

        SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)

    }

    var remain by remember {

        mutableLongStateOf(0)

    }

    LaunchedEffect(endTime) {

        while (true) {

            try {

                val now = Calendar.getInstance()

                val end = Calendar.getInstance()

                val d = formatter.parse(endTime)

                if (d != null) {

                    end.set(

                        Calendar.HOUR_OF_DAY,

                        d.hours

                    )

                    end.set(

                        Calendar.MINUTE,

                        d.minutes

                    )

                    end.set(

                        Calendar.SECOND,

                        d.seconds

                    )

                }

                remain =

                    (end.timeInMillis - now.timeInMillis)
                        .coerceAtLeast(0)

            } catch (_: Exception) {
            }

            delay(1000)

        }

    }

    val total = 60 * 60 * 1000f

    val progress by animateFloatAsState(

        targetValue = remain / total,

        animationSpec = tween(

            500,

            easing = LinearEasing

        ),

        label = ""

    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF111827),
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(3.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "ENDS IN",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Text(
                        text = millisToTime(remain),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            ) {

                drawRoundRect(
                    color = Color.DarkGray,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f,20f)
                )

                drawRoundRect(
                    color = Color(0xFF16A34A),
                    size = Size(
                        width = size.width * progress,
                        height = size.height
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f,20f)
                )
            }
        }
    }

}

private fun millisToTime(

    ms: Long

): String {

    val sec = ms / 1000

    val h = sec / 3600

    val m = (sec % 3600) / 60

    val s = sec % 60

    return "%02d:%02d:%02d".format(

        h,

        m,

        s

    )

}