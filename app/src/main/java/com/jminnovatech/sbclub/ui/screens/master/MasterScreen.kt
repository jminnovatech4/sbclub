package com.jminnovatech.sbclub.ui.screens.master

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import com.jminnovatech.sbclub.viewmodel.MasterVM
import com.jminnovatech.sbclub.utils.ApiState
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.*

import com.jminnovatech.sbclub.data.model.master.MasterUser
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.ui.screens.WithdrawScreen
import com.jminnovatech.sbclub.utils.SessionManager
import com.jminnovatech.sbclub.viewmodel.AuthVM
import com.jminnovatech.sbclub.viewmodel.WalletVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterScreen(vm: MasterVM, nav: NavHostController) {

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val context = LocalContext.current

    var showCreate by remember { mutableStateOf(false) }
    var showUsers by remember { mutableStateOf(false) }
    var showTransaction by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val isRefreshing = vm.userState is ApiState.Loading
    var showWithdraw by remember { mutableStateOf(false) }
    val walletVM = remember { WalletVM(AppRepository(context)) }
    val walletBalance = when (val state = walletVM.profileState) {
        is ApiState.Success -> state.data.wallet
        else -> "0"
    }
    var showChangePass by remember { mutableStateOf(false) }
    var showWithdrawScreen by remember { mutableStateOf(false) }
    val pendingCount = when(val s = walletVM.pendingState){
        is ApiState.Success -> s.data.size
        else -> 0
    }
    LaunchedEffect(Unit) {
        vm.loadUsers()
        walletVM.loadProfile()
        walletVM.loadPending()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {

                Text(
                    "👑 Master Panel",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )

                Divider()

                NavigationDrawerItem(
                    label = { Text("➕ Create User") },
                    selected = false,
                    onClick = {
                        showCreate = true
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.PersonAdd, null) }
                )

                NavigationDrawerItem(
                    label = { Text("👥 Users") },
                    selected = false,
                    onClick = {
                        showUsers = true
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Group, null) }
                )

                NavigationDrawerItem(
                    label = { Text("📊 Transactions") },
                    selected = false,
                    onClick = {
                        vm.loadLedger()
                        showTransaction = true
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.List, null) }
                )
                NavigationDrawerItem(
                    label = { Text("💸 Payment Withdraw") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        nav.navigate("withdraw")
                    },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, null) }
                )

                NavigationDrawerItem(
                    label = { Text("🔐 Change Password") },
                    selected = false,
                    onClick = {
                        showChangePass = true
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Lock, null) }
                )
                NavigationDrawerItem(
                    label = { Text("🚪 Logout") },
                    selected = false,
                    onClick = {
                        showLogoutDialog = true
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.ExitToApp, null) }
                )
            }
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Master Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, null)
                        }

                    },actions = {
                        var rotating by remember { mutableStateOf(false) }

                        IconButton(onClick = {
                            rotating = true
                            vm.loadUsers()
                            walletVM.loadProfile()
                            walletVM.loadPending()
                            vm.loadLedger()
                        }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.rotate(if (rotating) 360f else 0f)
                            )
                        }
                    }

                )
            }
        ) { padding ->

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { vm.loadUsers() }
            ) {

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    val pendingCount = when(val s = walletVM.pendingState){
                        is ApiState.Success -> s.data.size
                        else -> 0
                    }
                    // 🔥 DASHBOARD CARD
                    Card(
                        colors = CardDefaults.cardColors(Color(0xFF1E3A8A)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {

                            Text(" Balance", color = Color.White)

                            Text(
                                walletBalance,
                                color = Color.Yellow,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text("👥 Users: ${vm.users.size}", color = Color.White)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // 🔥 BUTTONS
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {

                        ActionCard("Create User", Icons.Default.PersonAdd, Color(0xFF400749), Modifier.weight(1f)) {
                            showCreate = true
                        }

                        ActionCard("Payment Transfer", Icons.Default.Group, Color.Blue, Modifier.weight(1f)) {
                            showUsers = true
                        }


                    }
                    Spacer(Modifier.height(6.dp))

//                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
//
//                        Box(modifier = Modifier.weight(1f)) {
//
//                            ActionCard(
//                                "Pending User Request",
//                                Icons.Default.AccountBalanceWallet,
//                                Color(0xFF2A046B),
//                                Modifier.fillMaxWidth()
//                            ) {
//                                showWithdraw = true
//                            }
//
//                            // 🔥 BADGE
//                            if(pendingCount > 0){
//                                Box(
//                                    modifier = Modifier
//                                        .align(Alignment.TopEnd)
//                                        .padding(4.dp)
//                                        .background(Color.Red, shape = RoundedCornerShape(50))
//                                        .padding(horizontal = 6.dp, vertical = 2.dp)
//                                ){
//                                    Text(
//                                        pendingCount.toString(),
//                                        color = Color.White,
//                                        fontSize = 10.sp
//                                    )
//                                }
//                            }
//                        }
//
//
//                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActionCard("Withdrawal Request", Icons.Default.ReceiptLong,Color(0xFF033B05), Modifier.weight(1f)) {
                            vm.loadLedger()
                            showWithdrawScreen = true
                        }
                        ActionCard(
                            "Transactions",
                            Icons.Default.ReceiptLong,
                            Color(0xFF2E7D32),
                            Modifier.weight(1f)
                        ) {
                            vm.loadLedger()
                            showTransaction = true
                        }

                        Box(modifier = Modifier.weight(1f)) {

                            ActionCard(
                                "Pending Request",
                                Icons.Default.AccountBalanceWallet,
                                Color(0xFF4002AD),
                                Modifier.fillMaxWidth()
                            ) {
                                showWithdraw = true
                            }

                            // 🔥 BADGE
                            if (pendingCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .background(Color.Red, RoundedCornerShape(50))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        pendingCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                    if (vm.userState is ApiState.Loading) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "💸 Recent User Payment Requests",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    when(val state = walletVM.pendingState){

                        is ApiState.Loading -> {
                            CircularProgressIndicator()
                        }

                        is ApiState.Success -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)   // 🔥 FIXED
                            ){

                                items(state.data){ item ->

                                    Card(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(Color(0xFF1E293B))
                                    ){
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ){

                                            Column {
                                                Text(
                                                    item.user_name ?: "User ${item.user_id}",
                                                    color = Color.White
                                                )
                                                Text(
                                                    "${item.amount}",
                                                    color = Color.Yellow
                                                )
                                            }

                                            Text(
                                                item.status,
                                                color = when(item.status){
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

                        is ApiState.Error -> {
                            Text(state.message, color = Color.Red)
                        }

                        else -> {}
                    }
                }
            }
        }

        // 🚪 LOGOUT
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure?") },
                confirmButton = {
                    Button(onClick = {
                        val session = SessionManager(context)
                        session.clear()

                        nav.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        if (showWithdrawScreen) {
            AnimatedVisibility(
                visible = showWithdrawScreen,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Dialog(
                    onDismissRequest = { showWithdrawScreen = false }
                ) {

                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),   // ✅ FULL SCREEN
                        color = Color.White
                    ) {

                        Box(Modifier.fillMaxSize()) {

                            // 🔥 MAIN SCREEN
                            WithdrawScreen(walletVM)

                            // 🔥 CLOSE BUTTON (TOP RIGHT)
                            IconButton(
                                onClick = { showWithdrawScreen = false },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(50)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }


                }
            }
        }
    }}

    // 🔥 MODALS
    if (showCreate) CreateUserModal(vm) { showCreate = false }
    if (showUsers) UserListModal(vm) { showUsers = false }
    if (showTransaction) TransactionModal(vm) { showTransaction = false }
    if(showWithdraw){
        WithdrawPendingModal(walletVM) {
            showWithdraw = false
            vm.loadUsers()
            walletVM.loadProfile()
            walletVM.loadPending()
        }
    }
    // ✅ SUCCESS ANIMATION DIALOG
    vm.successMsg?.let {
        SuccessDialog(it) {
            vm.successMsg = null
        }
    }

    if (showChangePass) {

        val vm: AuthVM.ProfileVM = viewModel(
            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthVM.ProfileVM(AppRepository(context)) as T
                }
            }
        )

        val session = SessionManager(context)

        var oldPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }

        var confirmPass by remember { mutableStateOf("") }
        var showOld by remember { mutableStateOf(false) }
        var showNew by remember { mutableStateOf(false) }
        var showConfirm by remember { mutableStateOf(false) }

        fun passwordStrength(pass: String): String {
            return when {
                pass.length < 6 -> "Weak"
                pass.length in 6..8 -> "Medium"
                pass.length > 8 -> "Strong"
                else -> ""
            }
        }

        Dialog(onDismissRequest = { showChangePass = false }) {

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {

                Column(Modifier.padding(20.dp)) {

                    Text("🔐 Change Password",
                        style = MaterialTheme.typography.titleLarge)

                    Spacer(Modifier.height(16.dp))

                    // OLD PASSWORD
                    OutlinedTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it },
                        label = { Text("Old Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showOld) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showOld = !showOld }) {
                                Icon(
                                    if (showOld) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    // NEW PASSWORD
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showNew = !showNew }) {
                                Icon(
                                    if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    // 🔥 Strength
                    Text(
                        "Strength: ${passwordStrength(newPass)}",
                        color = when (passwordStrength(newPass)) {
                            "Weak" -> Color.Red
                            "Medium" -> Color(0xFFFFA500)
                            "Strong" -> Color.Green
                            else -> Color.Gray
                        },
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    // CONFIRM PASSWORD
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showConfirm = !showConfirm }) {
                                Icon(
                                    if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {

                            // 🔴 VALIDATION
                            if (newPass != confirmPass) {
                                vm.state = ApiState.Error("Password mismatch")
                                return@Button
                            }

                            if (newPass.length < 6) {
                                vm.state = ApiState.Error("Password too short")
                                return@Button
                            }

                            vm.changePassword(oldPass, newPass)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Password")
                    }

                    Spacer(Modifier.height(10.dp))

                    when (val state = vm.state) {

                        is ApiState.Loading -> {
                            CircularProgressIndicator()
                        }

                        is ApiState.Success -> {

                            Text(state.data, color = Color.Green)

                            // 🔥 SHOW MESSAGE → THEN LOGOUT
                            LaunchedEffect(Unit) {

                                kotlinx.coroutines.delay(1500)

                                // logout
                                val session = SessionManager(context)
                                session.clear()

                                nav.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }

                        is ApiState.Error -> {
                            Text(state.message, color = Color.Red)
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}


@Composable
fun CreateUserModal(vm: MasterVM, onClose: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Create User") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") })

                OutlinedTextField(
                    phone,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() } && it.length <= 10) {
                            phone = it
                        }
                    },
                    label = { Text("Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(pass, { pass = it }, label = { Text("Password") })
            }
        },
        confirmButton = {
            Button(onClick = {
                vm.createUser(name, phone, pass)
                onClose()
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("Close")
            }
        }
    )
}
@Composable
fun TransferToUserModal(vm: MasterVM, user: MasterUser, onClose: () -> Unit) {

    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Transfer to ${user.name}") },

        text = {
            Column {

                Text("Available: ${user.balance}")

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },

        confirmButton = {
            Button(onClick = {
                vm.transfer(user.user_code ?: user.phone, amount.toIntOrNull() ?: 0)
                onClose()
            }) {
                Text("Send")
            }
        },

        dismissButton = {
            TextButton(onClick = onClose) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun UserListModal(vm: MasterVM, onClose: () -> Unit) {

    var search by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<com.jminnovatech.sbclub.data.model.master.MasterUser?>(null) }

    val filtered = vm.users.filter {
        it.name.contains(search, true) ||
                it.phone.contains(search)
    }

    Dialog(onDismissRequest = onClose) {

        Surface(Modifier.fillMaxSize(), color = Color(0xFF0E0123)) {

            Column {

                // 🔝 HEADER
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("👥 Users", color = Color.White)

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, "", tint = Color.White)
                    }
                }

                // 🔍 SEARCH
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
                        .padding(2.dp) // border effect
                ) {

                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },

                        placeholder = {
                            Text("🔍 Search user...", color = Color.Gray)
                        },

                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF6366F1)
                            )
                        },

                        singleLine = true,

                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),

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

                // 📋 LIST
                LazyColumn {

                    items(filtered) { user ->

                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(Color(0xFF1E293B))
                        ) {

                            Column(Modifier.padding(12.dp)) {

                                Text(user.name, color = Color.White)

                                Text("📱 ${user.phone}", color = Color.White)
                                Text("🆔 ${user.user_code}", color = Color.White)
                                Text(" ${user.balance}", color = Color.Green)

                                Spacer(Modifier.height(6.dp))

                                Button(
                                    onClick = { selectedUser = user },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Transfer")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 🔥 DIRECT TRANSFER MODAL
    selectedUser?.let { user ->
        TransferToUserModal(vm, user) {
            selectedUser = null
        }
    }
}

@Composable
fun SuccessDialog(message: String, onClose: () -> Unit) {

    Dialog(onDismissRequest = onClose) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E293B)
        ) {

            Column(
                Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("✅", fontSize = 40.sp)

                Spacer(Modifier.height(10.dp))

                Text(message, color = Color.White)

                Spacer(Modifier.height(10.dp))

                Button(onClick = onClose) {
                    Text("OK")
                }
            }
        }
    }
}


@Composable
fun TransactionModal(vm: MasterVM, onClose: () -> Unit) {

    val state = vm.ledgerState

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) { Text("Close") }
        },
        title = { Text(" Transaction History") },
        text = {

            when (state) {

                is ApiState.Loading -> {
                    CircularProgressIndicator()
                }

                is ApiState.Success -> {

                    LazyColumn {

                        items(state.data) { item ->

                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1E293B)
                                )
                            ) {

                                Column(Modifier.padding(10.dp)) {

                                    Text(item.remark, color = Color.White)

                                    Text(
                                        "📅 ${item.date}",
                                        color = Color.White
                                    )

                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "➕ ${item.credit}",
                                            color = Color.Green
                                        )
                                        Text(
                                            "➖ ${item.debit}",
                                            color = Color.Red
                                        )
                                    }

                                    Text(
                                        " Balance: ${item.balance}",
                                        color = Color.Yellow
                                    )
                                }
                            }
                        }
                    }
                }

                is ApiState.Error -> {
                    Text(state.message, color = Color.Red)
                }

                else -> {}
            }
        }
    )
}


