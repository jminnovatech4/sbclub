package com.jminnovatech.sbclub.ui.screens.admin.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jminnovatech.sbclub.data.model.admin.game.ScheduleHistory

@Composable
fun HistoryCard(
    item: ScheduleHistory
) {

    var expanded by remember {

        mutableStateOf(false)

    }

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                        expanded = !expanded

                    },

                verticalAlignment = Alignment.CenterVertically

            ) {

                Column(

                    modifier = Modifier.weight(1f)

                ) {

                    Text(

                        text = item.title ?: "BAJI-${item.baji_no}",

                        style = MaterialTheme.typography.titleMedium

                    )

                    Spacer(

                        Modifier.height(2.dp)

                    )

                    Text(

                        text =

                            "${item.start_time} - ${item.end_time}",

                        style = MaterialTheme.typography.bodySmall

                    )

                }

                Icon(

                    imageVector =

                        if (expanded)

                            Icons.Default.ExpandLess

                        else

                            Icons.Default.ExpandMore,

                    contentDescription = null

                )

            }

            if (expanded) {

                Spacer(

                    Modifier.height(12.dp)

                )

                item.results.forEach {

                    HorizontalDivider()

                    Spacer(

                        Modifier.height(8.dp)

                    )

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {

                        Text(

                            it.game_name

                        )

                        Text(

                            it.result_number ?: "-"

                        )

                    }

                    Spacer(

                        Modifier.height(8.dp)

                    )

                }

            }

        }

    }

}