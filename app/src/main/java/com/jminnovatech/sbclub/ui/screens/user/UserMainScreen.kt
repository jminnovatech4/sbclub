package com.jminnovatech.sbclub.ui.screens

import android.content.Context
import android.os.Build
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jminnovatech.sbclub.data.model.ResultData
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.utils.SessionManager
import com.jminnovatech.sbclub.viewmodel.AuthVM
import com.jminnovatech.sbclub.viewmodel.WalletVM
import com.jminnovatech.sbclub.viewmodel.WalletVMFactory
import kotlinx.coroutines.launch
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScreen(nav: NavController, context: Context) {
    val vm: WalletVM = viewModel(
        factory = WalletVMFactory(AppRepository(context))
    )
    val authVM: AuthVM = viewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    // 🔥 DEFAULT SCREEN = BET
    var selectedScreen by remember { mutableStateOf("bet") }
    val session = SessionManager(context)
    var showWithdrawScreen by remember { mutableStateOf(false) }
    val userName = session.getName() ?: "User"
    val userId = session.getUserId() ?: "0"
    val balance = session.getBalance() ?: "0"
    var showChangePass by remember { mutableStateOf(false) }
    var showFullMsg by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        authVM.loadMessage(context)
        val res = AppRepository(context).getAppMessage()
        Log.d("MSG_DEBUG", res.message)

    }
    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E40AF))
                        .padding(16.dp)
                ) {

                    Text(
                        "👤 ${userName}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "ID: ${userId}",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(6.dp))

//                    Text(
//                        "Balance: ${balance}",
//                        color = Color.Green,
//                        fontSize = 14.sp
//                    )
                }

                NavigationDrawerItem(
                    label = { Text("🎯 Play Game") },
                    selected = selectedScreen == "bet",
                    onClick = {
                        selectedScreen = "bet"
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Casino, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("📊 summary") },
                    selected = selectedScreen == "summary",
                    onClick = {
                        selectedScreen = "summary"
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationDrawerItem(
                    label = { Text("🏆 Bet History") },
                    selected = selectedScreen == "result",
                    onClick = {
                        selectedScreen = "result"
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.EmojiEvents, null) }
                )


                NavigationDrawerItem(
                    label = { Text("📜 Transactions") },
                    selected = selectedScreen == "transactions",
                    onClick = {
                        selectedScreen = "transactions"
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.History, null) }
                )

                NavigationDrawerItem(
                    label = { Text(" Withdrawal Request") },
                    selected = selectedScreen == "wallet",
                    onClick = {
                        showWithdrawScreen = true   // ✅ OPEN MODAL
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.AccountBalance, null) }
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
                Spacer(Modifier.height(20.dp))

                Divider()

                NavigationDrawerItem(
                    label = { Text("🚪 Logout") },
                    selected = false,
                    onClick = {
                        showLogoutDialog = true
                    },
                    icon = { Icon(Icons.Default.ExitToApp, null) }
                )
            }
        }
    ) {

        Scaffold(

            topBar = {
                TopAppBar(
                    title = {

                        when (selectedScreen) {

                            "bet" -> {
                                Column {

                                    Text(
                                        "🎯 Play Game",
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Box(modifier = Modifier.weight(1f)) {
                                            NewsTicker(authVM.message)
                                        }

                                        // 🔥 FULL BUTTON
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                                    ),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { showFullMsg = true }
                                                .padding(horizontal = 3.dp, vertical = 1.dp),
                                            contentAlignment = Alignment.Center
                                        ) {

                                            Row(verticalAlignment = Alignment.CenterVertically) {

                                                Icon(
                                                    imageVector = Icons.Default.Message, // 🔥 built-in icon
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(11.dp)
                                                )

                                                Spacer(Modifier.width(4.dp))

                                                Text(
                                                    text = "View",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            "summary" -> Text("📊 Summary")
                            "wallet" -> Text(" Wallet")
                            "result" -> Text("🏆 Bet History")
                            "transactions" -> Text("📜 Transactions")
                            else -> Text("App")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    }
                )

            }

        ) { padding ->

            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E293B)
                            )
                        )
                    )
            ) {

                when (selectedScreen) {

                    // 🎯 MAIN GAME SCREEN
                    "bet" -> {
                        com.jminnovatech.sbclub.ui.screens.user.BetScreen(nav, context)
                    }

                    "summary" -> Summary(vm)

                    "wallet" -> WalletScreen()

                    "transactions" -> Transactions(vm)
                    "result" -> ResultHistoryScreen(vm)
                }
            }
        }
    }
    if (showFullMsg) {

        AlertDialog(
            onDismissRequest = { showFullMsg = false },

            title = {
                Text("📢 Full Message")
            },

            text = {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = authVM.message,
                        fontSize = 15.sp
                    )
                }
            },

            confirmButton = {
                Button(onClick = { showFullMsg = false }) {
                    Text("Close")
                }
            }
        )
    }
    if (showLogoutDialog) {

        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },

            title = { Text("Logout") },

            text = { Text("Are you sure you want to logout?") },

            confirmButton = {
                Button(onClick = {

                    val session = com.jminnovatech.sbclub.utils.SessionManager(context)
                    session.clear()

                    showLogoutDialog = false

                    nav.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }

                }) {
                    Text("Yes")
                }
            },

            dismissButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                }) {
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
                        WithdrawScreen(vm)

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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Transactions(vm: WalletVM) {

    val context = LocalContext.current

    // 🔥 LOAD ONLY ONCE (important)
    LaunchedEffect(Unit) {
        if (vm.ledgerState !is ApiState.Success) {
            vm.loadLedger()
        }
    }

    when (val state = vm.ledgerState) {

        is ApiState.Loading -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ApiState.Success -> {

            if (state.data.isEmpty()) {

                // 🔥 EMPTY STATE
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions found", color = Color.Gray)
                }

            } else {

                LazyColumn(
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(state.data) { item ->

                        val isCredit = item.credit > 0

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1E293B)
                            )
                        ) {

                            Column(Modifier.padding(12.dp)) {

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
                                            "+${item.credit}"
                                        else "-${item.debit}",
                                        color = if (isCredit) Color(0xFF4CAF50) else Color.Red,
                                        fontSize = 16.sp
                                    )

                                    Text(
                                        "Bal: ${item.balance}",
                                        color = Color(0xFF00E5FF)
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
                }
            }
        }

        is ApiState.Error -> {

            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text("Error loading history", color = Color.Red)

                Spacer(Modifier.height(10.dp))

                Button(onClick = {
                    vm.loadLedger() // 🔥 retry
                }) {
                    Text("Retry")
                }
            }
        }

        else -> {}
    }
}



