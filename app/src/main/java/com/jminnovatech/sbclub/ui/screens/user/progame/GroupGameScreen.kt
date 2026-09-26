package com.jminnovatech.sbclub.ui.screens.user.progame

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.ProGameVM
import com.jminnovatech.sbclub.data.model.progame.Game

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupGameScreen(
    nav: NavController,
    context: Context,
    groupId: Int,
    vm: ProGameVM = remember { ProGameVM() }
) {

    // ============================================================
    // Load Group Games
    // ============================================================

    LaunchedEffect(groupId) {

        vm.loadGroupGames(
            context = context,
            groupId = groupId
        )
    }

    // ============================================================
    // UI
    // ============================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Select Game",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            nav.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }

    ) { paddingValues ->

        when (val state = vm.groupGamesState) {

            // ====================================================
            // IDLE
            // ====================================================

            ApiState.Idle -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFF8F7FF),
                                    Color(0xFFF1EEFF)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = Color(0xFF2563EB)
                    )
                }
            }

            // ====================================================
            // LOADING
            // ====================================================

            ApiState.Loading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFF8F7FF),
                                    Color(0xFFF1EEFF)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = Color(0xFF2563EB)
                    )
                }
            }

            // ====================================================
            // ERROR
            // ====================================================

            is ApiState.Error -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFF8F7FF),
                                    Color(0xFFF1EEFF)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = state.message,
                        color = Color.Red,
                        fontSize = 15.sp
                    )
                }
            }

            // ====================================================
            // SUCCESS
            // ====================================================

            is ApiState.Success -> {

                LazyColumn(

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFF8F7FF),
                                    Color(0xFFF0EDFF),
                                    Color(0xFFF8F7FF)
                                )
                            )
                        )
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 18.dp,
                            bottom = 24.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)

                ) {

                    items(
                        items = state.data,
                        key = { game -> game.id }
                    ) { game ->

                        PremiumGameCard(

                            game = game,

                            onClick = {

                                // ==========================================
                                // EXISTING NAVIGATION - UNCHANGED
                                // ==========================================

                                nav.navigate(
                                    "pro_game/$groupId/${game.id}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


// ============================================================================
// PREMIUM GAME CARD
// ============================================================================

@Composable
private fun PremiumGameCard(
    game: Game,
    onClick: () -> Unit
) {

    val gameName = game.game_name

    // ------------------------------------------------------------
    // Color according to game
    // ------------------------------------------------------------

    val gradient = when {

        gameName.equals("Single", ignoreCase = true) -> {
            listOf(
                Color(0xFF0EA5E9),
                Color(0xFF2563EB),
                Color(0xFF4338CA)
            )
        }

        gameName.equals("Jodi", ignoreCase = true) -> {
            listOf(
                Color(0xFFD946EF),
                Color(0xFF9333EA),
                Color(0xFFEC4899)
            )
        }

        gameName.equals("Patti", ignoreCase = true) -> {
            listOf(
                Color(0xFFFACC15),
                Color(0xFFF97316),
                Color(0xFFFFA000)
            )
        }

        gameName.equals("CP", ignoreCase = true) -> {
            listOf(
                Color(0xFF14B8A6),
                Color(0xFF0F9F9A),
                Color(0xFF10B981)
            )
        }

        else -> {
            listOf(
                Color(0xFF2563EB),
                Color(0xFF4F46E5)
            )
        }
    }

    val subtitle = when {

        gameName.equals("Single", ignoreCase = true) ->
            "Play Single Digit"

        gameName.equals("Jodi", ignoreCase = true) ->
            "Play Jodi Numbers"

        gameName.equals("Patti", ignoreCase = true) ->
            "Play Patti Numbers"

        gameName.equals("CP", ignoreCase = true) ->
            "Play CP Numbers"

        else ->
            "Play ${gameName}"
    }

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(
                RoundedCornerShape(24.dp)
            )
            .background(
                Brush.linearGradient(gradient)
            )
            .clickable {
                onClick()
            }

    ) {

        // ========================================================
        // Decorative curved background
        // ========================================================

        Box(
            modifier = Modifier
                .size(190.dp)
                .offset(
                    x = 220.dp,
                    y = (-45).dp
                )
                .background(
                    Color.White.copy(alpha = 0.08f),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(
                    x = (-45).dp,
                    y = 65.dp
                )
                .background(
                    Color.White.copy(alpha = 0.08f),
                    CircleShape
                )
        )

        Row(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 16.dp
                ),

            verticalAlignment = Alignment.CenterVertically

        ) {

            // ====================================================
            // Dice
            // ====================================================

            DiceTile(
                dots = game.digit_length,
                dotColor = gradient.last()
            )

            Spacer(
                modifier = Modifier.width(18.dp)
            )

            // ====================================================
            // Game Name
            // ====================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = gameName,
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = subtitle,
                    color = Color.White.copy(
                        alpha = 0.82f
                    ),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // ====================================================
            // Arrow
            // ====================================================

            Box(

                modifier = Modifier
                    .size(52.dp)
                    .background(
                        Color.White.copy(alpha = 0.22f),
                        CircleShape
                    ),

                contentAlignment = Alignment.Center

            ) {

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Open",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}


// ============================================================================
// DICE TILE
// ============================================================================

@Composable
private fun DiceTile(
    dots: Int,
    dotColor: Color
) {

    Box(

        modifier = Modifier
            .size(62.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp)
            )
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(Color.White),

        contentAlignment = Alignment.Center

    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {

            val r = size.minDimension / 9f

            val topLeft =
                Offset(
                    size.width * 0.25f,
                    size.height * 0.25f
                )

            val topRight =
                Offset(
                    size.width * 0.75f,
                    size.height * 0.25f
                )

            val center =
                Offset(
                    size.width * 0.50f,
                    size.height * 0.50f
                )

            val bottomLeft =
                Offset(
                    size.width * 0.25f,
                    size.height * 0.75f
                )

            val bottomRight =
                Offset(
                    size.width * 0.75f,
                    size.height * 0.75f
                )

            val middleLeft =
                Offset(
                    size.width * 0.25f,
                    size.height * 0.50f
                )

            val middleRight =
                Offset(
                    size.width * 0.75f,
                    size.height * 0.50f
                )

            fun dot(position: Offset) {

                drawCircle(
                    color = dotColor,
                    radius = r,
                    center = position
                )
            }

            when (dots) {

                // ====================================================
                // SINGLE
                // ====================================================

                1 -> {

                    dot(center)
                }

                // ====================================================
                // JODI
                // ====================================================

                2 -> {

                    dot(topRight)
                    dot(bottomLeft)
                }

                // ====================================================
                // PATTI
                // ====================================================

                3 -> {

                    dot(topRight)
                    dot(center)
                    dot(bottomLeft)
                }

                // ====================================================
                // CP
                // ====================================================

                4 -> {

                    dot(topLeft)
                    dot(topRight)
                    dot(bottomLeft)
                    dot(bottomRight)
                }

                // ====================================================
                // 5
                // ====================================================

                5 -> {

                    dot(topLeft)
                    dot(topRight)
                    dot(center)
                    dot(bottomLeft)
                    dot(bottomRight)
                }

                // ====================================================
                // 6
                // ====================================================

                6 -> {

                    dot(topLeft)
                    dot(middleLeft)
                    dot(bottomLeft)

                    dot(topRight)
                    dot(middleRight)
                    dot(bottomRight)
                }

                else -> {

                    dot(center)
                }
            }
        }
    }
}