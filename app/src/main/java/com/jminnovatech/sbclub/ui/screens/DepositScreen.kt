package com.jminnovatech.sbclub.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.jminnovatech.sbclub.data.model.WalletRequest
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AuthVM
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    context: Context,
    amount: String = "",
    transactionId: String = "",
    onClose: () -> Unit
) {
    val authVM: AuthVM = viewModel()
    val clipboard = LocalClipboardManager.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Deposit", "History")

    var upiId by remember { mutableStateOf("") }
    var amountValue by remember { mutableStateOf(amount) }
    var txnId by remember { mutableStateOf(transactionId) }

    // Dialog & Loading States
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authVM.loadMessage(context)
        authVM.loadDepositHistory(context)
    }

    LaunchedEffect(authVM.upiId) {
        if (authVM.upiId.isNotBlank()) {
            upiId = authVM.upiId
        }
    }

    LaunchedEffect(authVM.depositState) {
        when (val state = authVM.depositState) {
            is ApiState.Loading -> {
                isLoading = true
            }
            is ApiState.Success -> {

                isLoading = false

                successMessage = state.data.ifBlank {
                    "Recharge request submitted successfully."
                }

                // Clear saved payment amount
                context
                    .getSharedPreferences(
                        "SBCLUB_PAYMENT",
                        Context.MODE_PRIVATE
                    )
                    .edit()
                    .remove("pending_amount")
                    .apply()

                showSuccessDialog = true
            }
            is ApiState.Error -> {
                isLoading = false
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                authVM.clearDepositState()
            }
            else -> {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = {
                        Text(
                            "Deposit Money",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )

                // Tab Row Switcher
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                if (index == 1) {
                                    authVM.loadDepositHistory(context)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            if (selectedTabIndex == 0) {
                // TAB 1: DEPOSIT FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SCAN TO PAY",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            if (authVM.qrImage.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .size(210.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = authVM.qrImage,
                                        contentDescription = "QR Code",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }

                            if (authVM.upiName.isNotBlank()) {
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    text = authVM.upiName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = upiId,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("UPI ID") },
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (upiId.isNotBlank()) {
                                                clipboard.setText(AnnotatedString(upiId))
                                                Toast.makeText(context, "UPI ID copied", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = "Copy UPI ID",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                singleLine = true
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Enter Payment Details",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = amountValue,
                                onValueChange = {
                                    amountValue = it.filter(Char::isDigit).take(6)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Amount (₹)") },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            Spacer(Modifier.height(12.dp))

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = amountValue.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981),
                                    disabledContainerColor = Color(0xFFA7F3D0)
                                ),
                                onClick = {

                                    // Payment amount save
                                    context
                                        .getSharedPreferences(
                                            "SBCLUB_PAYMENT",
                                            Context.MODE_PRIVATE
                                        )
                                        .edit()
                                        .putString("pending_amount", amountValue)
                                        .apply()

                                    // Existing UPI code
                                    authVM.openUpi(
                                        context = context,
                                        upiId = upiId,
                                        upiName = authVM.upiName,
                                        amount = amountValue
                                    )
                                }
                            ) {
                                Text(
                                    "PAY VIA UPI APP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            Spacer(Modifier.height(20.dp))

                            OutlinedTextField(
                                value = txnId,
                                onValueChange = {
                                    txnId = it.uppercase()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Transaction / UTR Number") },
                                placeholder = { Text("12-digit UTR Number") },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                singleLine = true
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "Tip: Share receipt from Google Pay / PhonePe to auto-fill the UTR.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading,
                                onClick = {
                                    if (amountValue.isBlank()) {
                                        Toast.makeText(context, "Enter Amount", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (txnId.isBlank()) {
                                        Toast.makeText(context, "Enter Transaction ID / UTR", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val amt = amountValue.toDoubleOrNull()
                                    if (amt == null) {
                                        Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    if (amt < 10) {
                                        Toast.makeText(context, "Minimum deposit ₹10", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    authVM.submitDeposit(context, amt, txnId)
                                }
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "SUBMIT DEPOSIT REQUEST",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // TAB 2: DEPOSIT HISTORY
                when (val historyState = authVM.depositHistoryState) {
                    is ApiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is ApiState.Success -> {
                        val historyList = historyState.data
                        if (historyList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.LightGray
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "No deposit history found",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = historyList,
                                    key = { item -> item.id }
                                ) { item: WalletRequest ->
                                    WalletRequestItemCard(item = item)
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
                                text = historyState.message,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    // Success Pop-up Dialog
    if (showSuccessDialog) {
        Dialog(onDismissRequest = { }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Request Submitted!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = successMessage,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF16A34A)
                        ),
                        onClick = {
                            amountValue = ""
                            txnId = ""
                            showSuccessDialog = false
                            authVM.clearDepositState()

                            selectedTabIndex = 1
                            authVM.loadDepositHistory(context)
                        }
                    ) {
                        Text(
                            text = "VIEW IN HISTORY",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WalletRequestItemCard(item: WalletRequest) {
    val (statusColor, statusBg, statusText, statusIcon) = when (item.status.lowercase()) {
        "approved", "success" -> StatusConfig(
            Color(0xFF16A34A),
            Color(0xFFDCFCE7),
            "Approved",
            Icons.Default.CheckCircle
        )
        "rejected", "failed" -> StatusConfig(
            Color(0xFFDC2626),
            Color(0xFFFEE2E2),
            "Rejected",
            Icons.Default.Cancel
        )
        else -> StatusConfig(
            Color(0xFFD97706),
            Color(0xFFFEF3C7),
            "Pending",
            Icons.Default.HourglassTop
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${item.amount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "UTR: ",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.utr_no,
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.payment_method.ifBlank { "UPI" },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatIsoDate(item.created_at),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private data class StatusConfig(
    val color: Color,
    val bgColor: Color,
    val text: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

fun formatIsoDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(isoDate)

        val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()
        if (date != null) outputFormat.format(date) else isoDate
    } catch (e: Exception) {
        isoDate
    }
}