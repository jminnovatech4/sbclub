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

@Composable
fun ScheduleCard(

    gameId: Int,

    schedule: Schedule,

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

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFF1E293B)

        )

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically

            ) {

                Column(

                    modifier = Modifier.weight(1f)

                ) {

                    Text(

                        text = "Baji ${schedule.baji_no}",

                        style = MaterialTheme.typography.titleLarge,

                        color = Color.White,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(

                        Modifier.height(4.dp)

                    )

                    Text(

                        text =

                            formatTime(schedule.start_time)

                                    +

                                    " - "

                                    +

                                    formatTime(schedule.end_time),

                        color = Color.LightGray

                    )

                }

                AssistChip(

                    onClick = {},

                    label = {

                        Text(state)

                    },

                    leadingIcon = {

                        when (state) {

                            "RUNNING" ->

                                Icon(
                                    Icons.Default.PlayArrow,
                                    null
                                )

                            "RESULT" ->

                                Icon(
                                    Icons.Default.CheckCircle,
                                    null
                                )

                            else ->

                                Icon(
                                    Icons.Default.Lock,
                                    null
                                )

                        }

                    },

                    colors = AssistChipDefaults.assistChipColors(

                        containerColor = chipColor,

                        labelColor = Color.White

                    )

                )

            }

            Spacer(

                Modifier.height(12.dp)

            )

            Text(

                text =

                    "Result : ${resultNumber ?: "---"}",

                color = Color.White,

                style = MaterialTheme.typography.bodyLarge

            )

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

            Button(

                modifier = Modifier.fillMaxWidth(),

                enabled = state != "LOCK",

                onClick = {

                    if (state != "LOCK") {

                        expanded = !expanded

                    }

                }

            ) {

                Text(

                    when (state) {

                        "RUNNING" ->

                            if (expanded)

                                "CLOSE GAME"

                            else

                                "OPEN GAME"

                        "RESULT" ->

                            if (expanded)

                                "HIDE HISTORY"

                            else

                                "VIEW HISTORY"

                        else ->

                            "LOCKED"

                    }

                )

            }

            AnimatedVisibility(

                visible = expanded,

                enter = fadeIn() + expandVertically(),

                exit = fadeOut() + shrinkVertically()

            ) {                if (state == "RUNNING") {

                Spacer(
                    Modifier.height(18.dp)
                )

                Text(

                    text = "Wallet Balance",

                    style = MaterialTheme.typography.labelMedium,

                    color = Color.LightGray

                )

                Spacer(
                    Modifier.height(4.dp)
                )

                Text(

                    text = "₹ %.2f".format(wallet),

                    style = MaterialTheme.typography.titleLarge,

                    color = Color(0xFF22C55E),

                    fontWeight = FontWeight.Bold

                )

                Spacer(
                    Modifier.height(20.dp)
                )

                BetEntryCard(

                    onAdd = { number, amount ->

                        betList.add(

                            ProBetItem(

                                number = number,

                                amount = amount

                            )

                        )

                    }

                )

                Spacer(
                    Modifier.height(18.dp)
                )

                BetListCard(

                    bets = betList,

                    onDelete = { index ->

                        if (index in betList.indices) {

                            betList.removeAt(index)

                        }

                    },

                    onPlaceBet = {

                        if (betList.isNotEmpty()) {

                            onPlaceBet(

                                schedule.id,

                                betList.toList()

                            )

                            betList.clear()

                            expanded = false

                        }

                    }

                )

            } else {

                Spacer(
                    Modifier.height(18.dp)
                )

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