@Composable
fun WalletScreen() {

    Column(Modifier.padding(16.dp)) {

        Text(" Wallet", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(10.dp))

        Text("Balance:  --")

        Spacer(Modifier.height(10.dp))

        Button(onClick = {}) {
            Text("Withdraw")
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Summary(vm: WalletVM) {

    LaunchedEffect(Unit) {
        vm.loadSummary()
    }

    when (val state = vm.summaryState) {

        is ApiState.Success -> {

            val data = state.data

            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    //  BALANCE CARD
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E40AF)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Available Balance", color = Color.LightGray)
                            Text(
                                "${data.balance}",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }

                item {
                    // 📊 STATS GRID
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard("Profit", data.profit_loss, Color.Green, Modifier.weight(1f))
                        StatCard("Today", data.today_profit, Color.Cyan, Modifier.weight(1f))
                    }
                }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard("Win", data.total_win, Color.Green, Modifier.weight(1f))
                        StatCard("Bet", data.total_bet, Color.Red, Modifier.weight(1f))
                    }
                }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard("Wins", data.win_count.toDouble(), Color.Yellow, Modifier.weight(1f))
                        StatCard("Avg Bet", data.avg_bet, Color.Magenta, Modifier.weight(1f))
                    }
                }
            }
        }

        is ApiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> {
            Text("Error loading summary", color = Color.Red)
        }
    }
}
@Composable
fun StatCard(title: String, value: Double, color: Color, modifier: Modifier) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Text("${value.toInt()}", color = color, fontSize = 16.sp)
        }
    }
}


