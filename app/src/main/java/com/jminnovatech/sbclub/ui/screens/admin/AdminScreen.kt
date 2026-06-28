package com.jminnovatech.sbclub.ui.screens.admin
import android.os.Build
import android.os.CountDownTimer
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jminnovatech.sbclub.viewmodel.AdminVM
import com.jminnovatech.sbclub.ui.components.*
import com.jminnovatech.sbclub.ui.screens.master.WithdrawPendingModal
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.utils.SessionManager
import com.jminnovatech.sbclub.viewmodel.AuthVM
import com.jminnovatech.sbclub.viewmodel.WalletVM
import com.jminnovatech.sbclub.viewmodel.WalletVMFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog

fun parseTime(time:String): Long{
    return try{
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        sdf.parse(time)?.time ?: System.currentTimeMillis()
    }catch (e:Exception){
        System.currentTimeMillis()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(nav: NavController ) {

    val context = LocalContext.current
    val vm: AdminVM = viewModel()
    val authVM:  AuthVM = viewModel()
    fun refreshAll() {
        vm.loadCurrentRound(context)
        vm.loadCurrentPreview(context)
        vm.loadTransactions(context)
        vm.loadUsers(context)
    }
    // 🔥 STATES (EXISTING + NEW)
    var showMaster by remember { mutableStateOf(false) }
    var showGraph by remember { mutableStateOf(false) }
    var showMasterList by remember { mutableStateOf(false) }
    var showResultModal by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var betTime by remember { mutableStateOf("") }
    var resultTime by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf<String?>(null) }
    var showPendingAmountModal by remember { mutableStateOf(false) }
    val masterState = vm.createMasterState
    val profitState = vm.profitState
    val roundState = vm.roundState
    var showUserModal by remember { mutableStateOf(false) }
    var showTxModal by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showPattiModal by remember { mutableStateOf(false) }
    var showSuccessAnim by remember { mutableStateOf(false) }
    var showPattiListDialog by remember { mutableStateOf(false) }

    var showWithdraw by remember { mutableStateOf(false) }

    var showProfitModal by remember { mutableStateOf(false) }
    val walletVM: WalletVM = viewModel(
        factory = WalletVMFactory.WalletVMFactory(context)
    )
    var showMsgModal by remember { mutableStateOf(false) }
    var messageInput by remember { mutableStateOf("") }
    var showMasterReportModal by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(authVM.message) {
        messageInput = authVM.message
    }
// ✅ 🔥 এখানে বসাও (TOP LEVEL)
    LaunchedEffect(vm.createMasterState) {

        when(val res = vm.createMasterState){

            is ApiState.Success -> {
                successMsg = "Master Created ✔"
            }

            is ApiState.Error -> {
                Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }
    LaunchedEffect(Unit) {
        vm.loadCurrentRound(context)
    }
//    LaunchedEffect(Unit) {
//        while (true) {
//            kotlinx.coroutines.delay(10000) // 10 sec
//            refreshAll()
//        }
//    }
    LaunchedEffect(Unit) {
        vm.loadCurrentPreview(context)
    }

    LaunchedEffect(vm.manualState) {

        when (val state = vm.manualState) {

            is ApiState.Success -> {
                showSuccessAnim = true
                Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()

                showPattiModal = false

                vm.loadCurrentPreview(context)

                vm.manualState = ApiState.Idle // 🔥 reset
            }

            is ApiState.Error -> {

                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()

                vm.manualState = ApiState.Idle
            }

            else -> {}
        }
    }
    // 🔥 TIMER
    LaunchedEffect(vm.currentRoundState) {

        val state = vm.currentRoundState

        if(state is ApiState.Success){

            val data = state.data

            val end = data.end_time?.let { parseTime(it) } ?: System.currentTimeMillis()
            val result = data.result_time?.let { parseTime(it) } ?: System.currentTimeMillis()

            val now = System.currentTimeMillis()

            object : CountDownTimer(end - now,1000){
                override fun onTick(ms:Long){
                    val sec = ms / 1000
                    betTime = formatTime(sec) // 🔥 FIX
                }
                override fun onFinish(){
                    betTime = "Closed"
                }
            }.start()

            object : CountDownTimer(result - now,1000){
                override fun onTick(ms:Long){
                    val sec = ms / 1000
                    resultTime = formatTime(sec) // 🔥 FIX
                }
                override fun onFinish(){
                    resultTime = "Done"
                }
            }.start()
        }
    }

    // 🔥 DRAWER (UNCHANGED)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet {

                Column {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                                )
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {

                        Column {

                            Icon(Icons.Default.AccountCircle, null, tint = Color.White, modifier = Modifier.size(50.dp))

                            Spacer(Modifier.height(6.dp))

                            Text("Admin Panel", color = Color.White)
                            Text("Welcome Admin", color = Color.White.copy(0.7f))
                        }
                    }

                    Spacer(Modifier.height(5.dp))

                    DrawerItem("Dashboard", Icons.Default.Home) {
                        scope.launch { drawerState.close() }
                    }
                    DashboardItem("Update Message", Icons.Default.Edit) {
                        showMsgModal = true
                    }
                    DrawerItem("Create Master", Icons.Default.PersonAdd) {
                        showMaster = true
                        scope.launch { drawerState.close() }
                    }

//                    DrawerItem("Create Rounds", Icons.Default.Schedule) {
//                        vm.createRounds(context)
//                        scope.launch { drawerState.close() }
//                    }


                    DashboardItem("Users", Icons.Default.Group) {
                        showUserModal = true
                    }

                    DashboardItem("Transactions", Icons.Default.Receipt) {
                        showTxModal = true
                    }
                    DrawerItem("Pending Amount Transfer", Icons.Default.Payments) {
                        showPendingAmountModal = true
                    }
                    DrawerItem("Payment Transfer", Icons.Default.AccountBalanceWallet) {
                        showUserModal = true
                    }
                    DrawerItem("Pending Request", Icons.Default.AccountBalanceWallet) {
                        walletVM.loadPending()   // 🔥 load admin pending
                        showWithdraw = true
                    }

                    Divider()

                    DrawerItem("Logout", Icons.Default.ExitToApp, Color.Red) {
                        showLogoutDialog = true
                    }

                }
            }
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("👑 Admin Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            refreshAll()
                            Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            }
        ) { padding ->

            Box {

                Column(
                    Modifier
                        .padding(padding)
                        .padding(12.dp)
                ) {

                    // 🔥 TIMER UI
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // 🔵 BET TIMER
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("⏱ Bet Ends", color = Color.White)
                                Text(
                                    betTime,
                                    color = Color.Yellow,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            // 🔥 DIVIDER (optional nice look)
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(Color.White.copy(0.5f))
                            )

                            // 🔴 RESULT TIMER
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎯 Result In", color = Color.White)
                                Text(
                                    resultTime,
                                    color = Color.Cyan,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
// 🔥 LIVE PREVIEW DATA (ADD HERE)

                    if(vm.previewState is ApiState.Success){

                        val data = (vm.previewState as ApiState.Success).data

                        val list = data.numbers.orEmpty()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {

                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(16.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    // 🔵 LEFT SIDE (EXISTING UI — SAME)
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        // 🎯 ROUND + USERS
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "🎯 Round ${data.round_id}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleMedium
                                            )

                                            val totalUsers = list.sumOf { it.total_users }

                                            Text(
                                                "👥 $totalUsers",
                                                color = Color.White
                                            )
                                        }

                                        Spacer(Modifier.height(10.dp))

                                        Text(
                                            "₹${data.total_collection}",
                                            color = Color.Yellow,
                                            style = MaterialTheme.typography.headlineMedium
                                        )

                                        Text(
                                            "Total Collection",
                                            color = Color.White.copy(0.7f)
                                        )

                                        Spacer(Modifier.height(12.dp))

                                        Divider(color = Color.White.copy(0.3f))

                                        Spacer(Modifier.height(10.dp))
                                        if (data.normal_result_out == "yes") {

                                            Text(
                                                "🎯 Result: ${data.result_number}",
                                                color = Color.Yellow,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )

                                            Spacer(Modifier.height(6.dp))
                                        }
                                        Text(
                                            "🔥 Top Profit Numbers",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleSmall
                                        )

                                        Spacer(Modifier.height(6.dp))

                                        list.sortedByDescending { it.profit }
                                            .take(3)
                                            .forEachIndexed { index, item ->

                                                val medal = when(index){
                                                    0 -> "🥇"
                                                    1 -> "🥈"
                                                    else -> "🥉"
                                                }

                                                Row(
                                                    Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("$medal No ${item.number}", color = Color.White)
                                                    Text(
                                                        "₹${item.profit}",
                                                        color = if(item.profit < 0) Color.Red else Color.Green
                                                    )
                                                }
                                            }
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    // 🔴 RIGHT SIDE (🔥 PREMIUM PATTI CARD)
                                    Card(

                                        modifier = Modifier
                                            .weight(0.9f)
                                            .height(170.dp),
                                        shape = RoundedCornerShape(18.dp),
                                        elevation = CardDefaults.cardElevation(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF0F172A)
                                        )
                                    ) {
                                        val infinite = rememberInfiniteTransition()

                                        val glowAlpha by infinite.animateFloat(
                                            initialValue = 0.4f,
                                            targetValue = 1f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(800),
                                                repeatMode = RepeatMode.Reverse
                                            )
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {

                                            // 🔥 TITLE
                                            Text(
                                                "🎴 Patti",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )

                                            // 🔥 STATUS CHIP
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (data.patti_result_out == "yes")
                                                            Color(0xFF22C55E)
                                                        else
                                                            Color(0xFFEF4444),
                                                        shape = RoundedCornerShape(50)
                                                    )
                                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                                            ) {
                                                Text(
                                                    if (data.patti_result_out == "yes") "RESULT OUT" else "PENDING",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            if (data.patti_result_out == "yes") {

                                                Text(
                                                    "🎯 ${data.patti_result_number ?: "---"}",
                                                    color = Color.Yellow.copy(alpha = glowAlpha),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 22.sp
                                                )
                                            }
                                            // 🔥 PATTI CHIPS (BEST PART)
                                            if (data.patti_numbers.isNotEmpty()) {

                                                FlowRow(
                                                    horizontalArrangement = Arrangement.Center,
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {


                                                }

                                            } else {
                                                Text(
                                                    "No Patti Bet",
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )
                                            }

                                            // 🔥 VIEW BUTTON
                                            Button(
                                                onClick = { showPattiModal = true },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF2563EB)
                                                )
                                            ) {
                                                Text("View Details")
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    DashboardItem("Create Master", Icons.Default.PersonAdd) { showMaster = true }
//                    DashboardItem("Create Rounds", Icons.Default.Schedule) { vm.createRounds(context) }
                    DashboardItem("Result", Icons.Default.EmojiEvents) { showResultModal = true }
                    DrawerItem("Pending Amount Transfer", Icons.Default.Payment) {
                        showPendingAmountModal = true
                    }
                    DrawerItem("📊 Admin Profit Report", Icons.Default.BarChart) {
                        vm.loadProfitList(context)
                        showProfitModal = true
                        scope.launch { drawerState.close() }
                    }
                    DrawerItem("📊 Master Team Report", Icons.Default.BarChart) {
                        val today = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(Date())

                        vm.loadMasterReport(
                            context,
                            today,
                            today
                        )

                        showMasterReportModal = true
                        scope.launch { drawerState.close() }
                    }

                    DashboardItem("User Payment Transfer", Icons.Default.AccountBalanceWallet) { showUserModal = true }

                    if (profitState is ApiState.Error) {
                        Text(profitState.message, color = Color.Red)
                    }
                }

                // 🔥 LOADER
                if (
                    masterState is ApiState.Loading ||
                    profitState is ApiState.Loading ||
                    roundState is ApiState.Loading ||
                    vm.previewState is ApiState.Loading
                ) {
                    LoaderPro()
                }
            }
        }


        AnimatedVisibility(
            visible = showSuccessAnim
        ) {

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1500)
                showSuccessAnim = false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.4f)),
                contentAlignment = Alignment.Center
            ) {

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16A34A))
                ) {

                    Column(
                        modifier = Modifier.padding(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("✔", fontSize = 50.sp, color = Color.White)

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "Success",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }

    // 🔥 SUCCESS DIALOG
    if(successMsg != null){
        AlertDialog(
            onDismissRequest = { successMsg = null },
            confirmButton = {
                Button(onClick = { successMsg = null }) {
                    Text("OK")
                }
            },
            title = { Text("Success") },
            text = { Text(successMsg!!) }
        )
    }

    // 🔥 CREATE MASTER
    if (showMaster) {
        CreateMasterDialog(
            onClose = { showMaster = false },
            onSubmit = { name, phone, pass ->
                vm.createMaster(context, name, phone, pass)
                showMaster = false   // 🔥 successMsg এখান থেকে সরাও
            }
        )
    }
    if(showUserModal){
        UserListModal {
            showUserModal = false
        }
    }

    if(showTxModal){
        TransactionModal {
            showTxModal = false
        }
    }


    if(showPendingAmountModal){
        PaymentTransferModal(vm) {
            showPendingAmountModal = false
        }
    }

    // 🔥 RESULT (UPDATED)
    val isBetClosed = betTime == "Closed"

    if (showResultModal) {
        ResultPreviewModal(
            onClose = {
                showResultModal = false
                refreshAll()
            },
            isBetClosed = isBetClosed,
            betTime = betTime,
            resultTime = resultTime
        )
    }
    var selectedNumber by remember { mutableStateOf<Int?>(null) }
    var showConfirm by remember { mutableStateOf(false) }
    // 🔥 LOGOUT
    if (showLogoutDialog) {

        val session = SessionManager(context)

        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },

            confirmButton = {
                Button(
                    onClick = {

                        // 🔥 clear session
                        val session = SessionManager(context)
                        session.clear()

                        // 🔥 close dialog
                        showLogoutDialog = false

                        // 🔥 navigate to login (clear backstack)
                        nav.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("Logout")
                }
            },

            dismissButton = {
                Button(onClick = {
                    showLogoutDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
    if (showPattiModal && vm.previewState is ApiState.Success) {

        val data = (vm.previewState as ApiState.Success).data

        var pattiInput by remember(showPattiModal) { mutableStateOf("") }
        var showPattiListDialog by remember { mutableStateOf(false) }

        Column {

            // 🔥 MAIN PATTI MODAL
            AlertDialog(
                onDismissRequest = { showPattiModal = false },

                title = {
                    Text("🎴 Patti Result Entry")
                },

                text = {
                    Column {

                        // 💰 COLLECTION CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "💰 Collection: ₹${data.patti_collection}",
                                modifier = Modifier.padding(10.dp),
                                color = Color(0xFF22C55E),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // 🔢 INPUT FIELD
                        OutlinedTextField(
                            value = pattiInput,
                            onValueChange = {
                                if (it.length <= 3 && it.all { ch -> ch.isDigit() }) {
                                    pattiInput = it
                                }
                            },
                            label = { Text("Enter Patti (3 digit)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        // 📊 VIEW FULL LIST BUTTON
                        Button(
                            onClick = { showPattiListDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            )
                        ) {
                            Text("📊 View Full Patti List")
                        }

                        Spacer(Modifier.height(10.dp))

                        // 🔴 QUICK LIST (TOP 5)
                        if (data.patti_numbers.isNotEmpty()) {

                            LazyColumn(
                                modifier = Modifier.heightIn(max = 120.dp)
                            ) {

                                items(data.patti_numbers.take(5)) { item ->

                                    val payout = item.total_bet * 120
                                    val profit = data.patti_collection - payout
                                    val profitColor = if (profit >= 0) Color(0xFF22C55E) else Color.Red

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF374151), RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                            .clickable {
                                                pattiInput = item.number.toString() // 🔥 auto fill
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Text("🎴 ${item.number}", color = Color.White)

                                        Text("👥 ${item.total_users}", color = Color(0xFF93C5FD))

                                        Text("₹${item.total_bet}", color = Color(0xFF22C55E))

                                        Text(
                                            if (profit >= 0) "₹$profit" else "-₹${-profit}",
                                            color = profitColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                        } else {
                            Text("No Patti Bets", color = Color.Gray)
                        }
                    }
                },

                confirmButton = {
                    Button(
                        onClick = {

                            // 🔴 VALIDATION
                            if (pattiInput.length != 3) {
                                Toast.makeText(context, "Enter 3 digit", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val exists = data.patti_numbers.any {
                                it.number.toString() == pattiInput
                            }

//                            if (exists) {
//                                Toast.makeText(context, "Already used ❌", Toast.LENGTH_SHORT).show()
//                                return@Button
//                            }

                            // ✅ API CALL
                            vm.manualPattiResult(context, pattiInput)
                        },
                        enabled = vm.manualState !is ApiState.Loading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C3AED)
                        )
                    ) {
                        Text(
                            if (vm.manualState is ApiState.Loading)
                                "Processing..."
                            else
                                "Submit"
                        )
                    }
                },

                dismissButton = {
                    OutlinedButton(
                        onClick = { showPattiModal = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )

            // 🔥 LOADING TEXT
            if (vm.manualState is ApiState.Loading) {
                Spacer(Modifier.height(8.dp))
                Text("Processing...", color = Color.Gray)
            }
        }

        // 🔥 FULL LIST DIALOG (SECOND MODAL)
        if (showPattiListDialog) {

            AlertDialog(
                onDismissRequest = { showPattiListDialog = false },

                title = {
                    Text("📊 Patti Full Report")
                },

                text = {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    ) {

                        items(data.patti_numbers.sortedByDescending { it.total_bet }) { item ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1F2937)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    Text(
                                        "🎴 ${item.number}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        "👥 ${item.total_users}",
                                        color = Color(0xFF93C5FD)
                                    )

                                    Text(
                                        "₹${item.total_bet}",
                                        color = Color(0xFF22C55E),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },

                confirmButton = {
                    Button(
                        onClick = { showPattiListDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
    if (showPattiListDialog && vm.previewState is ApiState.Success) {

        val data = (vm.previewState as ApiState.Success).data

        AlertDialog(
            onDismissRequest = { showPattiListDialog = false },

            title = {
                Text("📊 Patti Full Report")
            },

            text = {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {

                    items(data.patti_numbers.sortedByDescending { it.total_bet }) { item ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1F2937)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // 🎴 NUMBER
                                Text(
                                    "🎴 ${item.number}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                // 👥 USERS
                                Text(
                                    "👥 ${item.total_users}",
                                    color = Color(0xFF93C5FD),
                                    fontWeight = FontWeight.Medium
                                )

                                // 💰 AMOUNT
                                Text(
                                    "₹${item.total_bet}",
                                    color = Color(0xFF22C55E),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },

            confirmButton = {
                Button(
                    onClick = { showPattiListDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close")
                }
            }
        )
    }

    if(showWithdraw){
        WithdrawPendingModal(walletVM) {
            showWithdraw = false

            walletVM.loadProfile()
            walletVM.loadPending()
        }
    }

    if (showProfitModal) {
        ProfitReportModal(vm) {
            showProfitModal = false
        }
    }

    if (showMsgModal) {

        val state = vm.updateState
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { showMsgModal = false },

            title = {
                Text(
                    "📢 Update App Message",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {

                Column {

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        label = { Text("Enter message") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // 🔥 LIVE PREVIEW
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        )
                    ) {
                        Text(
                            messageInput.ifEmpty { "Preview message..." },
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // 🔄 LOADING
                    if (state is ApiState.Loading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // ❌ ERROR
                    if (state is ApiState.Error) {
                        Text(state.message, color = Color.Red)
                    }

                    // ✅ SUCCESS
                    if (state is ApiState.Success) {
                        LaunchedEffect(state) {
                            Toast.makeText(context, "Updated ✔", Toast.LENGTH_SHORT).show()
                            showMsgModal = false
                            vm.updateState = null
                        }
                    }
                }
            },

            confirmButton = {
                Button(
                    onClick = {
                        if (messageInput.isEmpty()) {
                            Toast.makeText(context, "Enter message", Toast.LENGTH_SHORT).show()
                        } else {
                            vm.updateMessage(context, messageInput)
                        }
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Update")
                }
            },

            dismissButton = {
                OutlinedButton(
                    onClick = { showMsgModal = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showMasterReportModal) {

        MasterTeamReportModal(
            vm = vm,
            onClose = {
                showMasterReportModal = false
            }
        )
    }
}

@Composable
fun PaymentTransferModal(
    vm: AdminVM,
    onClose: () -> Unit
){
    val context = LocalContext.current
    val state = vm.pendingState
    val transferState = vm.transferState

    LaunchedEffect(Unit){
        vm.loadPending(context)
    }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {},
        title = {
            Text(
                "💰 Payment Transfer",
                fontWeight = FontWeight.Bold
            )
        },
        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                when(state){

                    // 🔄 LOADING
                    is ApiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }

                    // ✅ SUCCESS DATA
                    is ApiState.Success -> {

                        val list = state.data.data
                        val totalPending = state.data.total_pending
                        val canTransfer = totalPending > 0
                        // 🔥 TOTAL
                        Text(
                            text = "Total Pending: ₹${state.data.total_pending}",
                            fontWeight = FontWeight.SemiBold,
                            color = if(state.data.total_pending > 0)
                                Color(0xFF2E7D32)
                            else Color.Gray
                        )

                        Spacer(Modifier.height(8.dp))

                        // ❌ NO PENDING MESSAGE
                        if(list.isEmpty()){
                            Text(
                                "No pending payments available",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        // 🔥 SCROLLABLE LIST
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                        ) {

                            items(list) { item ->

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    elevation = CardDefaults.cardElevation(6.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ){
                                    Column(
                                        Modifier.padding(12.dp)
                                    ){

                                        Text(
                                            item.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )

                                        Spacer(Modifier.height(2.dp))

                                        Text(
                                            "📞 ${item.phone}",
                                            fontSize = 13.sp
                                        )

                                        Spacer(Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ){
                                            Text(
                                                "Pending: ₹${item.pending_amount}",
                                                color = Color.Red,
                                                fontSize = 13.sp
                                            )

                                            Text(
                                                "Wallet: ₹${item.available_amount}",
                                                color = Color(0xFF1565C0),
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // 🚀 TRANSFER BUTTON
                        Button(
                            onClick = {
                                vm.transferAll(context)
                            },
                            enabled = canTransfer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "🚀 Transfer To All",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // ❌ ERROR
                    is ApiState.Error -> {
                        Text(
                            state.message,
                            color = Color.Red
                        )
                    }

                    else -> {}
                }

                // 🔄 TRANSFER LOADING
                if(transferState is ApiState.Loading){
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // ✅ SUCCESS
                if(transferState is ApiState.Success){
                    LaunchedEffect(transferState){
                        Toast.makeText(context, transferState.data, Toast.LENGTH_SHORT).show()
                        onClose()
                    }
                }
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
                                                text = "💰 ₹${item.amount}",
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfitReportModal(
    vm: AdminVM,
    onClose: () -> Unit
) {
    val state = vm.profitListState
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }


    val dateRangePickerState = rememberDateRangePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onClose) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF8FAFC) // 🔥 LIGHT BG
        ) {

            Column {

                // 🔝 HEADER
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "📊 Profit Report",
                        color = Color(0xFF0F172A),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, null, tint = Color.Black)
                    }
                }

                Divider(color = Color(0xFFE2E8F0))
                IconButton(onClick = {
                    showDatePicker = true
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
                if (showDatePicker) {

                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {

                            val context = LocalContext.current

                            Button(onClick = {

                                showDatePicker = false

                                val start = dateRangePickerState.selectedStartDateMillis
                                val end = dateRangePickerState.selectedEndDateMillis

                                if (start != null && end != null) {

                                    val from = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .format(Date(start))

                                    val to = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .format(Date(end))

                                    vm.loadProfitList(context, from, to)
                                }

                            }) {
                                Text("Apply")
                            }
                        }
                    ) {

                        DateRangePicker(
                            state = dateRangePickerState,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                when (state) {

                    is ApiState.Loading -> {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ApiState.Success -> {

                        val list = state.data
                        val chartData = list.take(7) // 🔥 last 7 days (weekly)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {

                            Text(
                                "📊 Last 7 Days Profit",
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(10.dp))

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                chartData.forEach { day ->

                                    val height = (day.profit / 1000).coerceAtLeast(10.0)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        Box(
                                            modifier = Modifier
                                                .width(20.dp)
                                                .height(height.dp)
                                                .background(
                                                    if (day.profit >= 0)
                                                        Color(0xFF22C55E)
                                                    else Color.Red,
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            day.date.take(2),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                        LazyColumn {

                            items(list) { day ->

                                var expanded by remember { mutableStateOf(false) }

                                val profitColor =
                                    if (day.profit >= 0) Color(0xFF16A34A)
                                    else Color(0xFFDC2626)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .clickable { expanded = !expanded },

                                    shape = RoundedCornerShape(18.dp),

                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),

                                    elevation = CardDefaults.cardElevation(6.dp)
                                ) {

                                    Column(Modifier.padding(16.dp)) {

                                        // 🔥 DATE + PROFIT
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {

                                            Column {

                                                Text(
                                                    "📅 ${day.date}",
                                                    color = Color(0xFF0F172A),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )

                                                Spacer(Modifier.height(4.dp))

                                                Text(
                                                    "Collection ₹${day.collection}",
                                                    color = Color(0xFF475569),
                                                    fontSize = 12.sp
                                                )
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.End
                                            ) {

                                                Text(
                                                    "₹${day.profit}",
                                                    color = profitColor,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp
                                                )

                                                Text(
                                                    "Profit",
                                                    color = Color.Gray,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(10.dp))

                                        // 🔥 COLLECTION vs PAYOUT BAR
                                        LinearProgressIndicator(
                                            progress = if (day.collection > 0)
                                                (day.payout / day.collection).toFloat()
                                            else 0f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp),
                                            color = Color(0xFFEF4444),
                                            trackColor = Color(0xFF22C55E)
                                        )

                                        Spacer(Modifier.height(6.dp))

                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("💰 Earn ₹${day.collection}", fontSize = 11.sp)
                                            Text("💸 Payout ₹${day.payout}", fontSize = 11.sp)
                                        }

                                        // 🔥 EXPANDABLE SECTION
                                        AnimatedVisibility(expanded) {

                                            Column {

                                                Spacer(Modifier.height(12.dp))
                                                Divider()
                                                Spacer(Modifier.height(8.dp))

                                                day.games.forEach { game ->

                                                    val gColor =
                                                        if (game.profit >= 0) Color(0xFF16A34A)
                                                        else Color(0xFFDC2626)

                                                    Card(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 4.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = Color(0xFFF1F5F9)
                                                        ),
                                                        shape = RoundedCornerShape(12.dp)
                                                    ) {

                                                        Row(
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .padding(10.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {

                                                            Text(
                                                                "🕐 ${game.time}",
                                                                color = Color(0xFF0F172A),
                                                                fontWeight = FontWeight.Medium
                                                            )

                                                            Text(
                                                                "₹${game.profit}",
                                                                color = gColor,
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
                        }
                    }

                    is ApiState.Error -> {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(state.message, color = Color.Red)
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun MessageNoteBox(
    value: String,
    onChange: (String) -> Unit
) {
    Column {

        // 🔷 HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "📝 App Message",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // 🔥 CLEAR BUTTON
            Text(
                "Clear",
                color = Color(0xFFEF4444),
                modifier = Modifier
                    .clickable { onChange("") }
                    .padding(6.dp)
            )
        }

        Spacer(Modifier.height(6.dp))

        // 🔥 NOTE STYLE BOX
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Color(0xFFFFFBEB), // soft note yellow
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    1.dp,
                    Color(0xFFFDE68A),
                    RoundedCornerShape(16.dp)
                )
                .padding(10.dp)
        ) {

            BasicTextField(
                value = value,
                onValueChange = { onChange(it) },
                modifier = Modifier.fillMaxSize(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 15.sp,
                    color = Color.Black
                ),
                decorationBox = { inner ->

                    if (value.isEmpty()) {
                        Text(
                            "Type your message here...",
                            color = Color.Gray
                        )
                    }

                    inner()
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        // 🔢 CHARACTER COUNT
        Text(
            "${value.length}/150",
            fontSize = 11.sp,
            color = if (value.length > 150) Color.Red else Color.Gray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun MasterTeamReportModal(
    vm: AdminVM,
    onClose: () -> Unit
) {

    val state = vm.masterReportState

    val context = LocalContext.current

    val calendar = Calendar.getInstance()

    // 🔥 DEFAULT TODAY
    var fromDate by remember {

        mutableStateOf(

            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())
        )
    }

    var toDate by remember {

        mutableStateOf(

            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())
        )
    }

    Dialog(
        onDismissRequest = onClose
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF020817)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // 🔥 TOP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF2563EB),
                                    Color(0xFF1D4ED8)
                                )
                            )
                        )
                        .padding(14.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            "📊 Master Team Report",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            "Today Analytics",
                            color = Color.White.copy(0.7f),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onClose
                    ) {

                        Icon(
                            Icons.Default.Close,
                            null,
                            tint = Color.White
                        )
                    }
                }

                // 🔥 DATE RANGE SECTION
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),

                    shape = RoundedCornerShape(18.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF111827)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            "📅 Select Date Range",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(
                            Modifier.height(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            // 🔥 FROM
                            OutlinedButton(
                                modifier = Modifier.weight(1f),

                                onClick = {

                                    DatePickerDialog(
                                        context,

                                        { _, y, m, d ->

                                            fromDate =
                                                String.format(
                                                    "%04d-%02d-%02d",
                                                    y,
                                                    m + 1,
                                                    d
                                                )

                                        },

                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            ) {

                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        "FROM",
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )

                                    Text(
                                        fromDate,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // 🔥 TO
                            OutlinedButton(
                                modifier = Modifier.weight(1f),

                                onClick = {

                                    DatePickerDialog(
                                        context,

                                        { _, y, m, d ->

                                            toDate =
                                                String.format(
                                                    "%04d-%02d-%02d",
                                                    y,
                                                    m + 1,
                                                    d
                                                )

                                        },

                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            ) {

                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        "TO",
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )

                                    Text(
                                        toDate,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(
                            Modifier.height(10.dp)
                        )

                        Button(
                            onClick = {

                                vm.loadMasterReport(
                                    context,
                                    fromDate,
                                    toDate
                                )
                            },

                            modifier = Modifier.fillMaxWidth(),

                            shape = RoundedCornerShape(12.dp),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            )
                        ) {

                            Icon(
                                Icons.Default.Search,
                                null
                            )

                            Spacer(
                                Modifier.width(6.dp)
                            )

                            Text("Load Report")
                        }
                    }
                }

                // 🔥 CONTENT
                when (state) {

                    is ApiState.Loading -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator()
                        }
                    }

                    is ApiState.Success -> {

                        val response = state.data

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp)
                        ) {

                            items(response.data) { master ->

                                var expanded by remember {
                                    mutableStateOf(false)
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),

                                    shape = RoundedCornerShape(22.dp),

                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF0F172A)
                                    )
                                ) {

                                    Column(
                                        modifier = Modifier.padding(14.dp)
                                    ) {

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),

                                            horizontalArrangement =
                                                Arrangement.SpaceBetween,

                                            verticalAlignment =
                                                Alignment.CenterVertically
                                        ) {

                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {

                                                Text(
                                                    text =
                                                        "👑 ${master.master_name}",

                                                    color = Color.White,

                                                    fontWeight =
                                                        FontWeight.Bold,

                                                    fontSize = 14.sp,

                                                    maxLines = 1
                                                )

                                                Spacer(
                                                    Modifier.height(4.dp)
                                                )

                                                Text(
                                                    text =
                                                        "📞 ${master.master_phone}",

                                                    color = Color.White,

                                                    fontSize = 12.sp
                                                )
                                            }

                                            Column(
                                                modifier =
                                                    Modifier.width(120.dp),

                                                horizontalAlignment =
                                                    Alignment.End
                                            ) {

                                                Text(
                                                    text =
                                                        "₹${master.total_bet}",

                                                    color =
                                                        Color(0xFF4ADE80),

                                                    fontWeight =
                                                        FontWeight.Bold,

                                                    fontSize = 16.sp,

                                                    maxLines = 1,

                                                    softWrap = false
                                                )

                                                Text(
                                                    "Total Bet",

                                                    color = Color.LightGray,

                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Spacer(
                                            Modifier.height(14.dp)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),

                                            horizontalArrangement =
                                                Arrangement.SpaceBetween
                                        ) {

                                            MiniStatCard(
                                                "Users",
                                                master.total_users.toString(),
                                                Color(0xFFEEECEC)
                                            )

                                            MiniStatCard(
                                                "Commission",
                                                "₹${master.total_commission}",
                                                Color(0xFF16A34A)
                                            )

                                            MiniStatCard(
                                                "Online",
                                                master.online_users.toString(),
                                                Color(0xFFF59E0B)
                                            )
                                        }

                                        Spacer(
                                            Modifier.height(14.dp)
                                        )

                                        Button(
                                            onClick = {
                                                expanded = !expanded
                                            },

                                            modifier =
                                                Modifier.fillMaxWidth(),

                                            colors =
                                                ButtonDefaults.buttonColors(
                                                    containerColor =
                                                        Color.White
                                                )
                                        ) {

                                            Text(
                                                if (expanded)
                                                    "Hide Daily Report"
                                                else
                                                    "View Daily Report",

                                                color = Color.Black
                                            )
                                        }

                                        AnimatedVisibility(expanded) {

                                            Column(
                                                modifier =
                                                    Modifier.padding(top = 10.dp)
                                            ) {

                                                master.daily.forEach { day ->

                                                    Card(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 5.dp),

                                                        colors =
                                                            CardDefaults.cardColors(
                                                                containerColor =
                                                                    Color(0xFF1E293B)
                                                            )
                                                    ) {

                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(14.dp),

                                                            horizontalArrangement =
                                                                Arrangement.SpaceBetween
                                                        ) {

                                                            Column {

                                                                Text(
                                                                    "📅 ${day.date}",
                                                                    color = Color.White,
                                                                    fontWeight =
                                                                        FontWeight.Bold
                                                                )

                                                                Spacer(
                                                                    Modifier.height(4.dp)
                                                                )

                                                                Text(
                                                                    "👥 ${day.users} Users",
                                                                    color = Color.LightGray,
                                                                    fontSize = 12.sp
                                                                )
                                                            }

                                                            Column(
                                                                horizontalAlignment =
                                                                    Alignment.End
                                                            ) {

                                                                Text(
                                                                    "₹${day.bet_amount}",
                                                                    color = Color(
                                                                        0xFF4ADE80
                                                                    ),
                                                                    fontWeight =
                                                                        FontWeight.Bold
                                                                )

                                                                Spacer(
                                                                    Modifier.height(3.dp)
                                                                )

                                                                Text(
                                                                    "Com ₹${day.commission}",
                                                                    color = Color(
                                                                        0xFF60A5FA
                                                                    ),
                                                                    fontSize = 12.sp
                                                                )
                                                            }
                                                        }
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

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                state.message,
                                color = Color.Red
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
@Composable
fun MiniStatCard(
    title: String,
    value: String,
    color: Color
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(0.12f)
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                title,
                color = color,
                fontSize = 11.sp
            )

            Spacer(
                Modifier.height(2.dp)
            )

            Text(
                value,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}