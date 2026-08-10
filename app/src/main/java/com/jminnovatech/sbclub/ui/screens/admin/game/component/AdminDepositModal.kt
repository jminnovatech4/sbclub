package com.jminnovatech.sbclub.ui.screens.admin.game.components
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDepositModal(
    walletVM: WalletVM,
    onClose: () -> Unit
) {

    var selectedTab by remember {
        mutableStateOf(0)
    }

    val tabs = listOf(
        "Pending",
        "History"
    )

    Dialog(
        onDismissRequest = onClose
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFF8FAFC)
        ) {

            Column {

                // =====================================
                // HEADER
                // =====================================

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2563EB),
                    shape = RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 14.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                "Deposit Management",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                "Manage user deposit requests",
                                color =
                                    Color.White.copy(
                                        alpha = 0.75f
                                    ),
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = onClose
                        ) {

                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }


                // =====================================
                // TABS
                // =====================================

                TabRow(
                    selectedTabIndex = selectedTab
                ) {

                    tabs.forEachIndexed {
                            index,
                            title ->

                        Tab(
                            selected =
                                selectedTab == index,

                            onClick = {

                                selectedTab =
                                    index

                            },

                            text = {

                                Text(
                                    title,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        )
                    }
                }


                // =====================================
                // CONTENT
                // =====================================

                when (selectedTab) {

                    0 -> {

                        AdminDepositPendingList(
                            walletVM
                        )

                    }

                    1 -> {

                        AdminDepositHistoryList(
                            walletVM
                        )

                    }
                }

            }
        }
    }
}