@Composable
fun ResultHistoryScreen(vm: WalletVM) {
    val context = LocalContext.current   // 🔥 ADD HERE

    LaunchedEffect(Unit) {
        vm.loadResults(context)         // 🔥 ADD HERE
    }

    val isRefreshing = vm.resultState is ApiState.Loading

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { vm.loadResults(context) }
    ) {

        when (val state = vm.resultState) {

            is ApiState.Success -> {

                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(state.data.data) { item ->

                        ExpandableResultCard(item)
                    }
                }
            }

            is ApiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading results", color = Color.Red)
                }
            }
        }
    }
}
@Composable
fun ExpandableResultCard(item: ResultData) {

    var expanded by remember { mutableStateOf(false) }

    val cardColor = if (expanded) Color(0xFFE3F2FD) else Color(0xFFFFFFFF)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {

        Column(
            Modifier
                .clickable { expanded = !expanded }
                .padding(14.dp)
        ) {

            // 🔥 HEADER
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        "🎯 ${item.result}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )

                    Spacer(Modifier.width(8.dp))

                    val rotation by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f
                    )

                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                }

                Text(
                    formatServerTime(item.result_time),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            ResultRow(
                title = "Normal Result",
                result = item.result,
                isWin = item.normal_status == "win",
                amount = item.normal_profit
            )

            Spacer(Modifier.height(8.dp))

            ResultRow(
                title = "Patti Result",
                result = item.patti_result ?: "--",
                isWin = item.patti_status == "win",
                amount = item.patti_profit
            )

            // 🔥 EXPAND
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                Column {

                    Spacer(Modifier.height(12.dp))

                    Divider(color = Color.LightGray)

                    Spacer(Modifier.height(8.dp))

                    // 📊 SUMMARY
                    val totalBet = item.normal_total_bet + item.patti_total_bet
                    val totalWin = item.normal_total_win + item.patti_total_win
                    val profit = item.normal_profit + item.patti_profit

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFFFFF)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column {
                                Text("Total Bet", fontSize = 12.sp)
                                Text("$totalBet", fontWeight = FontWeight.Bold)
                            }

//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text("Win", fontSize = 12.sp)
//                                Text("$totalWin", color = Color(0xFF02AB0B))
//                            }
//
//                            Column(horizontalAlignment = Alignment.End) {
//                                Text("Profit", fontSize = 12.sp)
//                                Text(
//                                    if (profit >= 0) "+$profit" else "-${kotlin.math.abs(profit)}",
//                                    color = if (profit >= 0) Color.Green else Color.Red,
//                                    fontWeight = FontWeight.Bold
//                                )
//                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "🧾 My Bets",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(Modifier.height(6.dp))

                    // 🎯 BET LIST
                    if (item.my_bets.isEmpty()) {

                        Text(
                            "❌ No bet placed",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )

                    } else {

                        item.my_bets.forEach { bet ->

                            val isWin =
                                bet.number == item.result ||
                                        bet.number == item.patti_result

                            val glowColor = if (isWin) Color(0xFF4CAF50) else Color.Transparent

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(
                                        if (isWin) Color(0xFFE8F5E9) else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .shadow(
                                        if (isWin) 6.dp else 0.dp,
                                        RoundedCornerShape(8.dp),
                                        ambientColor = glowColor,
                                        spotColor = glowColor
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    Text(
                                        "🎯 ${bet.number}",
                                        color = if (isWin) Color(0xFF2E7D32) else Color(0xFF1565C0),
                                        fontWeight = if (isWin) FontWeight.Bold else FontWeight.Normal
                                    )

                                    if (isWin) {
                                        Spacer(Modifier.width(6.dp))
                                        Text("🏆", fontSize = 14.sp)
                                    }
                                }

                                Text(
                                    "${bet.amount}",
                                    color = if (isWin) Color(0xFF2E7D32) else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(
    title: String,
    result: String,
    isWin: Boolean,
    amount: Double
) {

    val bgColor = if (isWin) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val textColor = if (isWin) Color(0xFF2E7D32) else Color(0xFFC62828)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(title, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Text("🎯 $result", color = Color.Black)
        }

        Column(horizontalAlignment = Alignment.End) {

            Text(
                if (isWin) "WIN" else "",
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            if (isWin) {
                Text(
                    "+$amount",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatServerTime(input: String): String {
    return try {
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val formatter = java.text.SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.ENGLISH)

        val date = parser.parse(input.substring(0, 19)) // remove microseconds
        formatter.format(date!!)
    } catch (e: Exception) {
        input
    }
}

@Composable
fun NewsTicker(
    text: String,
    modifier: Modifier = Modifier,
    speedMs: Int = 16000 // বেশি = ধীরে
) {
    if (text.isEmpty()) return

    var textWidth by remember { mutableStateOf(0f) }
    var boxWidth by remember { mutableStateOf(0f) }
    var paused by remember { mutableStateOf(false) }

    val ready = textWidth > 0f

    val transition = rememberInfiniteTransition()

    val offsetX by transition.animateFloat(
        initialValue = -textWidth,   // 🔥 LEFT START
        targetValue = boxWidth,      // 🔥 RIGHT END
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = speedMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(26.dp)
            .clipToBounds()
            .onGloballyPositioned {
                boxWidth = it.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        paused = true
                        tryAwaitRelease()
                        paused = false
                    }
                )
            }
    ) {

        // 🔥 FADE EDGES (left & right)
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent,
                            Color.Transparent,
                            Color.White
                        ),
                        startX = 0f,
                        endX = boxWidth
                    )
                )
        )

        // 🔥 LOOP TEXT (continuous)
        Row(
            modifier = Modifier.offset {
                IntOffset(
                    x = if (!ready || paused) 0 else -offsetX.toInt(),
                    y = 0
                )
            }
        ) {

            Text(
                text = "$text     ", // gap for smooth loop
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red,
                            Color.Magenta,
                            Color.Green
                        )
                    )
                ),
                onTextLayout = {
                    textWidth = it.size.width.toFloat()
                }
            )

            Text(
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Red,
                            Color.Magenta,
                            Color.Green
                        )
                    )
                )
            )
        }
    }
}

