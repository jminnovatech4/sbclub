package com.jminnovatech.sbclub.ui.screens.user

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jminnovatech.sbclub.data.model.user.BetItem.BetItem
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.GameVM
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import com.jminnovatech.sbclub.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetScreen2(nav: NavController, context1: Context) {
    fun getTodayTimeMillis(time: String): Long {
        val parts = time.split(":")
        val cal = Calendar.getInstance()

        cal.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
        cal.set(Calendar.MINUTE, parts[1].toInt())
        cal.set(Calendar.SECOND, 0)

        return cal.timeInMillis
    }
    val endSchedule = listOf(
        "10:20","11:30","12:40","13:50",
        "15:00","16:10","17:20","18:30",
        "19:40","20:50"
    )

    val resultSchedule = listOf(
        "10:30","11:40","12:50","14:00",
        "15:10","16:20","17:30","18:40",
        "19:50","21:00"
    )
    val vm: GameVM = viewModel()
    val context = LocalContext.current
    LaunchedEffect(vm.betState) {
        if (vm.betState is ApiState.Success) {
            vm.loadCurrentRound(context)
            vm.loadCurrentBets(context)
            vm.clearBetState()
        }
    }
    val numbers = (0..9).toList()

    // ✅ PER NUMBER AMOUNT
    var amounts by remember { mutableStateOf(List(10) { "" }) }

    // ✅ MODAL STATE
    var showDialog by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1) }
    var tempAmount by remember { mutableStateOf("") }

    var walletBalance by remember { mutableStateOf(0) }
    var originalBalance by remember { mutableStateOf(0) }
    var endTime by remember { mutableStateOf(0L) }
    var betOpen by remember { mutableStateOf(true) }
    val isTimeOver = System.currentTimeMillis() > endTime
    val finalBetOpen = betOpen && !isTimeOver
    val isLocked = !finalBetOpen
    var lastHit by remember { mutableStateOf("") }
    var showResultPopup by remember { mutableStateOf(false) }
    var resultNumber by remember { mutableStateOf("") }
    var isWin by remember { mutableStateOf(false) }
    var showResultModal by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // 🔥 PATTI MOCK STATE
    var showPattiDialog by remember { mutableStateOf(false) }
    var pattiNumber by remember { mutableStateOf("") }
    var pattiAmount by remember { mutableStateOf("") }
    var pattiBet by remember { mutableStateOf<Pair<String, Int>?>(null) }
    fun refreshAll(vm: GameVM, context: Context) {
        vm.loadCurrentRound(context)
        vm.loadCurrentBets(context)
    }
