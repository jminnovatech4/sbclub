package com.jminnovatech.sbclub.ui.screens.user.progame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.progame.ProBetItem
import com.jminnovatech.sbclub.data.model.progame.Schedule
import com.jminnovatech.sbclub.ui.screens.user.progame.GameTimeUtils.formatTime
import com.jminnovatech.sbclub.ui.screens.user.progame.GameTimeUtils.formatToAmPm
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
@Composable
fun ScheduleCard(

    gameId: Int,

    schedule: Schedule,
    digitLength: Int,
    currentId: Int,

    wallet: Double,

    resultNumber: String? = null,

    onPlaceBet: (
        scheduleId: Int,
        bets: List<ProBetItem>
    ) -> Unit,

    onHistoryClick: (Int) -> Unit = {}

) {

    var expanded by remember {

        mutableStateOf(false)

    }

    val betList = remember {

        mutableStateListOf<ProBetItem>()

    }

    val state = when {

        GameTimeUtils.isRunning(
            schedule.start_time,
            schedule.end_time
        ) -> "RUNNING"

        GameTimeUtils.isLocked(
            schedule.start_time
        ) -> "LOCK"

        else -> "RESULT"

    }

    val chipColor = when (state) {

        "RUNNING" -> Color(0xFF16A34A)

        "RESULT" -> Color(0xFF2563EB)

        else -> Color(0xFFF59E0B)

    }

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(26.dp)
            ),

        shape = RoundedCornerShape(26.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color.Transparent

        )

    ){

        Column(

            modifier = Modifier

                .background(

                    Brush.verticalGradient(

                        listOf(

                            Color(0xFF1E293B),

                            Color(0xFF0F172A)

                        )

                    )

                )

                .padding(18.dp)

        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically

            ) {

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically

                ){

                    Column(

                        modifier = Modifier.weight(1f)

                    ){

                        Text(

                            text = "BAJI ${schedule.baji_no}",

                            color = Color.White,

                            fontWeight = FontWeight.Black

                        )

                        Spacer(Modifier.height(4.dp))

                        Text(

                            text =

                                "${formatTime(schedule.start_time)} - ${formatTime(schedule.end_time)}",

                            color = Color(0xFFCBD5E1)

                        )

                    }

                    Surface(

                        color = chipColor,

                        shape = RoundedCornerShape(50)

                    ){

                        Row(

                            modifier = Modifier.padding(

                                horizontal = 14.dp,

                                vertical = 8.dp

                            ),

                            verticalAlignment = Alignment.CenterVertically

                        ){

                            Icon(

                                when(state){

                                    "RUNNING"->Icons.Default.PlayArrow

                                    "LOCK"->Icons.Default.Lock

                                    else->Icons.Default.CheckCircle

                                },

                                null,

                                tint = Color.White

                            )

                            Spacer(Modifier.width(6.dp))

                            Text(

                                state,

                                color = Color.White

                            )

                        }

                    }

                }
            }

            Spacer(

                Modifier.height(12.dp)

            )

            Card(

                colors = CardDefaults.cardColors(

                    containerColor = Color(0xFF111827)

                )

            ){

                Row(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(14.dp),

                    horizontalArrangement = Arrangement.SpaceBetween

                ){

                    Text(

                        "Winning Number",

                        color = Color.Gray

                    )

                    Text(

                        resultNumber ?: "---",

                        color = Color(0xFF22C55E),

                        fontWeight = FontWeight.Bold

                    )

                }

            }

            Spacer(

                Modifier.height(15.dp)

            )

            if (state == "RUNNING") {

                TimerView(

                    endTime =

                        formatToAmPm(schedule.end_time)

                )

                Spacer(

                    Modifier.height(15.dp)

                )

            }
            Card(

                colors = CardDefaults.cardColors(

                    containerColor = Color(0xFF111827)

                ),

                shape = RoundedCornerShape(20.dp)

            ){

                Row(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(16.dp),

                    horizontalArrangement = Arrangement.SpaceEvenly

                ){

                    WalletBox(

                        "Wallet",

                        "₹%.0f".format(wallet),

                        Color(0xFF22C55E)

                    )

                    WalletBox(

                        "Bet",

                        "₹%.0f".format(betList.sumOf{it.amount}),

                        Color(0xFFF59E0B)

                    )

                    WalletBox(

                        "Left",

                        "₹%.0f".format(wallet-betList.sumOf{it.amount}),

                        Color.White

                    )

                }

            }
            Button(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                enabled = state != "LOCK",

                shape = RoundedCornerShape(18.dp),

                colors = ButtonDefaults.buttonColors(

                    containerColor = when (state) {

                        "RUNNING" -> Color(0xFF2563EB)

                        "RESULT" -> Color(0xFF0EA5E9)

                        else -> Color(0xFF475569)

                    },

                    disabledContainerColor = Color(0xFF475569),

                    contentColor = Color.White,

                    disabledContentColor = Color.White

                ),

                elevation = ButtonDefaults.buttonElevation(

                    defaultElevation = 8.dp,

                    pressedElevation = 2.dp

                ),

                onClick = {

                    if (state != "LOCK") {

                        expanded = !expanded

                    }

                }

            ) {

                Icon(

                    imageVector = when (state) {

                        "RUNNING" -> Icons.Default.PlayArrow

                        "RESULT" -> if (expanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown

                        else -> Icons.Default.Lock

                    },

                    contentDescription = null

                )

                Spacer(Modifier.width(10.dp))

                Text(

                    text = when (state) {

                        "RUNNING" ->

                            if (expanded)
                                "CLOSE BET PANEL"
                            else
                                "OPEN BET PANEL"

                        "RESULT" ->

                            if (expanded)
                                "HIDE RESULT HISTORY"
                            else
                                "VIEW RESULT HISTORY"

                        else -> "GAME LOCKED"

                    },

                    style = MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold

                )

            }
            AnimatedVisibility(

                visible = expanded,

                enter = fadeIn() + expandVertically(),

                exit = fadeOut() + shrinkVertically()

            ) {
                if (state == "RUNNING") {

                    Spacer(Modifier.height(20.dp))

                    //==============================
                    // WALLET SUMMARY
                    //==============================

                    Card(

                        modifier = Modifier.fillMaxWidth(),

                        colors = CardDefaults.cardColors(

                            containerColor = Color(0xFF111827)

                        ),

                        shape = RoundedCornerShape(20.dp)

                    ) {

                        Row(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                            horizontalArrangement = Arrangement.SpaceEvenly,

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            WalletBox(

                                title = "Wallet",

                                value = "₹ %.2f".format(wallet),

                                color = Color(0xFF22C55E)

                            )

                            WalletBox(

                                title = "Bet",

                                value = "₹ %.2f".format(

                                    betList.sumOf {

                                        it.amount

                                    }

                                ),

                                color = Color(0xFFF59E0B)

                            )

                            WalletBox(

                                title = "Left",

                                value = "₹ %.2f".format(

                                    wallet -

                                            betList.sumOf {

                                                it.amount

                                            }

                                ),

                                color = Color.White

                            )

                        }

                    }

                    Spacer(Modifier.height(18.dp))

                    //==============================
                    // ADD BET TITLE
                    //==============================

                    Text(

                        text = "Add New Bet",

                        style = MaterialTheme.typography.titleMedium,

                        fontWeight = FontWeight.Bold,

                        color = Color.White

                    )

                    Spacer(Modifier.height(12.dp))

                    //==============================
                    // BET ENTRY
                    //==============================


                    //========================================
// BETTING PANEL
//========================================

                    var showBetDialog by remember {

                        mutableStateOf(false)

                    }

                    Card(

                        modifier = Modifier.fillMaxWidth(),

                        colors = CardDefaults.cardColors(

                            containerColor = Color(0xFF111827)

                        ),

                        shape = RoundedCornerShape(22.dp)

                    ){

                        Column(

                            modifier = Modifier.padding(18.dp)

                        ){

                            Text(

                                text = "My Bets",

                                style = MaterialTheme.typography.titleMedium,

                                color = Color.White,

                                fontWeight = FontWeight.Bold

                            )

                            Spacer(Modifier.height(14.dp))

                            if(betList.isEmpty()){

                                Text(

                                    "No Bet Added",

                                    color = Color.Gray

                                )

                            }else{

                                betList.forEachIndexed{ index,bet->

                                    BetRow(

                                        bet = bet,

                                        onDelete ={

                                            betList.removeAt(index)

                                        }

                                    )

                                }

                            }

                            Spacer(Modifier.height(18.dp))

                            FilledTonalButton(

                                modifier = Modifier.fillMaxWidth(),

                                onClick = {

                                    showBetDialog=true

                                }

                            ){

                                Text("ADD BET")

                            }

                            Spacer(Modifier.height(15.dp))

                            val total=

                                betList.sumOf{

                                    it.amount

                                }

                            Button(

                                modifier = Modifier

                                    .fillMaxWidth()

                                    .height(56.dp),

                                enabled =

                                    betList.isNotEmpty()

                                            &&

                                            total<=wallet,

                                onClick ={

                                    onPlaceBet(

                                        schedule.id,

                                        betList.toList()

                                    )

                                    betList.clear()

                                    expanded=false

                                }

                            ){

                                Text(

                                    "PLACE BET  ₹ %.2f".format(total)

                                )

                            }

                        }

                    }
                    BetEntryDialog(

                        show = showBetDialog,

                        digitLength = 1,

                        wallet = wallet,

                        onDismiss = {

                            showBetDialog = false

                        },

                        onAdd = { number, amount ->

                            val exists = betList.any {

                                it.number == number

                            }

                            if (!exists) {

                                betList.add(

                                    ProBetItem(

                                        number = number,

                                        amount = amount

                                    )

                                )

                            }

                        }

                    )
                    Spacer(Modifier.height(20.dp))

                    //==============================
                    // BET LIST
                    //==============================

                    BetListCard(

                        bets = betList,

                        onDelete = {

                            if (it in betList.indices) {

                                betList.removeAt(it)

                            }

                        },

                        onPlaceBet = {

                            if (betList.isEmpty()) {

                                return@BetListCard

                            }

                            val total =

                                betList.sumOf {

                                    it.amount

                                }

                            if (total > wallet) {

                                return@BetListCard

                            }

                            onPlaceBet(

                                schedule.id,

                                betList.toList()

                            )

                            betList.clear()

                            expanded = false

                        }

                    )

                } else {

                    Spacer(Modifier.height(18.dp))

                    ResultHistoryCard(

                        scheduleId = schedule.id,

                        resultNumber = resultNumber,

                        onRefresh = {

                            onHistoryClick(

                                schedule.id

                            )

                        }

                    )

                }
            }

        }

    }

}

