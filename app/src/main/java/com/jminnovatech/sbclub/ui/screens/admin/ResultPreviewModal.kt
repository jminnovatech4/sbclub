package com.jminnovatech.sbclub.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jminnovatech.sbclub.viewmodel.AdminVM
import com.jminnovatech.sbclub.utils.ApiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultPreviewModal(
    onClose: () -> Unit,
    isBetClosed: Boolean,
    betTime: String,
    resultTime: String
) {

    val vm: AdminVM = viewModel()
    val context = LocalContext.current
    var successMsg by remember { mutableStateOf<String?>(null) }
    var selectedNumber by remember { mutableStateOf<Int?>(null) }
    var showConfirm by remember { mutableStateOf(false) }

    // 🔥 AUTO LOAD
    LaunchedEffect(Unit) {
        vm.loadCurrentPreview(context)
    }

    ModalBottomSheet(onDismissRequest = { onClose() }) {

        when(val state = vm.previewState){

            is ApiState.Loading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }

            is ApiState.Success -> {

                val data = state.data
                val list = data.numbers.orEmpty()
// ✅ RESULT SYNC (backend theke)



                LaunchedEffect(data.round_id) {
                    selectedNumber = data.result_number
                }
                // 🔥 SORT PROFIT
                val sorted = list.sortedByDescending { it.profit }
                val top3 = sorted.take(3)

                Column(Modifier.padding(12.dp)) {

                    // 🎯 HEADER
                    Text(
                        text = "\uD83D\uDD04 Round ${data.round_id}  ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "⏱ $betTime | 🎯 $resultTime",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    // 💰 TOTAL + TOP
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {

                            Text(
                                "💰 Total Collection: ₹${data.total_collection}",
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text("🔥 Top Profit Numbers")

                            top3.forEach {
                                Text(
                                    "No ${it.number} → ₹${it.profit}",
                                    color = Color(0xFFFF6F00)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // 🔥 GRID VIEW
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxHeight()
                    ) {

                        items(sorted) { item ->

                            val isBest = item == sorted.first()

                            val bgColor = when {
                                isBest -> Color(0xFFFFF8E1) // 🔥 best
                                item.profit < 0 -> Color(0xFFFFEBEE) // loss
                                else -> Color(0xFFE8F5E9) // profit
                            }
                            val isSelected = selectedNumber == item.number
                            Card(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        Color(0xFFBBDEFB) // 🔥 selected highlight
                                    else bgColor
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {

                                Column(Modifier.padding(10.dp)) {

                                    Text(
                                        "No: ${item.number}",
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text("👥 Users: ${item.total_users}")
                                    Text("💰 Bet: ₹${item.total_bet}")
                                    Text("📤 Payout: ₹${item.payout}")

                                    Text(
                                        "📊 Profit: ₹${item.profit}",
                                        color = if(item.profit < 0) Color.Red else Color(0xFF2E7D32)
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Button(
                                        onClick = {
                                            if(isBetClosed){
                                                selectedNumber = item.number
                                                showConfirm = true
                                            }
                                        },
//                                        enabled = isBetClosed && data.normal_result_out != "yes" && data.total_collection > 0,
//                                        modifier = Modifier.fillMaxWidth()
                                        enabled = isBetClosed && data.normal_result_out != "yes",
                                        modifier = Modifier.fillMaxWidth()
                                        ) {
                                        Text(
                                            when {
                                                !isBetClosed -> "Locked"
                                                data.normal_result_out == "yes" -> "Result Done"
                                                else -> "Select"
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 🔥 CONFIRM DIALOG
                if(showConfirm && selectedNumber != null){

                    AlertDialog(
                        onDismissRequest = { showConfirm = false },
                        confirmButton = {
                            Button(onClick = {

                                vm.manualResult(
                                    context,
                                    data.round_id,
                                    selectedNumber.toString()
                                )

                                showConfirm = false

                                // 🔥 SUCCESS MESSAGE
                                successMsg = "Result $selectedNumber submitted successfully"

                            }) {
                                Text("CONFIRM")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showConfirm = false }) {
                                Text("Cancel")
                            }
                        },
                        title = { Text("Confirm Result") },
                        text = {
                            Text("Set result = $selectedNumber ?")
                        }
                    )
                }
            }

            is ApiState.Error -> {
                Text(
                    "Error: ${state.message}",
                    color = Color.Red
                )
            }

            else -> {
                Text("No Data")
            }
        }
    }
    if(successMsg != null){

        AlertDialog(
            onDismissRequest = { successMsg = null },

            confirmButton = {
                Button(onClick = {
                    successMsg = null
                    onClose() // 🔥 modal close
                }) {
                    Text("OK")
                }
            },

            title = { Text("Success ✅") },

            text = { Text(successMsg!!) }
        )
    }
}

fun formatTime(seconds: Long): String {
    return if (seconds >= 60) {
        val min = seconds / 60
        val sec = seconds % 60
        "${min}m ${sec}s"
    } else {
        "${seconds}s"
    }
}