//@Composable
//fun NewsTicker(text: String) {
//
//    if (text.isEmpty()) return
//
//    var textWidth by remember { mutableStateOf(0) }
//    var boxWidth by remember { mutableStateOf(0) }
//
//    val offset = remember { Animatable(0f) }
//
//    LaunchedEffect(textWidth, boxWidth, text) {
//
//        if (textWidth == 0 || boxWidth == 0) return@LaunchedEffect
//
//        while (true) {
//
//            offset.snapTo(0f)
//
//            offset.animateTo(
//                targetValue = textWidth.toFloat(),
//                animationSpec = tween(
//                    durationMillis = (textWidth * 12)
//                        .coerceIn(8000, 30000),
//                    easing = LinearEasing
//                )
//            )
//        }
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(32.dp)
//    ) {
//
//        // 🔴 LIVE
//        Box(
//            modifier = Modifier
//                .background(Color.Red)
//                .padding(horizontal = 8.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text("LIVE", color = Color.White, fontSize = 11.sp)
//        }
//
//        // 🔵 TICKER
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .background(Color.Black)
//                .clipToBounds()
//                .onGloballyPositioned {
//                    boxWidth = it.size.width
//                }
//        ) {
//
//            Row(
//                modifier = Modifier.offset {
//                    IntOffset(-offset.value.toInt(), 0)
//                }
//            ) {
//
//                // 🔥 FIRST TEXT (measure)
//                Text(
//                    text = "🔥 $text     ",
//                    color = Color.Yellow,
//                    fontSize = 14.sp,
//                    maxLines = 1,
//                    softWrap = false,
//                    modifier = Modifier.onGloballyPositioned {
//                        textWidth = it.size.width
//                    }
//                )
//
//                // 🔥 SECOND TEXT (loop)
//                Text(
//                    text = "🔥 $text",
//                    color = Color.Yellow,
//                    fontSize = 14.sp,
//                    maxLines = 1,
//                    softWrap = false
//                )
//            }
//        }
//    }
//}