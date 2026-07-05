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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.*
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp

@Composable
fun ScheduleCard(

    gameId: Int,

    schedule: Schedule,

    currentId: Int,
    gameCode:String,
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


    var number by remember {

        mutableStateOf("")

    }

    var amount by remember {

        mutableStateOf("")

    }

    var error by remember {

        mutableStateOf("")

    }

    var loading by remember {

        mutableStateOf(false)

    }

    var success by remember {

        mutableStateOf(false)

    }

    val rotation by animateFloatAsState(

        targetValue = if (expanded) 180f else 0f,

        label = "rotation"

    )

    val borderColor by animateColorAsState(

        targetValue =

            if (expanded)

                Color(0xFF2563EB)

            else

                Color(0xFF334155),

        label = "border"

    )
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

        border = BorderStroke(

            1.dp,

            borderColor

        ),

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

                Modifier.height(5.dp)

            )

            if (state == "RUNNING") {

                TimerView(

                    endTime =

                        formatToAmPm(schedule.end_time)

                )

                Spacer(

                    Modifier.height(5.dp)

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
            Surface(

                modifier = Modifier

                    .fillMaxWidth()

                    .clip(RoundedCornerShape(18.dp))

                    .clickable(

                        interactionSource = remember {

                            MutableInteractionSource()

                        },

                        indication = null

                    ){

                        if(state!="LOCK"){

                            expanded=!expanded

                        }

                    },

                color = Color(0xFF2563EB)

            ){

                Row(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(

                            vertical = 18.dp

                        ),

                    horizontalArrangement = Arrangement.Center,

                    verticalAlignment = Alignment.CenterVertically

                ){

                    Text(

                        if(expanded)

                            "CLOSE BET PANEL"

                        else

                            "OPEN BET PANEL",

                        color = Color.White,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(

                        Modifier.width(8.dp)

                    )

                    Icon(

                        Icons.Default.KeyboardArrowDown,

                        null,

                        modifier = Modifier.rotate(rotation),

                        tint = Color.White

                    )

                }

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

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically

                    ){

                        Box(

                            modifier = Modifier

                                .height(2.dp)

                                .weight(1f)

                                .background(Color(0xFF334155))

                        )

                        Text(

                            text = " ADD BET ",

                            modifier = Modifier.padding(horizontal = 10.dp),

                            color = Color.White,

                            style = MaterialTheme.typography.titleMedium,

                            fontWeight = FontWeight.Bold

                        )

                        Box(

                            modifier = Modifier

                                .height(2.dp)

                                .weight(1f)

                                .background(Color(0xFF334155))

                        )

                    }

                    Spacer(Modifier.height(20.dp))



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

                            Button(

                                modifier = Modifier

                                    .fillMaxWidth()

                                    .height(54.dp),

                                onClick = {

                                    val bet = amount.toDoubleOrNull()

                                    when{

                                        number.isBlank()->{

                                            error="Enter Number"

                                        }

                                        !BetValidator.validate(

                                            gameCode,

                                            number

                                        )->{

                                            error=

                                                BetValidator.error(

                                                    gameCode

                                                )

                                        }

                                        bet==null->{

                                            error="Enter Amount"

                                        }

                                        bet<=0->{

                                            error="Invalid Amount"

                                        }

                                        bet>wallet->{

                                            error="Wallet Low"

                                        }

                                        betList.any{

                                            it.number==number

                                        }->{

                                            error="Number Already Added"

                                        }

                                        else->{

                                            error=""

                                            betList.add(

                                                ProBetItem(

                                                    number,

                                                    bet

                                                )

                                            )

                                            number=""

                                            amount=""

                                        }

                                    }

                                }

                            ){

                                Text(

                                    "ADD TO BET LIST"

                                )

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
                    Spacer(Modifier.height(18.dp))


                    OutlinedTextField(
                        value = number,
                        onValueChange = {
                            number = it.filter { c -> c.isDigit() }
                        },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Number") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF111827),
                            unfocusedContainerColor = Color(0xFF111827),
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            cursorColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = {
                            Text("Amount")
                        },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF111827),
                            unfocusedContainerColor = Color(0xFF111827),

                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,

                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color.Gray,

                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,

                            cursorColor = Color.White
                        )
                    )
                    BetEntryDialog(

                        show = showBetDialog,

                        gameCode = gameCode,

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