@Composable
fun AdminDepositPendingList(
    walletVM: WalletVM
) {

    val state =
        walletVM.adminDepositPendingState

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (state) {

            is ApiState.Loading -> {

                CircularProgressIndicator(
                    modifier =
                        Modifier.align(
                            Alignment.Center
                        )
                )
            }

            is ApiState.Error -> {

                Column(
                    modifier =
                        Modifier.align(
                            Alignment.Center
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        state.message,
                        color = Color.Red
                    )

                    Spacer(
                        Modifier.height(10.dp)
                    )

                    Button(
                        onClick = {
                            walletVM
                                .loadAdminDepositPending()
                        }
                    ) {

                        Text("Retry")
                    }
                }
            }

            is ApiState.Success -> {

                val list =
                    state.data

                if (list.isEmpty()) {

                    Text(
                        "No pending deposits",
                        modifier =
                            Modifier.align(
                                Alignment.Center
                            ),
                        color = Color.Gray
                    )

                } else {

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(12.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        items(
                            list,
                            key = {
                                it.id
                            }
                        ) { item ->

                            AdminDepositPendingCard(
                                item,
                                walletVM
                            )
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun AdminDepositPendingCard(
    item: com.jminnovatech.sbclub.data.model.WalletRequest,
    walletVM: WalletVM
) {

    val context = LocalContext.current

    var amount by remember(
        item.id,
        item.amount
    ) {

        mutableStateOf(
            item.amount.toString()
        )
    }

    var showApproveConfirm by remember {
        mutableStateOf(false)
    }

    var showRejectConfirm by remember {
        mutableStateOf(false)
    }
    val approveState =
        walletVM.depositActionState
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.White
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // =====================================
            // USER
            // =====================================

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier =
                        Modifier.size(42.dp)
                )

                Spacer(
                    Modifier.width(10.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        "User ID: ${item.user_id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        "Deposit #${item.id}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape =
                        RoundedCornerShape(20.dp),
                    color =
                        Color(0xFFFFF7ED)
                ) {

                    Text(
                        "PENDING",
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                        color =
                            Color(0xFFEA580C),
                        fontSize = 11.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                Modifier.height(14.dp)
            )

            HorizontalDivider()

            Spacer(
                Modifier.height(12.dp)
            )


            // =====================================
            // UTR
            // =====================================

            Text(
                "UTR / Transaction ID",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(
                Modifier.height(3.dp)
            )

            Text(
                item.utr_no,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )


            Spacer(
                Modifier.height(12.dp)
            )


            // =====================================
            // AMOUNT EDIT
            // =====================================

            OutlinedTextField(

                value = amount,

                onValueChange = {
                    amount =
                        it.filter { c ->
                            c.isDigit() ||
                                    c == '.'
                        }
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Approve Amount")
                },

                prefix = {
                    Text("₹ ")
                },

                singleLine = true,

                keyboardOptions =
                    androidx.compose.foundation.text
                        .KeyboardOptions(
                            keyboardType =
                                androidx.compose.ui
                                    .text
                                    .input
                                    .KeyboardType
                                    .Decimal
                        ),

                shape =
                    RoundedCornerShape(12.dp)
            )


            Spacer(
                Modifier.height(12.dp)
            )


            // =====================================
            // BUTTONS
            // =====================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // ==========================
                // REJECT
                // ==========================

                OutlinedButton(

                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),

                    onClick = {
                        showRejectConfirm = true
                    },

                    contentPadding = PaddingValues(
                        horizontal = 6.dp,
                        vertical = 0.dp
                    ),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )

                ) {

                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(
                        Modifier.width(3.dp)
                    )

                    Text(
                        "Reject",
                        fontSize = 11.sp
                    )
                }


                // ==========================
                // APPROVE
                // ==========================

                Button(

                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),

                    onClick = {

                        val value =
                            amount.toDoubleOrNull()

                        if (
                            value == null ||
                            value < 10
                        ) {

                            Toast.makeText(
                                context,
                                "Invalid amount",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@Button
                        }

                        showApproveConfirm = true
                    },

                    contentPadding = PaddingValues(
                        horizontal = 6.dp,
                        vertical = 0.dp
                    ),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A)
                    )

                ) {

                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(
                        Modifier.width(3.dp)
                    )

                    Text(
                        "Approve",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }


    // =========================================
    // APPROVE CONFIRM
    // =========================================

    if (showApproveConfirm) {

        AlertDialog(

            onDismissRequest = {
                showApproveConfirm =
                    false
            },

            title = {
                Text("Approve Deposit")
            },

            text = {

                Text(
                    "Approve ₹$amount for UTR\n${item.utr_no}?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        val value =
                            amount.toDoubleOrNull()

                        if (value == null || value < 10) {

                            Toast.makeText(
                                context,
                                "Invalid amount",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@Button
                        }

                        Toast.makeText(
                            context,
                            "Approving ₹$value...",
                            Toast.LENGTH_SHORT
                        ).show()

                        walletVM.approveDeposit(
                            item.id,
                            value
                        )

                        // এখনই dialog close করছি না
                    }
                ) {

                    Text("Approve")
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        showApproveConfirm =
                            false
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
    LaunchedEffect(approveState) {

        when (approveState) {

            is ApiState.Success -> {

                Toast.makeText(
                    context,
                    approveState.data,
                    Toast.LENGTH_LONG
                ).show()

                showApproveConfirm = false

                walletVM.loadAdminDepositPending()

                walletVM.loadAdminDepositHistory()
            }

            is ApiState.Error -> {

                Toast.makeText(
                    context,
                    "Approve failed: ${approveState.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {}
        }
    }

    // =========================================
    // REJECT CONFIRM
    // =========================================

    if (showRejectConfirm) {

        AlertDialog(

            onDismissRequest = {
                showRejectConfirm =
                    false
            },

            title = {
                Text("Reject Deposit")
            },

            text = {
                Text(
                    "Reject this deposit request?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        walletVM
                            .rejectDeposit(
                                item.id
                            )

                        showRejectConfirm =
                            false
                    },

                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    Color.Red
                            )
                ) {

                    Text("Reject")
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        showRejectConfirm =
                            false
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }

}

@Composable
fun AdminDepositHistoryList(
    walletVM: WalletVM
) {

    val state =
        walletVM.adminDepositHistoryState

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (state) {

            is ApiState.Loading -> {

                CircularProgressIndicator(
                    modifier =
                        Modifier.align(
                            Alignment.Center
                        )
                )
            }

            is ApiState.Error -> {

                Column(
                    modifier =
                        Modifier.align(
                            Alignment.Center
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        state.message,
                        color = Color.Red
                    )

                    Spacer(
                        Modifier.height(10.dp)
                    )

                    Button(
                        onClick = {
                            walletVM
                                .loadAdminDepositHistory()
                        }
                    ) {

                        Text("Retry")
                    }
                }
            }

            is ApiState.Success -> {

                val list =
                    state.data

                if (list.isEmpty()) {

                    Text(
                        "No deposit history",
                        modifier =
                            Modifier.align(
                                Alignment.Center
                            ),
                        color = Color.Gray
                    )

                } else {

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(12.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        items(
                            list,
                            key = {
                                it.id
                            }
                        ) { item ->

                            AdminDepositHistoryCard(
                                item
                            )
                        }
                    }
                }
            }

            else -> {}
        }
    }
}
@Composable
fun AdminDepositHistoryCard(
    item: com.jminnovatech.sbclub.data.model.WalletRequest
) {

    val statusColor =
        when (item.status.lowercase()) {

            "approved" ->
                Color(0xFF16A34A)

            "rejected" ->
                Color(0xFFDC2626)

            else ->
                Color(0xFFEA580C)
        }

    val statusBg =
        when (item.status.lowercase()) {

            "approved" ->
                Color(0xFFDCFCE7)

            "rejected" ->
                Color(0xFFFEE2E2)

            else ->
                Color(0xFFFFF7ED)
        }


    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(15.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        "User ID: ${item.user_id}",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "ID #${item.id}",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    color = statusBg,
                    shape =
                        RoundedCornerShape(20.dp)
                ) {

                    Text(
                        item.status.uppercase(),
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                Modifier.height(10.dp)
            )

            Row {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        "Amount",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )

                    Text(
                        "₹ ${item.amount}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        "Payment",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )

                    Text(
                        item.payment_method,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(
                Modifier.height(10.dp)
            )

            Text(
                "UTR",
                color = Color.Gray,
                fontSize = 11.sp
            )

            Text(
                item.utr_no,
                fontWeight =
                    FontWeight.Medium
            )

            if (!item.created_at.isNullOrBlank()) {

                Spacer(
                    Modifier.height(6.dp)
                )

                Text(
                    item.created_at,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}


