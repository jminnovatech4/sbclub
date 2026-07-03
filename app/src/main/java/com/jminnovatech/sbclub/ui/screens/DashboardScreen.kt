package com.jminnovatech.sbclub.ui.screens.progame

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jminnovatech.sbclub.data.model.progame.Game
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.ProGameVM

@Composable
fun DashboardScreen(
    nav: NavController,
    context: Context,
    vm: ProGameVM = remember { ProGameVM() }
) {

    LaunchedEffect(Unit) {
        vm.loadGames(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            )
            .padding(16.dp)
    ) {

        Spacer(Modifier.height(20.dp))

        Text(
            "SB CLUB",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "Select Game",
            color = Color.LightGray
        )

        Spacer(Modifier.height(20.dp))

        when(val state = vm.gamesState){

            ApiState.Idle -> {}

            ApiState.Loading -> {

                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }

            }

            is ApiState.Error -> {

                Text(
                    state.message,
                    color = Color.Red
                )

            }

            is ApiState.Success -> {

                LazyColumn {

                    items(state.data){ game ->

                        GameCard(game){

                            nav.navigate("pro_game/${game.id}")

                        }

                        Spacer(Modifier.height(15.dp))

                    }

                }

            }

        }

    }

}
@Composable
fun DiceIcon(dots: Int) {

    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                Color.White,
                RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize().padding(5.dp)
        ) {

            val r = size.minDimension / 10f

            val left = Offset(size.width * .25f, size.height * .25f)
            val center = Offset(size.width * .5f, size.height * .5f)
            val right = Offset(size.width * .75f, size.height * .75f)

            val topRight = Offset(size.width * .75f, size.height * .25f)
            val bottomLeft = Offset(size.width * .25f, size.height * .75f)
            val topLeft = Offset(size.width * .25f, size.height * .25f)
            val bottomRight = Offset(size.width * .75f, size.height * .75f)
            val middleLeft = Offset(size.width * .25f, size.height * .5f)
            val middleRight = Offset(size.width * .75f, size.height * .5f)

            fun dot(p: Offset) {
                drawCircle(
                    color = Color(0xFF2563EB),
                    radius = r,
                    center = p
                )
            }

            when (dots) {

                1 -> {
                    dot(center)
                }

                2 -> {
                    dot(topRight)
                    dot(bottomLeft)
                }

                3 -> {
                    dot(topRight)
                    dot(center)
                    dot(bottomLeft)
                }

                5 -> {
                    dot(topLeft)
                    dot(topRight)
                    dot(center)
                    dot(bottomLeft)
                    dot(bottomRight)
                }
            }
        }
    }
}
@Composable
fun GameCard(

    game: Game,

    onClick:()->Unit

){
    val diceIcon = when (game.digit_length) {
        1 -> "\u2680"   // ⚀
        2 -> "\u2681"   // ⚁
        3 -> "\u2682"   // ⚂
        4 -> "\u2683"   // ⚃
        5 -> "\u2684"   // ⚄
        6 -> "\u2685"   // ⚅
        else -> "\u2680"
    }
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF2563EB)

        )

    ){

        Row(

            modifier = Modifier.fillMaxSize(),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.Center

        ){

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

//                Text(
//                    text = diceIcon,
//                    fontSize = 28.sp,
//                    color = Color.White
//                )
                DiceIcon(game.digit_length)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = game.game_name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(10.dp))

//            Text(
//
//                game.game_name,
//
//                color = Color.White,
//
//                fontSize = 20.sp,
//
//                fontWeight = FontWeight.Bold
//
//            )

        }

    }

}
