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

        shape = CircleShape,

        color = Color(0xFF111827),

        shadowElevation = 6.dp

    ) {

        Box(

            modifier = Modifier.size(120.dp),

            contentAlignment = Alignment.Center

        ) {

            Canvas(

                modifier = Modifier.fillMaxSize()

            ) {

                drawArc(

                    color = Color.DarkGray,

                    startAngle = -90f,

                    sweepAngle = 360f,

                    useCenter = false,

                    style = Stroke(

                        10f,

                        cap = StrokeCap.Round

                    )

                )

                drawArc(

                    color = Color(0xFF16A34A),

                    startAngle = -90f,

                    sweepAngle = 360 * progress,

                    useCenter = false,

                    style = Stroke(

                        10f,

                        cap = StrokeCap.Round

                    )

                )

            }

            Column(

                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Icon(

                    Icons.Default.Timer,

                    null,

                    tint = Color.White

                )

                Spacer(

                    Modifier.height(5.dp)

                )

                Text(

                    millisToTime(remain),

                    color = Color.White,

                    fontWeight = FontWeight.Bold,

                    fontSize = 18.sp

                )

                Text(

                    "Ends In",

                    color = Color.Gray,

                    fontSize = 11.sp

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