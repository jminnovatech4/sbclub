package com.jminnovatech.sbclub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jminnovatech.sbclub.data.model.progame.ResultGroup
import com.jminnovatech.sbclub.data.model.progame.ResultGame


@Composable
fun ResultNotificationPopup(

    groups: List<ResultGroup>,

    onClose: () -> Unit

) {

    AlertDialog(

        onDismissRequest = onClose,

        title = {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Icon(

                    imageVector =
                        Icons.Default.Notifications,

                    contentDescription = null,

                    tint = Color(0xFF2563EB)

                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text(

                    text = "RESULT OUT",

                    fontWeight = FontWeight.Bold

                )

            }

        },

        text = {

            LazyColumn(

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        max = 500.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)

            ) {

                items(

                    items = groups,

                    key = {
                        "${it.group_id}_${it.baji_no}"
                    }

                ) { group ->

                    ResultGroupCard(
                        group = group
                    )

                }

            }

        },

        confirmButton = {

            Button(

                onClick = onClose

            ) {

                Text("CLOSE")

            }

        },

        dismissButton = {

            IconButton(

                onClick = onClose

            ) {

                Icon(

                    Icons.Default.Close,

                    contentDescription = "Close"

                )

            }

        }

    )

}


@Composable
private fun ResultGroupCard(

    group: ResultGroup

) {

    Card(

        modifier = Modifier
            .fillMaxWidth(),

        shape =
            RoundedCornerShape(14.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (group.group_id == 1)

                        Color(0xFFEFF6FF)

                    else

                        Color(0xFFFFF7ED)

            )

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)

        ) {

            Text(

                text =
                    group.group_name,

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    if (group.group_id == 1)

                        Color(0xFF1D4ED8)

                    else

                        Color(0xFFC2410C)

            )


            Spacer(
                Modifier.height(4.dp)
            )


            Text(

                text =
                    "${group.title ?: "Baji"} • Baji ${group.baji_no}",

                fontWeight =
                    FontWeight.SemiBold,

                color = Color.DarkGray

            )


            Text(

                text =
                    "Date: ${group.result_date ?: "-"}",

                fontSize = 12.sp,

                color = Color.Gray

            )


            Text(

                text =
                    "Game Time: ${formatGameTime(group.start_time)} - ${formatGameTime(group.end_time)}",

                fontSize = 12.sp,

                color = Color.Gray

            )


            Text(

                text =
                    "Result Time: ${formatGameTime(group.result_time)}",

                fontSize = 12.sp,

                color = Color.Gray

            )


            Spacer(
                Modifier.height(10.dp)
            )


            group.results.forEach { result ->

                ResultGameRow(
                    result = result
                )

                Spacer(
                    Modifier.height(6.dp)
                )

            }

        }

    }

}


@Composable
private fun ResultGameRow(

    result: ResultGame

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(10.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White

            )

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Column(

                modifier =
                    Modifier.weight(1f)

            ) {

                Text(

                    text =
                        result.game_name,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 14.sp

                )

                Text(

                    text =
                        result.game_code,

                    fontSize = 11.sp,

                    color = Color.Gray

                )

            }


            Text(

                text =
                    result.result_number,

                fontSize = 24.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                color =
                    Color(0xFF16A34A)

            )

        }

    }

}


private fun formatGameTime(
    value: String?
): String {

    if (
        value.isNullOrBlank()
    ) {
        return "-"
    }

    return try {

        val input =
            java.text.SimpleDateFormat(
                "HH:mm:ss",
                java.util.Locale.ENGLISH
            )

        val output =
            java.text.SimpleDateFormat(
                "hh:mm a",
                java.util.Locale.ENGLISH
            )

        output.format(
            input.parse(value)!!
        )

    } catch (e: Exception) {

        value

    }

}