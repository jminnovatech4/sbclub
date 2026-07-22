package com.jminnovatech.sbclub.ui.screens.admin.game

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.admin.game.ResultPanelResponse
import com.jminnovatech.sbclub.ui.screens.admin.game.components.CurrentScheduleCard
import com.jminnovatech.sbclub.ui.screens.admin.game.components.HistoryCard
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AdminVM

@Composable
fun ResultPanelScreen(
    vm: AdminVM
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {

        vm.loadResultPanel(context)

    }

    val panelState = vm.resultPanelState
    val publishState = vm.publishAllState

    LaunchedEffect(publishState) {

        when (publishState) {

            is ApiState.Success -> {

                Toast.makeText(
                    context,
                    publishState.data,
                    Toast.LENGTH_SHORT
                ).show()

                vm.resetPublishState()

            }

            is ApiState.Error -> {

                Toast.makeText(
                    context,
                    publishState.message,
                    Toast.LENGTH_SHORT
                ).show()

                vm.resetPublishState()

            }

            else -> {}

        }

    }

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF8FAFC),
                        Color.White
                    )
                )
            )

    ) {

        when (panelState) {

            ApiState.Loading -> {

                Column(

                    modifier = Modifier.fillMaxSize(),

                    horizontalAlignment = Alignment.CenterHorizontally,

                    verticalArrangement = Arrangement.Center

                ) {

                    CircularProgressIndicator()

                    Spacer(Modifier.height(15.dp))

                    Text("Loading Result Panel...")

                }

            }

            is ApiState.Error -> {

                Column(

                    modifier = Modifier.fillMaxSize(),

                    horizontalAlignment = Alignment.CenterHorizontally,

                    verticalArrangement = Arrangement.Center

                ) {

                    Text(
                        "⚠",
                        style = MaterialTheme.typography.displayLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(panelState.message)

                    Spacer(Modifier.height(20.dp))

                    Button(

                        onClick = {

                            vm.loadResultPanel(context)

                        }

                    ) {

                        Icon(
                            Icons.Default.Refresh,
                            null
                        )

                        Spacer(Modifier.width(8.dp))

                        Text("Retry")

                    }

                }

            }

            is ApiState.Success -> {

                ResultPanelBody(

                    vm = vm,

                    data = panelState.data

                )

            }

            else -> {}

        }

    }

}

@Composable
private fun ResultPanelBody(

    vm: AdminVM,

    data: ResultPanelResponse

) {

    val context = LocalContext.current

    val inputMap = remember {

        mutableStateMapOf<Int, String>()

    }

    val noCurrent =
        data.current_schedule == null

    val noHistory =
        data.history.isEmpty()

    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(16.dp),

        verticalArrangement = Arrangement.spacedBy(18.dp)

    ) {

        // ===============================
        // REFRESH BUTTON
        // ===============================

        item {

            FilledTonalButton(

                modifier = Modifier.fillMaxWidth(),

                onClick = {

                    vm.loadResultPanel(context)

                }

            ) {

                Icon(
                    Icons.Default.Refresh,
                    null
                )

                Spacer(Modifier.width(10.dp))

                Text("Refresh Panel")

            }

        }

        // ===============================
        // CURRENT PUBLISH PANEL
        // ===============================

        if (!noCurrent) {

            item {

                Text(

                    text = "Current Publish",

                    style = MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.Bold

                )

            }

            item {

                CurrentScheduleCard(

                    data = data,

                    vm = vm,

                    inputMap = inputMap

                )

            }

        }

        // ===============================
        // EMPTY STATE
        // ===============================

        if (noCurrent && noHistory) {

            item {

                Card(

                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(

                        containerColor = Color(0xFFF8FAFC)

                    )

                ) {

                    Column(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),

                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {

                        Text(

                            "🎯",

                            style = MaterialTheme.typography.displayLarge

                        )

                        Spacer(Modifier.height(12.dp))

                        Text(

                            "No Result Available",

                            style = MaterialTheme.typography.headlineSmall,

                            fontWeight = FontWeight.Bold

                        )

                        Spacer(Modifier.height(8.dp))

                        Text(

                            "No game is ready for publishing yet.\nPlease wait until the first Baji result time.",

                            color = Color.Gray

                        )

                        Spacer(Modifier.height(22.dp))

                        Button(

                            onClick = {

                                vm.loadResultPanel(context)

                            }

                        ) {

                            Icon(
                                Icons.Default.Refresh,
                                null
                            )

                            Spacer(Modifier.width(8.dp))

                            Text("Refresh")

                        }

                    }

                }

            }

        }

        // ===============================
        // HISTORY
        // ===============================

        if (!noHistory) {

            item {

                Row(

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Icon(

                        Icons.Default.History,

                        null,

                        tint = Color(0xFF2563EB)

                    )

                    Spacer(Modifier.width(8.dp))

                    Text(

                        text = "Published History",

                        style = MaterialTheme.typography.titleLarge,

                        fontWeight = FontWeight.Bold

                    )

                }

            }

            items(data.history) { item ->

                HistoryCard(item)

            }

        }

        item {

            Spacer(
                Modifier.height(30.dp)
            )

        }

    }

}