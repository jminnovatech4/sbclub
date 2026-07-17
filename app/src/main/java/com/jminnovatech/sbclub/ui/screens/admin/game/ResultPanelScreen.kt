package com.jminnovatech.sbclub.ui.screens.admin.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast

import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AdminVM

import com.jminnovatech.sbclub.data.model.admin.game.ResultPanelResponse

import com.jminnovatech.sbclub.ui.screens.admin.game.components.CurrentScheduleCard
import com.jminnovatech.sbclub.ui.screens.admin.game.components.HistoryCard

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

    when (panelState) {

        ApiState.Loading -> {

            Box(

                modifier = Modifier.fillMaxSize(),

                contentAlignment = Alignment.Center

            ) {

                CircularProgressIndicator()

            }

        }

        is ApiState.Error -> {

            Box(

                modifier = Modifier.fillMaxSize(),

                contentAlignment = Alignment.Center

            ) {

                Text(panelState.message)

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

@Composable
private fun ResultPanelBody(

    vm: AdminVM,

    data: ResultPanelResponse

) {

    val inputMap = remember {

        mutableStateMapOf<Int, String>()

    }

    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        if (data.current_schedule != null) {

            item {

                CurrentScheduleCard(

                    data = data,

                    vm = vm,

                    inputMap = inputMap

                )

            }

        }

        if (data.history.isNotEmpty()) {

            item {

                Text(

                    text = "History",

                    style = MaterialTheme.typography.titleLarge

                )

            }

            items(data.history) { item ->

                HistoryCard(item)

            }

        }

    }

}