package com.jminnovatech.sbclub.ui.screens.progame

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jminnovatech.sbclub.data.model.progame.Game
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.ProGameVM
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jminnovatech.sbclub.ui.screens.Transactions
import com.jminnovatech.sbclub.viewmodel.WalletVM
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.ui.screens.DepositScreen

import com.jminnovatech.sbclub.utils.SessionManager
import com.jminnovatech.sbclub.viewmodel.AuthVM

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    nav: NavController,
    context: Context,

    openDeposit: Boolean = false,

    sharedAmount: String = "",

    sharedUtr: String = "",

    vm: ProGameVM = remember { ProGameVM() }
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showTransactions by remember { mutableStateOf(false) }
    val walletVM = remember {

        WalletVM(

            AppRepository(context)

        )

    }
    var showLogoutDialog by remember {

        mutableStateOf(false)

    }
    var showFullMsg by remember { mutableStateOf(false) }
    val authVM: AuthVM = viewModel()
    var showChangePass by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showDeposit by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(sharedUtr) {

        if (sharedUtr.isNotBlank()) {

            showDeposit = true

        }

    }
    LaunchedEffect(Unit) {

        vm.loadGames(context)
        vm.loadWallet(context)

        authVM.loadMessage(context)

    }
    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet {

                Spacer(Modifier.height(20.dp))

                Text(

                    text = "SB CLUB",

                    modifier = Modifier.padding(20.dp),

                    style = MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.Bold

                )

                HorizontalDivider()

                NavigationDrawerItem(

                    label = { Text("🎯 Play Game") },

                    selected = false,

                    onClick = {

                        scope.launch {

                            drawerState.close()

                        }

                    }

                )

//                NavigationDrawerItem(
//
//                    label = { Text("📊 Summary") },
//
//                    selected = false,
//
//                    onClick = {
//
//                        scope.launch {
//
//                            drawerState.close()
//
//                        }
//
//                    }
//
//                )

//                NavigationDrawerItem(
//
//                    label = { Text("🏆 Bet History") },
//
//                    selected = false,
//
//                    onClick = {
//
//                        scope.launch {
//
//                            drawerState.close()
//
//                        }
//
//                    }
//
//                )
                NavigationDrawerItem(

                    label = { Text("💰 Deposit Money") },

                    selected = false,

                    onClick = {

                        showDeposit = true

                        scope.launch {
                            drawerState.close()
                        }

                    }

                )
                NavigationDrawerItem(

                    label = { Text("📜 Transactions") },

                    selected = false,

                    onClick = {

                        showTransactions = true

                        scope.launch {

                            drawerState.close()

                        }

                    }

                )

                NavigationDrawerItem(

                    label = { Text("💸 Withdrawal") },

                    selected = false,

                    onClick = {

                        nav.navigate("withdraw")

                    }

                )

                NavigationDrawerItem(

                    label = { Text("🔐 Change Password") },

                    selected = false,

                    onClick = {

                        showChangePass = true

                        scope.launch {

                            drawerState.close()

                        }

                    }
                )

                Spacer(Modifier.weight(1f))

                HorizontalDivider()

                NavigationDrawerItem(

                    label = {

                        Text(

                            "🚪 Logout",

                            color = Color.Red

                        )

                    },

                    selected = false,

                    onClick = {

                        showLogoutDialog = true

                        scope.launch {

                            drawerState.close()

                        }

                    }

                )

            }

        }

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B)
                        )
                    )
                )
                .padding(16.dp)
        ) {

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(

                    onClick = {

                        scope.launch {

                            drawerState.open()

                        }

                    }

                ) {

                    Icon(

                        Icons.Default.Menu,

                        contentDescription = null,

                        tint = Color.White

                    )

                }

                Spacer(Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "SB CLUB",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )





                }
                Card(

                    shape = RoundedCornerShape(50.dp),

                    colors = CardDefaults.cardColors(

                        containerColor = Color.White

                    )

                ) {

                    Row(

                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        ),

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color(0xFF2563EB)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(

                            text = "₹ %.2f".format(vm.walletBalance),

                            color = Color.Black,

                            fontWeight = FontWeight.Bold

                        )

                    }

                }
            }
            Spacer(Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showFullMsg = true
                    },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF6FF0D)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {





                    NewsTicker(
                        text = authVM.message,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Message,
                        contentDescription = null,
                        tint = Color.Blue
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "KOLKATA FATAFAT",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )


            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {

                Button(
                    onClick = {

                        vm.loadGames(context)
                        vm.loadWallet(context)

                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    ),
                    modifier = Modifier
                        .height(52.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Refresh",
                        fontWeight = FontWeight.Bold
                    )

                }

            }


            Spacer(Modifier.height(10.dp))

            when (val state = vm.gamesState) {

                ApiState.Idle -> {}

                ApiState.Loading -> {

                    Box(
                        Modifier.fillMaxSize(),
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

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(70.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                "No Internet Connection",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                state.message,
                                color = Color.LightGray
                            )

                            Spacer(Modifier.height(18.dp))

                            Button(
                                onClick = {

                                    vm.loadGames(context)
                                    vm.loadWallet(context)

                                }
                            ) {

                                Text("Retry")

                            }

                        }

                    }

                }

                is ApiState.Success -> {

                    LazyColumn {

                        items(state.data) { game ->

                            GameCard(game) {

                                nav.navigate("pro_game/${game.id}")

                            }

                            Spacer(Modifier.height(15.dp))

                        }

                    }

                }

            }

        }
    }
    if (showTransactions) {

        Dialog(

            onDismissRequest = {

                showTransactions = false

            }

        ) {

            Surface(

                modifier = Modifier.fillMaxSize(),

                color = Color(0xFFF5F5F5)

            ) {

                Column(

                    modifier = Modifier.fillMaxSize()

                ) {

                    Surface(

                        modifier = Modifier.fillMaxWidth(),

                        color = Color(0xFF2563EB),

                        shadowElevation = 8.dp

                    ) {

                        Row(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 12.dp
                                ),

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            Text(

                                text = "Transactions",

                                modifier = Modifier.weight(1f),

                                color = Color.White,

                                style = MaterialTheme.typography.titleLarge

                            )

                            IconButton(

                                onClick = {

                                    showTransactions = false

                                }

                            ) {

                                Icon(

                                    Icons.Default.Close,

                                    contentDescription = null,

                                    tint = Color.White

                                )

                            }

                        }

                    }

                    Box(

                        modifier = Modifier
                            .fillMaxSize()

                    ) {

                        Transactions(walletVM)

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
    if (showLogoutDialog) {

        AlertDialog(

            onDismissRequest = {

                showLogoutDialog = false

            },

            icon = {

                Text(
                    "🚪",
                    style = MaterialTheme.typography.headlineMedium
                )

            },

            title = {

                Text(
                    "Logout"
                )

            },

            text = {

                Text(
                    "Are you sure you want to logout?"
                )

            },

            confirmButton = {

                Button(

                    onClick = {

                        showLogoutDialog = false

                        val session = SessionManager(context)

                        session.clear()

                        nav.navigate("login") {

                            popUpTo(0) {

                                inclusive = true

                            }

                        }

                    }

                ) {

                    Text("Logout")

                }

            },

            dismissButton = {

                OutlinedButton(

                    onClick = {

                        showLogoutDialog = false

                    }

                ) {

                    Text("Cancel")

                }

            }

        )

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
    if (showDeposit) {

        Dialog(
            onDismissRequest = {
                showDeposit = false
            }
        ) {

            Surface(

                modifier = Modifier.fillMaxSize(),

                color = Color(0xFFF4F6FA)

            ) {

                DepositScreen(

                    context = context,

                    amount = sharedAmount,

                    transactionId = sharedUtr,

                    onClose = {

                        showDeposit = false

                    }

                )

            }

        }

    }
}
@Composable
fun DiceIcon(dots: Int) {

    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                Color.White,
                RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize().padding(5.dp)
        ) {

            val r = size.minDimension / 10f

            val left = Offset(size.width * .25f, size.height * .25f)
            val center = Offset(size.width * .5f, size.height * .5f)
            val right = Offset(size.width * .75f, size.height * .75f)

            val topRight = Offset(size.width * .75f, size.height * .25f)
            val bottomLeft = Offset(size.width * .25f, size.height * .75f)
            val topLeft = Offset(size.width * .25f, size.height * .25f)
            val bottomRight = Offset(size.width * .75f, size.height * .75f)
            val middleLeft = Offset(size.width * .25f, size.height * .5f)
            val middleRight = Offset(size.width * .75f, size.height * .5f)

            fun dot(p: Offset) {
                drawCircle(
                    color = Color(0xFF2563EB),
                    radius = r,
                    center = p
                )
            }

            when (dots) {

                1 -> {
                    dot(center)
                }

                2 -> {
                    dot(topRight)
                    dot(bottomLeft)
                }

                3 -> {
                    dot(topRight)
                    dot(center)
                    dot(bottomLeft)
                }

                5 -> {
                    dot(topLeft)
                    dot(topRight)
                    dot(center)
                    dot(bottomLeft)
                    dot(bottomRight)
                }
            }
        }
    }
}
@Composable
fun GameCard(

    game: Game,

    onClick:()->Unit

){
    val diceIcon = when (game.digit_length) {
        1 -> "\u2680"   // ⚀
        2 -> "\u2681"   // ⚁
        3 -> "\u2682"   // ⚂
        4 -> "\u2683"   // ⚃
        5 -> "\u2684"   // ⚄
        6 -> "\u2685"   // ⚅
        else -> "\u2680"
    }
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF2563EB)

        )

    ){

        Row(

            modifier = Modifier.fillMaxSize(),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.Center

        ){

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

//                Text(
//                    text = diceIcon,
//                    fontSize = 28.sp,
//                    color = Color.White
//                )
                DiceIcon(game.digit_length)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = game.game_name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(10.dp))

//            Text(
//
//                game.game_name,
//
//                color = Color.White,
//
//                fontSize = 20.sp,
//
//                fontWeight = FontWeight.Bold
//
//            )

        }

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