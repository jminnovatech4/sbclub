package com.jminnovatech.sbclub.ui.screens.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jminnovatech.sbclub.ui.screens.formatServerTime
import com.jminnovatech.sbclub.viewmodel.AdminVM
import com.jminnovatech.sbclub.utils.ApiState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionModal(onClose:()->Unit) {

    val vm: AdminVM = viewModel()
    val context = LocalContext.current

    // 🔥 LOAD DATA
    LaunchedEffect(Unit){
        if(vm.ledgerState !is ApiState.Success){
            vm.loadLedger(context, true)
        }
    }

    ModalBottomSheet(onDismissRequest = onClose) {

        Column(Modifier.padding(12.dp)) {

            Text(
                "📜 Transaction History",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(10.dp))

            when(val state = vm.ledgerState){

                is ApiState.Loading -> {

                    // 🔥 FULL SCREEN LOADER
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ApiState.Success -> {

                    LazyColumn(
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(state.data){ item ->

                            val isCredit = item.credit > 0

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1E293B)
                                )
                            ) {

                                Column(Modifier.padding(12.dp)) {

                                    if(item.user_name != null){
                                        Text(
                                            item.user_name,
                                            color = Color(0xFFFFD54F),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        item.remark,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Text(
                                            if (isCredit)
                                                "+₹${item.credit}"
                                            else "-₹${item.debit}",
                                            color = if (isCredit) Color(0xFF4CAF50) else Color.Red,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    if(item.balance != null){
                                        Text(
                                            "Bal: ₹${item.balance}",
                                            color = Color(0xFF00E5FF),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        formatServerTime(item.date),
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // 🔥 LOADING FOOTER (future refresh support)
                        item {
                            if(vm.ledgerState is ApiState.Loading){
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ){
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }

                is ApiState.Error -> {

                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error loading history", color = Color.Red)

                        Spacer(Modifier.height(10.dp))

                        Button(onClick = {
                            vm.loadLedger(context, true)
                        }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {}
            }
        }
    }
}