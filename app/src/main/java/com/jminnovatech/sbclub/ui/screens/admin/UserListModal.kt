package com.jminnovatech.sbclub.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jminnovatech.sbclub.viewmodel.AdminVM
import com.jminnovatech.sbclub.utils.ApiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListModal(onClose: () -> Unit) {
    var showSuccessDialog by remember { mutableStateOf(false) }
    val vm: AdminVM = viewModel()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<com.jminnovatech.sbclub.model.UserNew?>(null) }
    var amount by remember { mutableStateOf("") }
    LaunchedEffect(vm.walletAddState) {

        when(val res = vm.walletAddState){

            is ApiState.Success -> {

                showDialog = false
                showSuccessDialog = true   // 🔥 SHOW SUCCESS DIALOG
                vm.loadUsers(context)

                // 🔥 AUTO CLOSE AFTER 1.5s
                kotlinx.coroutines.delay(1500)
                showSuccessDialog = false
                vm.resetWalletState()
            }

            is ApiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar("❌ ${res.message}")
                }
            }

            else -> {}
        }
    }
    LaunchedEffect(Unit) {
        vm.loadUsers(context)
        vm.resetWalletState()
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = Color(0xFF0F172A)
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            Text("👥 Users", color = Color.White)

            Spacer(Modifier.height(10.dp))

            // 🔍 SEARCH BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(2.dp)
            ) {

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },

                    placeholder = {
                        Text("🔍 Search user...", color = Color.Gray)
                    },

                    singleLine = true,

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(14.dp)),

                    shape = RoundedCornerShape(14.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
            var selectedFilter by remember { mutableStateOf("ALL") }
            var isFilterLoading by remember { mutableStateOf(false) }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                listOf("ALL", "MASTER", "USER").forEach { type ->

                    val isSelected = selectedFilter == type

                     Button(
                         onClick = {

                             if (selectedFilter != type) {

                                 isFilterLoading = true

                                 scope.launch {
                                     kotlinx.coroutines.delay(400) // 🔥 smooth loader feel
                                     selectedFilter = type
                                     isFilterLoading = false
                                 }
                             }
                         },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                Color(0xFF6366F1)
                            else
                                Color(0xFF1E293B)
                        ),

                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            type,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            when (val state = vm.userListState) {

                is ApiState.Loading -> {
                    CircularProgressIndicator()
                }

                is ApiState.Success -> {

                    val filtered = state.data.filter { user ->

                        val matchesSearch =
                            user.name.contains(searchText, true) ||
                                    user.phone.contains(searchText)

                        val matchesFilter = when (selectedFilter) {
                            "MASTER" -> user.role.equals("master", true)
                            "USER" -> user.role.equals("user", true)
                            else -> true
                        }

                        matchesSearch && matchesFilter
                    }
                    if (isFilterLoading) {

                        Box(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF6366F1))
                        }

                    } else {
                    LazyColumn {

                        items(filtered) { user ->

                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1E293B)
                                ),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {

                                Row(
                                    Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Column {

                                        Text(user.name, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text("📱 ${user.phone}", color = Color.White)
                                        Text("🎯 ${user.role}", color = Color.Cyan)
                                    }

                                    Spacer(Modifier.weight(1f))

                                    Button(
                                        onClick = {
                                            selectedUser = user
                                            amount = ""
                                            showDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF22C55E)
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Add ₹")
                                    }
                                }
                            }
                        }
                    }}
                }

                is ApiState.Error -> {
                    Text("Error loading users", color = Color.Red)
                }

                else -> {}
            }

            SnackbarHost(hostState = snackbarHostState)
        }
    }

    // 🔥 AMOUNT DIALOG
    if (showDialog) {

        AlertDialog(
            onDismissRequest = { showDialog = false },

            title = { Text("💰 Add Balance") },

            text = {

                Column {

                    Text("User: ${selectedUser?.name}")

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = amount,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() }) {
                                amount = it
                            }
                        },
                        placeholder = { Text("Enter amount") }
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(100, 500, 1000).forEach {
                            OutlinedButton(onClick = {
                                amount = it.toString()
                            }) {
                                Text("₹$it")
                            }
                        }
                    }
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        val amt = amount.toIntOrNull() ?: 0

                        if (amt > 0 && selectedUser != null) {

                            vm.walletAdd(
                                context,
                                selectedUser!!.id,
                                amt
                            )

                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("⚠️ Enter valid amount")
                            }
                        }
                    },

                    enabled = vm.walletAddState !is ApiState.Loading,   // 🔥 prevent multiple click

                    modifier = Modifier.fillMaxWidth(),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22C55E)
                    )
                ) {

                    // 🔥 LOADING UI
                    if (vm.walletAddState is ApiState.Loading) {

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )

                            Spacer(Modifier.width(8.dp))

                            Text("Processing...", color = Color.White)
                        }

                    } else {
                        Text("Submit", color = Color.White)
                    }
                }
            },

            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    if (showSuccessDialog) {

        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },

            title = {
                Text("✅ Success")
            },

            text = {
                Text("Amount added successfully 💰")
            },

            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}