@Composable
fun WithdrawPendingModal(vm: WalletVM, onClose: () -> Unit) {

    val state = vm.pendingState
    val actionState = vm.withdrawState   // 🔥 approve/reject result

    var loadingId by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(onClick = onClose) {
                Text("Close")
            }
        },
        title = {
            Text("💸 Withdraw Requests", fontWeight = FontWeight.Bold)
        },
        text = {

            Column {

                // 🔥 GLOBAL LOADER
                if (actionState is ApiState.Loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }

                when (state) {

                    is ApiState.Loading -> {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ApiState.Success -> {

                        if (state.data.isEmpty()) {
                            Text("No pending requests", color = Color.Gray)
                        } else {

                            LazyColumn {

                                items(state.data) { item ->

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF0F172A)
                                        ),
                                        elevation = CardDefaults.cardElevation(6.dp)
                                    ) {

                                        Column(
                                            modifier = Modifier.padding(14.dp)
                                        ) {

                                            // 🔹 USER NAME
                                            Text(
                                                text = "👤 ${item.user_name ?: "User #${item.user_id}"}",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(Modifier.height(6.dp))

                                            // 🔹 AMOUNT
                                            Text(
                                                text = " ${item.amount}",
                                                color = Color(0xFFFFD54F),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(Modifier.height(6.dp))

                                            // 🔹 STATUS
                                            Text(
                                                text = "Status: ${item.status}",
                                                color = Color.Yellow
                                            )

                                            // 🔥 PAYMENT DETAILS (NEW 🔥)
                                            Spacer(Modifier.height(6.dp))

                                            Text(
                                                text = "💳 ${item.payment_type ?: ""} - ${item.account_number ?: ""}",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 13.sp
                                            )

                                            item.holder_name?.let {
                                                Text(
                                                    text = "👤 $it",
                                                    color = Color(0xFFCBD5F5),
                                                    fontSize = 13.sp
                                                )
                                            }

                                            Spacer(Modifier.height(12.dp))

                                            // 🔹 BUTTONS
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {

                                                Button(
                                                    onClick = {
                                                        loadingId = item.id
                                                        vm.approve(item.id)
                                                    },
                                                    enabled = loadingId != item.id,
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF16A34A)
                                                    )
                                                ) {

                                                    if (loadingId == item.id && actionState is ApiState.Loading) {
                                                        CircularProgressIndicator(
                                                            color = Color.White,
                                                            modifier = Modifier.size(16.dp),
                                                            strokeWidth = 2.dp
                                                        )


                                                    } else {
                                                        Text("Approve")
                                                    }
                                                }

                                                Button(
                                                    onClick = {
                                                        loadingId = item.id
                                                        vm.reject(item.id)
                                                    },
                                                    enabled = loadingId != item.id,
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFFDC2626)
                                                    )
                                                ) {

                                                    if (loadingId == item.id && actionState is ApiState.Loading) {
                                                        CircularProgressIndicator(
                                                            color = Color.White,
                                                            modifier = Modifier.size(16.dp),
                                                            strokeWidth = 2.dp
                                                        )
                                                    } else {
                                                        Text("Reject")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is ApiState.Error -> {
                        Text(state.message, color = Color.Red)
                    }

                    else -> {}
                }
            }
        }
    )
}
@Composable
fun ActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(6.dp),
        onClick = onClick
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,              // ✅ CENTER VERTICALLY
            horizontalAlignment = Alignment.CenterHorizontally    // ✅ CENTER HORIZONTALLY
        ) {

            Icon(
                icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center   // ✅ TEXT CENTER
            )
        }
    }
}