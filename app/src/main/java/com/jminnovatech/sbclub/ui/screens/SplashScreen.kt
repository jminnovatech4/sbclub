package com.jminnovatech.sbclub.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.jminnovatech.sbclub.utils.SessionManager
import com.jminnovatech.sbclub.R
@Composable
fun SplashScreen(nav: NavController, context: Context) {

    val session = SessionManager(context)

    var startAnim by remember { mutableStateOf(false) }

    // 🔥 Smooth scale animation
    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.6f,
        animationSpec = tween(700, easing = FastOutSlowInEasing)
    )

    // 🔥 Fade in
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(700)
    )

    // 🔥 Floating effect (up-down)
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val translateY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    // 🔥 Pulse effect (slight zoom in-out)
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    LaunchedEffect(Unit) {

        startAnim = true

        // 🔥 SHORT DELAY (fast splash)
        delay(1200)

        when (session.getRole()) {
            "admin" -> nav.navigate("admin") {
                popUpTo("splash") { inclusive = true }
            }
            "master" -> nav.navigate("master") {
                popUpTo("splash") { inclusive = true }
            }
            "user" -> nav.navigate("dashboard") {
                popUpTo("splash") { inclusive = true }
            }
            else -> nav.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020617), Color(0xFF1E293B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // 🔥 LOGO (NO CROP NEEDED)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(260.dp)
                    .graphicsLayer {
                        scaleX = scale * pulse
                        scaleY = scale * pulse
                        translationY = translateY
                        alpha = alphaAnim
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "SBCLUB",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator(color = Color.Green)
        }
    }
}