//    LaunchedEffect(Unit) {
//
//        vm.loadCurrentRound(context)
//
//        vm.loadCurrentBets(context) // 🔥 ADD THIS
//
//
//    }

    LaunchedEffect(vm.currentBetsState) {

        val state = vm.currentBetsState

        if (state is ApiState.Success) {

            val newList = MutableList(10) { "" }

            state.data.forEach {

                val numStr = it.number

                // 🔢 NORMAL (1 digit)
                if (numStr.length == 1) {
                    val index = numStr.toIntOrNull()
                    if (index != null && index in 0..9) {
                        newList[index] = it.amount.toString()
                    }
                }
            }

            // 🎴 Patti detect
            val patti = state.data.find { it.number.length == 3 }

            if (patti != null) {
                pattiBet = patti.number to patti.amount
            }

            amounts = newList
        }
    }
    LaunchedEffect(vm.currentRoundState) {

        val state = vm.currentRoundState

        if (state is ApiState.Success) {

            val roundId = state.data.id

            if (lastHit != "round_$roundId") {
                lastHit = "round_$roundId"

                // ✅ CLEAR OLD BETS
                amounts = List(10) { "" }

                // 🔥 LOAD NEW ROUND BETS
                vm.loadCurrentBets(context)
            }
        }
    }
    LaunchedEffect(Unit) {

        vm.loadCurrentRound(context)
        vm.loadCurrentBets(context)
        // existing code...

        // 🔥 AUTO SCHEDULE LOOP
        while (true) {

            val now = System.currentTimeMillis()

            // 🔒 BET CLOSE TIME
            endSchedule.forEach { time ->

                val t = getTodayTimeMillis(time)

                if (now in t..(t + 60000)) {

                    if (lastHit != "end_$time") {
                        lastHit = "end_$time"
                        amounts = List(10) { "" }
                        // 🔥 reload round
                        vm.loadCurrentRound(context)
                    }
                }
            }

            // 🎯 RESULT TIME
            resultSchedule.forEach { time ->

                val t = getTodayTimeMillis(time)

                if (now in t..(t + 60000)) {

                    if (lastHit != "result_$time") {
                        lastHit = "result_$time"

                        vm.loadCurrentRound(context)

                        // 🔥 ADD THIS
                        val result = vm.currentRoundState
                        if (result is ApiState.Success) {

                            val resNum = result.data.result_number ?: ""
                            resultNumber = resNum

                            val userBet = amounts[resNum.toIntOrNull() ?: -1].toIntOrNull() ?: 0
                            isWin = userBet > 0

                            showResultPopup = true
                        }
                    }
                }
            }

            delay(5000) // 🔥 better than 1 sec (battery save)
        }
    }
    LaunchedEffect(
        try {
            vm.currentRoundState
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    ) {

        val state = vm.currentRoundState

        if (state is ApiState.Success) {

            val res = state.data.result_number

            // 🔥 already result আছে কিনা
            if (!res.isNullOrEmpty() && !showResultPopup) {

                resultNumber = res

                val userBet = amounts[res.toIntOrNull() ?: -1]
                    .toIntOrNull() ?: 0

                isWin = userBet > 0

                showResultPopup = true
            }
        }
    }

    LaunchedEffect(showResultPopup) {
        if (showResultPopup && isWin) {

            try {
                val mp = MediaPlayer.create(context, R.raw.win)

                mp?.setOnCompletionListener { player ->
                    player.release()
                }

                mp?.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // 🎨 CLEAN DARK UI (NO COLOR CLASH)
    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            )
            .padding(4.dp)
    ) {

        // 🔥 ROUND INFO (OLD + CLEAN)
        when (val state = vm.currentRoundState) {

            is ApiState.Success -> {

                val round = state.data

                walletBalance = round.wallet_balance?.toDoubleOrNull()?.toInt() ?: 0
                originalBalance = walletBalance
                endTime = round.end_time_millis
                betOpen = round.bet_open
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0B1220)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {

                    // 🔥 GLOW ANIMATION
                    val infinite = rememberInfiniteTransition()
                    val glow by infinite.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF1E293B),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        //  BALANCE SECTION
                        Column {

                            Text(
                                "Available Balance",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                "🪙$walletBalance",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFF22C55E).copy(alpha = glow) // 🔥 glow
                            )
                        }

                        // ⏳ TIMER SECTION
                        Column(horizontalAlignment = Alignment.End) {

                            Text(
                                if (betOpen) "🟢 LIVE" else "\uD83E\uDD16 ROBOT ON \uD83D\uDFE2",
                                color = if (betOpen) Color.Green else Color.Green,
                                style = MaterialTheme.typography.bodySmall
                            )

                            CountdownTimer(endTime)
                        }
                    }
                }

            }

            is ApiState.Loading -> CircularProgressIndicator()
            is ApiState.Error -> Text(state.message, color = Color.Red)
            else -> {}
        }
        LaunchedEffect(finalBetOpen) {
            if (!finalBetOpen) {
                amounts = List(10) { "" }
            }
        }
        Spacer(Modifier.height(2.dp))

        // 🔢 GRID (NEW PROFESSIONAL UI)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),

        ) {

            // 🔢 EXISTING NUMBERS (UNCHANGED)
            items(numbers.size) { index ->

                val num = numbers[index]
                val amt = amounts[index].toIntOrNull() ?: 0
                val win = amt * 9
                val isSelected = amt > 0

                val bgColor = when {
                    isSelected -> Color(0xFF2563EB)
                    else -> Color(0xFF1E293B)
                }

                Card(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                        .alpha(if (isLocked) 0.6f else 1f)
                        .clickable(enabled = !isLocked) {
                            selectedIndex = index
                            tempAmount = amounts[index]
                            showDialog = true
                        },
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Box {

                        // 🔢 ORIGINAL CONTENT (UNCHANGED)
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(num.toString(), color = Color.White, fontWeight = FontWeight.Bold)

                            Spacer(Modifier.height(4.dp))

                            if (amt > 0) {
                                Text("🪙$amt", color = Color.White)
                                Text("Win: $win", color = Color(0xFF22C55E))
                            } else {
                                Text("Tap", color = Color.Gray)
                            }
                        }

                        // 🔒 LOCK ICON
//                        if (isLocked) {
//                            Text(
//                                "🔒",
//                                modifier = Modifier
//                                    .align(Alignment.TopEnd)
//                                    .padding(6.dp)
//                            )
//                        }

                        // 🚫 OVERLAY (PREMIUM)
                        if (isLocked) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "🔒",
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 🎴 PATTI CARD (NEW – NO IMPACT ON EXISTING)
            item(
                span = { GridItemSpan(2) } // 🔥 takes 2 slots
            ) {

                val amt = pattiBet?.second ?: 0
                val num = pattiBet?.first ?: "---"
                val win = amt * 120

                Card(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                        .alpha(if (isLocked) 0.6f else 1f)
                        .clickable(enabled = !isLocked) {
                            showPattiDialog = true
                        },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF7C3AED)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF312E81)
                    )
                ) {

                    Box {

                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text("🎴 PATTI", color = Color.White, fontWeight = FontWeight.Bold)

                            Spacer(Modifier.height(6.dp))

                            Text(
                                if (pattiBet == null) "Tap" else num,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(6.dp))

                            if (amt > 0) {
                                Text("🪙$amt", color = Color.White)
                                Text("Win: $win", color = Color(0xFF22C55E))
                            } else {
                                Text("Add", color = Color.White.copy(alpha = 0.7f))
                            }
                        }

                        // 🔒 LOCK
//                        if (isLocked) {
//                            Text(
//                                "🔒",
//                                modifier = Modifier
//                                    .align(Alignment.TopEnd)
//                                    .padding(6.dp)
//                            )
//                        }

                        // 🚫 OVERLAY
                        if (isLocked) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {Text(
                                "🔒",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                            )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(5.dp))

        // 🔥 CALCULATION
        val total = amounts.sumOf { it.toIntOrNull() ?: 0 }
        val remaining = (originalBalance ).coerceAtLeast(0)
        val isLowBalance = total > originalBalance
// 🧾 CURRENT BET CARD
        val activeBets = amounts.mapIndexedNotNull { i, v ->
            val a = v.toIntOrNull()
            if (a != null && a > 0) "$i- 🪙$a" else null
        }
//  SUMMARY CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E3A8A)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 🔥 TITLE
// 🔥 PREMIUM LIVE HEADER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(
                            Color(0xFF334155),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                ) {

                    // 🔴 BLINKING DOT
                    val infinite = rememberInfiniteTransition()
                    val alpha by infinite.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                Color.Green.copy(alpha = alpha),
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Spacer(Modifier.width(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text( "LIVE", color = Color.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold )
                        // LEFT SIDE (Title)
                        Spacer(Modifier.width(3.dp))
                        Text(
                            "Current Bet",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f)) // 🔥 pushes right

                        // RIGHT SIDE (Total)
                        val total = when (val state = vm.currentBetsState) {
                            is ApiState.Success -> state.data.sumOf { it.amount }
                            else -> 0
                        }

                        Text(
                            "Total 🪙$total",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                }

                Spacer(Modifier.height(8.dp))

                // 🔥 BET CHIPS
                when (val state = vm.currentBetsState) {

                    is ApiState.Success -> {

                        val bets = state.data

                        if (bets.isNotEmpty()) {

                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                bets.forEach { bet ->

                                    val num = bet.number.toIntOrNull()

                                    val chipColor = when {
                                        num == null -> Color(0xFF7C3AED) // 🎴 Patti color
                                        num % 5 == 0 -> Color(0xFF22C55E)
                                        num % 5 == 1 -> Color(0xFF3B82F6)
                                        num % 5 == 2 -> Color(0xFFF97316)
                                        num % 5 == 3 -> Color(0xFF06B6D4)
                                        else -> Color(0xFFA855F7)
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(
                                                chipColor.copy(alpha = 0.2f),
                                                RoundedCornerShape(20.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    chipColor,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(bet.number, color = Color.White, fontSize = 12.sp)
                                        }

                                        Spacer(Modifier.width(4.dp))

                                        Text(
                                            "${bet.amount}",
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                        } else {
                            Text("No Bet", color = Color.Gray)
                        }
                    }

                    else -> Text("Loading...", color = Color.Gray)
                }




            }
        }

        Spacer(Modifier.height(10.dp))





        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔄 RELOAD
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFF8A65), // light orange
                                Color(0xFFFF7043)  // deep orange
                            )
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        refreshAll(vm, context) // 🔥 FULL REFRESH
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        "⟳",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        "Reload",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            // 🟢 LIVE RESULT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF66BB6A), // light green
                                Color(0xFF43A047)  // deep green
                            )
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        vm.loadResults(context) // 🔥 now using results
                        showResultModal = true
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        "📊",
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        "Live Result",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        // 🔥 RESPONSE
        when (val state = vm.betState) {

            is ApiState.Loading -> CircularProgressIndicator()

            is ApiState.Success -> {
                Text("✅ ${state.data}", color = Color.Green)
            }

            is ApiState.Error -> {
                Text("❌ ${state.message}", color = Color.Red)
            }

            else -> {}
        }
    }

    // 🔥 MODAL INPUT (BEST UX)
    if (showResultPopup) {

        AlertDialog(
            onDismissRequest = { showResultPopup = false },

            title = {
                Text("🎯 Result Declared")
            },

            text = {
                Column {

                    Text("Winning Number: $resultNumber")

                    Spacer(Modifier.height(10.dp))

                    Text(
                        if (isWin) "🎉 You Won!" else "😔 You Lost",
                        color = if (isWin) Color.Green else Color.Red
                    )
                }
            },

            confirmButton = {
                Button(onClick = { showResultPopup = false }) {
                    Text("OK")
                }
            }
        )
    }
    if (showDialog && finalBetOpen) {

        val amt = tempAmount.toIntOrNull() ?: 0
        val win = amt * 9

        AlertDialog(
            onDismissRequest = { showDialog = false },

            title = { Text("Enter Amount") },

            text = {
                Column {

                    OutlinedTextField(
                        value = tempAmount,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() }) {
                                tempAmount = it
                            }
                        },
                        placeholder = { Text("Amount") },
                        singleLine = true
                    )

                    Spacer(Modifier.height(10.dp))

                    if (amt > 0) {
                        Text(
                            " You Win: 🪙$win",
                            color = Color(0xFF22C55E),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            },

            confirmButton = {
                Button(onClick = {

                    val enteredAmount = tempAmount.toIntOrNull() ?: 0
                    val previousAmount = amounts[selectedIndex].toIntOrNull() ?: 0

                    val extraNeeded = enteredAmount - previousAmount

                    println("==== BET DEBUG ====")
                    println("walletBalance = $walletBalance")
                    println("previousAmount = $previousAmount")
                    println("enteredAmount = $enteredAmount")
                    println("extraNeeded = $extraNeeded")

                    if (extraNeeded > walletBalance) {
                        Toast.makeText(context, "Insufficient Balance", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // ✅ update UI
                    val newList = amounts.toMutableList()
                    newList[selectedIndex] = tempAmount
                    amounts = newList

                    // ✅ API call
                    val bets = listOf(
                        BetItem(selectedIndex.toString(), enteredAmount)
                    )

                    vm.placeBet(context, bets)

                    showDialog = false

                }) {
                    Text("Save")
                }
            },

            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    if (showResultModal) {

        ModalBottomSheet(
            onDismissRequest = { showResultModal = false },
            containerColor = Color(0xFF0F172A)
        ) {

            Column(Modifier.padding(12.dp)) {

                Text(
                    "📊 Result History",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(10.dp))

                when (val state = vm.resultState) {

                    is ApiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is ApiState.Success -> {

                        LazyColumn {

                            items(state.data.data) { item ->   // ✅ correct

                                val num = item.result.toIntOrNull() ?: 0   // ✅ FIX

                                val bgColor = when (num % 5) {
                                    0 -> Color(0xFF22C55E)
                                    1 -> Color(0xFF3B82F6)
                                    2 -> Color(0xFFF97316)
                                    3 -> Color(0xFF06B6D4)
                                    else -> Color(0xFFA855F7)
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF111827)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {

                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Column {

                                            Text(
                                                item.result_time,   // ✅ FIX
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }

                                        // 🎴 PATTI
                                        Box(
                                            modifier = Modifier
                                                .background(bgColor, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                        ) {
                                            Text(
                                                item.patti_result ?: "--",   // ✅ FIX
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text("/", color = Color.White)

                                        // 🎯 RESULT
                                        Box(
                                            modifier = Modifier
                                                .background(bgColor, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                        ) {
                                            Text(
                                                item.result,   // ✅ FIX
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
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
    }

    if (showPattiDialog && !isLocked) {

        val amt = pattiAmount.toIntOrNull() ?: 0
        val win = amt * 120

        AlertDialog(
            onDismissRequest = { showPattiDialog = false },

            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🎴 Patti Bet",
                        fontWeight = FontWeight.Bold
                    )

                    // ❌ CLOSE BUTTON
                    Text(
                        "✖",
                        modifier = Modifier
                            .clickable { showPattiDialog = false },
                        color = Color.Red,
                        fontSize = 18.sp
                    )
                }
            },

            text = {
                Column {

                    // 🔢 NUMBER FIELD (PREMIUM STYLE)
                    Text(
                        "Enter Number",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = pattiNumber,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            if (it.length <= 3 && it.all { ch -> ch.isDigit() }) {
                                pattiNumber = it
                            }
                        },
                        supportingText = {
                            Text("৩ সংখ্যার Patti নম্বর দিন", fontSize = 11.sp)
                        },
                        placeholder = { Text("100 - 999") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    //  AMOUNT FIELD
                    Text(
                        "Enter Amount",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = pattiAmount,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() }) {
                                pattiAmount = it
                            }
                        },
                        placeholder = { Text("Amount") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    // 🎯 WIN PREVIEW (ATTRACTIVE)
                    if (amt > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFF1E293B),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "🎯 Win: 🪙$win",
                                color = Color(0xFF22C55E),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },

            confirmButton = {
                Button(
                    onClick = {

                        // ❌ MUST BE EXACT 3 DIGIT
                        if (pattiNumber.length != 3) {
                            Toast.makeText(
                                context,
                                "Patti নম্বর অবশ্যই ৩ সংখ্যার হতে হবে",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val amount = pattiAmount.toIntOrNull() ?: 0

                        if (amount <= 0) {
                            Toast.makeText(
                                context,
                                "সঠিক পরিমাণ দিন",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        // 🔥 DUPLICATE CHECK
                        val alreadyExists = vm.currentBetsState.let { state ->
                            if (state is ApiState.Success) {
                                state.data.any { it.number == pattiNumber }
                            } else false
                        }

                        if (alreadyExists) {
                            Toast.makeText(
                                context,
                                "এই নম্বরে ইতিমধ্যে বেট করা হয়েছে",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        showConfirmDialog = true

                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Proceed")
                }
            },

            dismissButton = {
                TextButton(onClick = { showPattiDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    if (showConfirmDialog) {

        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },

            title = {
                Text("Confirm Bet")
            },

            text = {
                Column {
                    Text("Number: $pattiNumber")
                    Text("Amount: 🪙$pattiAmount")

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Are you sure?",
                        color = Color.Gray
                    )
                }
            },

            confirmButton = {
                Button(onClick = {

                    val amount = pattiAmount.toIntOrNull() ?: 0

                    pattiBet = pattiNumber to amount

                    vm.placeBet(
                        context,
                        listOf(BetItem(pattiNumber, amount)),
                        gameType = "2no"
                    )

                    showConfirmDialog = false
                    showPattiDialog = false

                }) {
                    Text("Confirm")
                }
            },

            dismissButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }

}

// ⏳ TIMER
@Composable
fun CountdownTimer2(endTime: Long) {

    var timeLeft by remember { mutableStateOf(0L) }
    var blink by remember { mutableStateOf(true) }

    // ✅ CORRECT animation
    val infinite = rememberInfiniteTransition()

    val scale by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(endTime) {
        while (true) {
            timeLeft = endTime - System.currentTimeMillis()
            delay(1000)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            blink = !blink
            delay(500)
        }
    }

    if (timeLeft > 0) {

        val totalSec = timeLeft / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60

        val isLastMinute = totalSec <= 60

        val text = String.format("%02d:%02d:%02d", h, m, s)

        Text(
            text = text,
            color = if (isLastMinute && blink) Color.Red else Color.Green,
            modifier = Modifier.scale(scale)
        )
    }
}