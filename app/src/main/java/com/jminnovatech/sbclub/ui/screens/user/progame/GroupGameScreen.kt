package com.jminnovatech.sbclub.ui.screens.user.progame

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jminnovatech.sbclub.ui.screens.progame.GameCard
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.ProGameVM

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
                        fontSize = 20.sp
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
                        .padding(paddingValues),

                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            // ====================================================
            // LOADING
            // ====================================================

            ApiState.Loading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),

                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            // ====================================================
            // ERROR
            // ====================================================

            is ApiState.Error -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = state.message,
                        color = Color.Red
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
                        .padding(16.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)

                ) {

                    items(
                        items = state.data,
                        key = { game -> game.id }
                    ) { game ->

                        GameCard(

                            game = game,

                            onClick = {

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