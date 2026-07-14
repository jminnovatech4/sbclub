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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.jminnovatech.sbclub.data.model.admin.game.AdminGameItem
import com.jminnovatech.sbclub.data.model.admin.game.AdminRateItem
import com.jminnovatech.sbclub.data.model.admin.game.AdminScheduleItem
import com.jminnovatech.sbclub.ui.screens.admin.game.AdminRateModal
import com.jminnovatech.sbclub.ui.screens.admin.game.AdminScheduleModal
import com.jminnovatech.sbclub.ui.screens.admin.game.PublishResultDialog

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
        vm.loadGameDashboard(context)

        vm.loadGames(context)

        vm.loadCurrentRound(context)

        vm.loadCurrentPreview(context)

        vm.loadTransactions(context)

        vm.loadUsers(context)

        vm.loadPending(context)

    }
    // 🔥 STATES (EXISTING + NEW)
    var showMaster by remember { mutableStateOf(false) }
    var showGraph by remember { mutableStateOf(false) }
    var showMasterList by remember { mutableStateOf(false) }

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
    // ==========================
// ADMIN GAME
// ==========================
    var selectedGameId by remember { mutableStateOf<Int?>(null) }

    var showEditSchedule by remember { mutableStateOf(false) }

    var showEditRate by remember { mutableStateOf(false) }

    var showPublishResult by remember { mutableStateOf(false) }

    var selectedSchedule by remember { mutableStateOf<AdminScheduleItem?>(null) }

    var selectedRate by remember { mutableStateOf<AdminRateItem?>(null) }
    LaunchedEffect(authVM.message) {
        messageInput = authVM.message
    }

    var showScheduleModal by remember {
        mutableStateOf(false)
    }

    var selectedGame by remember {
        mutableStateOf<AdminGameItem?>(null)
    }
    var showRateModal by remember {
        mutableStateOf(false)
    }
    var showResultModal by remember {
        mutableStateOf(false)
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

        vm.loadGameDashboard(context)

        vm.loadGames(context)

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




                    DashboardItem("Users", Icons.Default.Group) {
                        showUserModal = true
                    }

                    DashboardItem("Transactions", Icons.Default.Receipt) {
                        showTxModal = true
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {


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



                                    Spacer(Modifier.width(12.dp))


                                }
                            }
                        }
                    }
                    when(val state = vm.gameDashboardState){

                        is ApiState.Success ->{

                            val summary = state.data.summary

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ){

                                Column(
                                    Modifier.padding(16.dp)
                                ){

                                    Text(
                                        "Games : ${summary.games}"
                                    )

                                    Text(
                                        "Running : ${summary.running_games}"
                                    )

                                    Text(
                                        "Today's Bet : ₹${summary.today_bet}"
                                    )

                                    Text(
                                        "Today's Win : ₹${summary.today_win}"
                                    )

                                }

                            }

                        }

                        is ApiState.Loading->{

                            CircularProgressIndicator()

                        }

                        else->{}

                    }
                    Spacer(Modifier.height(12.dp))
                    if (vm.gameDashboardState is ApiState.Success) {

                        val dashboard =
                            (vm.gameDashboardState as ApiState.Success).data

                        Spacer(Modifier.height(16.dp))

                        dashboard.games.forEach { game ->

//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 6.dp)
//                            ) {
//
//                                Column(
//                                    Modifier.padding(16.dp)
//                                ) {
//
//                                    Text(
//                                        game.game_name,
//                                        style = MaterialTheme.typography.titleMedium
//                                    )
//
//                                    Spacer(Modifier.height(10.dp))
//
//                                    Row {
//
////                                        Button(
////                                            onClick = {
////                                                selectedGame = game
////
////                                                showScheduleModal = true
////
////                                            }
////                                        ) {
////                                            Text("Schedules")
////                                        }
//
////                                        Spacer(Modifier.width(8.dp))
////
////                                        Button(
////                                            onClick = {
////                                                // Rates
////                                                selectedGame = game
////
////                                                showRateModal = true
////                                            }
////                                        ) {
////                                            Text("Rates")
////                                        }
//
//                                        Spacer(Modifier.width(8.dp))
//
//                                        Button(
//                                            onClick = {
//                                                selectedGame = game
//
//                                                showResultModal = true
//                                            }
//                                        ) {
//                                            Text("Result")
//                                        }
//                                    }
//                                }
//                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(18.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = game.game_name,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            text = "Publish & Manage Results",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                    }

                                    FilledTonalButton(
                                        onClick = {

                                            selectedGame = game
                                            showResultModal = true

                                        },
                                        shape = RoundedCornerShape(14.dp)
                                    ) {

                                        Icon(
                                            Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )

                                        Spacer(Modifier.width(6.dp))

                                        Text("Result")

                                    }

                                }

                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    DashboardItem("Create Master", Icons.Default.PersonAdd) { showMaster = true }



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


    if(showWithdraw){
        WithdrawPendingModal(walletVM) {
            showWithdraw = false

            walletVM.loadProfile()
            walletVM.loadPending()
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
    if(
        showScheduleModal &&
        selectedGame != null
    ){

        AdminScheduleModal(

            vm = vm,

            game = selectedGame!!,

            onClose = {

                showScheduleModal = false

            }

        )
    }
    if (

        showRateModal &&

        selectedGame != null

    ) {

        AdminRateModal(

            vm = vm,

            game = selectedGame!!,

            onClose = {

                showRateModal = false

            }

        )

    }
    if (

        showResultModal &&

        selectedGame != null

    ){

        PublishResultDialog(

            vm = vm,

            game = selectedGame!!,

            onClose = {

                showResultModal = false

                vm.loadGameDashboard(context)

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