@Composable
private fun WalletBox(

    title:String,

    value:String,

    color:Color

){

    Column(

        horizontalAlignment = Alignment.CenterHorizontally

    ){

        Text(

            title,

            color = Color.Gray

        )

        Spacer(Modifier.height(5.dp))

        Text(

            value,

            color = color,

            style = MaterialTheme.typography.titleMedium,

            fontWeight = FontWeight.Bold

        )

    }

}

@Composable
private fun SummaryItem(

    title: String,

    value: String,

    color: Color

) {

    Column(

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Text(

            text = title,

            color = Color.Gray,

            style = MaterialTheme.typography.labelMedium

        )

        Spacer(Modifier.height(5.dp))

        Text(

            text = value,

            color = color,

            fontWeight = FontWeight.Bold,

            style = MaterialTheme.typography.titleMedium

        )

    }

}

@Composable
private fun BetRow(

    bet:ProBetItem,

    onDelete:()->Unit

){

    Card(

        modifier = Modifier

            .fillMaxWidth()

            .padding(bottom=10.dp),

        colors = CardDefaults.cardColors(

            containerColor=Color(0xFF1E293B)

        )

    ){

        Row(

            modifier=Modifier

                .fillMaxWidth()

                .padding(14.dp),

            verticalAlignment=Alignment.CenterVertically

        ){

            Column(

                modifier=Modifier.weight(1f)

            ){

                Text(

                    bet.number,

                    color=Color.White,

                    fontWeight=FontWeight.Bold

                )

                Text(

                    "₹ %.2f".format(bet.amount),

                    color=Color(0xFF22C55E)

                )

            }

            TextButton(

                onClick=onDelete

            ){

                Text(

                    "DELETE",

                    color=Color.Red

                )

            }

        }

    }

}