package com.jminnovatech.sbclub.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import com.google.accompanist.swiperefresh.*
import com.jminnovatech.sbclub.viewmodel.WalletVM
import com.jminnovatech.sbclub.utils.ApiState
import java.time.*
import java.time.format.DateTimeFormatter

fun formatDate(dateStr: String): String {
    return try {

        val instant = Instant.parse(dateStr)
        val zoned = instant.atZone(ZoneId.systemDefault())

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

        zoned.format(formatter)

    } catch (e: Exception) {
        dateStr
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawScreen(vm: WalletVM) {


    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current

    var amount by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    val options = listOf("upi","gpay","phonepe")
    var type by remember { mutableStateOf("phonepe") }

    var acc by remember { mutableStateOf("") }
    var holder_name by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }

    val profileState = vm.profileState
    val withdrawState = vm.withdrawState

    var errorMsg by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var balance by remember { mutableStateOf("0") }

    val MIN_WITHDRAW = 500
    val MAX_WITHDRAW = 50000
    val isRefreshing = withdrawState is ApiState.Loading
// 🔥 Load profile
    LaunchedEffect(Unit) {
        vm.loadProfile()
    }

// 🔥 Auto fill
    LaunchedEffect(profileState) {
        if (profileState is ApiState.Success) {
            val data = profileState.data
            balance = data.wallet ?: "0"

            data.payment?.let {
                type = it.payment_type ?: "upi"
                acc = it.account_number ?: ""
                holder_name = it.holder_name ?: ""
                ifsc = it.ifsc_code ?: ""
            }
        }
    }

// 🔥 Success handling
    LaunchedEffect(withdrawState) {
        if (withdrawState is ApiState.Success) {
            showSheet = false
            showSuccess = true
            vm.loadProfile()
            vm.loadHistory()

            delay(1500)
            showSuccess = false
            amount = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Withdraw", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->

        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF030C52), Color(0xFF39793D))
                    )
                )
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    vm.loadProfile()
                    vm.loadHistory()
                }
            ) {
            Column(Modifier.padding(16.dp)) {

                Text("💸 Withdraw",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(10.dp))

                Text("Balance: ₹$balance",
                    color = Color(0xFFFFEB3B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)

                Spacer(Modifier.height(20.dp))

                // 🔥 Amount input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(2.dp) // border effect
                ) {

                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() }) amount = it
                        },
                        placeholder = {
                            Text("Enter Amount", color = Color.Gray)
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color(0xFF6366F1)
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {

                        val amt = amount.toIntOrNull() ?: 0

                        when {
                            amt <= 0 -> {
                                errorMsg = "Enter valid amount"
                                showErrorDialog = true
                            }

                            amt < MIN_WITHDRAW -> {
                                errorMsg = "Minimum withdraw ₹$MIN_WITHDRAW"
                                showErrorDialog = true
                            }

                            amt > MAX_WITHDRAW -> {
                                errorMsg = "Maximum withdraw ₹$MAX_WITHDRAW"
                                showErrorDialog = true
                            }

                            amt > balance.toDouble().toInt() -> {
                                errorMsg = "Insufficient balance"
                                showErrorDialog = true
                            }

                            else -> {
                                showSheet = true // ✅ correct flow
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Withdraw")
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        vm.loadHistory()
                        showHistory = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📜 History")
                }
            }
}
            // 🔥 Loader
            if (withdrawState is ApiState.Loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

// 🔥 ERROR DIALOG
    if (showErrorDialog) {
        Dialog(onDismissRequest = { showErrorDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(
                    Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠️", fontSize = 40.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(errorMsg, color = Color.Black)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            }
        }
    }

// 🔥 PAYMENT MODAL
    if (showSheet) {

        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            containerColor = Color.White
        ) {

            Column(Modifier.padding(16.dp)) {

                Text("Confirm Withdrawal ₹$amount",
                    fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(12.dp))

                // dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Type") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    type = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                if (type == "bank") {
                    LightTextField(acc, { acc = it }, "Account Number")
                    LightTextField(ifsc, { ifsc = it }, "IFSC")
                } else {
                    LightTextField(acc, { acc = it }, "UPI ID")
                }

                LightTextField(holder_name, { holder_name = it }, "Holder Name")

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {

                        if (acc.isEmpty()) {
                            errorMsg = "Enter payment details"
                            showErrorDialog = true
                            return@Button
                        }

                        keyboard?.hide()
                        focus.clearFocus()

                        vm.withdraw(
                            amount.toDoubleOrNull() ?: 0.0,
                            type,
                            acc,
                            ifsc,
                            holder_name   // ✅ MUST PASS
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Withdraw")
                }
            }
        }
    }

// 🔥 HISTORY
    if (showHistory) {
        WithdrawHistoryModal(vm) { showHistory = false }
    }

// 🔥 SUCCESS
    AnimatedVisibility(
        visible = showSuccess,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Dialog(onDismissRequest = {}) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF065F46)
            ) {
                Column(
                    Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅", fontSize = 40.sp)
                    Text("Success", color = Color.White)
                }
            }
        }
    }


}

@Composable
fun WithdrawHistoryModal(vm: WalletVM, onClose: () -> Unit) {


    val state = vm.historyState

    Dialog(onDismissRequest = onClose) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {

            Column(Modifier.padding(12.dp)) {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("History", color = Color.White)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, "", tint = Color.White)
                    }
                }

                when (state) {
                    is ApiState.Success -> {
                        LazyColumn {
                            items(state.data) { item ->
                                Card(
                                    Modifier.padding(6.dp),
                                    colors = CardDefaults.cardColors(Color(0xFF1E293B))
                                ) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("₹${item.amount}", color = Color.White)
                                            Text(
                                                formatDate(item.created_at),
                                                color = Color.Gray
                                            )
                                        }
                                        Text(
                                            item.status,
                                            color = when (item.status) {
                                                "pending" -> Color.Yellow
                                                "approved" -> Color.Green
                                                "rejected" -> Color.Red
                                                else -> Color.Gray
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    else -> CircularProgressIndicator()
                }
            }
        }
    }


}

@Composable
fun LightTextField(value: String, onChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}
