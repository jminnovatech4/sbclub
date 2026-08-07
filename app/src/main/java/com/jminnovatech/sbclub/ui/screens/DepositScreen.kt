package com.jminnovatech.sbclub.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
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
import com.jminnovatech.sbclub.utils.ApiState
import com.jminnovatech.sbclub.viewmodel.AuthVM

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

    var upiId by remember { mutableStateOf("") }

    // Dialog & Loading State
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var amountValue by remember {
        mutableStateOf("")
    }

    var txnId by remember {
        mutableStateOf("")
    }

    LaunchedEffect(amount, transactionId) {

        if (amountValue.isBlank() && amount.isNotBlank()) {

            amountValue = amount

        }

        if (txnId.isBlank() && transactionId.isNotBlank()) {

            txnId = transactionId

        }

    }
    LaunchedEffect(Unit) {
        authVM.loadMessage(context)
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
                successMessage = state.data.ifBlank { "Your deposit request has been submitted successfully!" }
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // QR & UPI Details Card
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

                        // 4. UPI ID ReadOnly
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

                // Payment Inputs Section
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

                        // 6. Amount Limit (max 6 digits)
                        OutlinedTextField(
                            value = amountValue,
                            onValueChange = {
                                amountValue =

                                    it.filter {

                                        it.isDigit() || it == '.'

                                    }.take(8)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Amount (₹)") },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Spacer(Modifier.height(12.dp))

                        // 3. PAY Button Disable if amount is blank
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
                        if (amountValue.isNotBlank() || txnId.isNotBlank()) {

                            Card(

                                modifier = Modifier.fillMaxWidth(),

                                colors = CardDefaults.cardColors(

                                    containerColor = Color(0xFFE8F5E9)

                                )

                            ) {

                                Column(

                                    modifier = Modifier.padding(14.dp)

                                ) {

                                    Text(

                                        text = "Receipt Detected",

                                        color = Color(0xFF2E7D32),

                                        fontWeight = FontWeight.Bold

                                    )

                                    Spacer(Modifier.height(6.dp))

                                    if (amountValue.isNotBlank()) {

                                        Text(

                                            "Amount : ₹$amountValue"

                                        )

                                    }

                                    if (txnId.isNotBlank()) {

                                        Text(

                                            "UTR : $txnId"

                                        )

                                    }

                                }

                            }

                            Spacer(Modifier.height(15.dp))

                        }
                        // 5. UTR Auto Uppercase
                        OutlinedTextField(
                            value = txnId,
                            onValueChange = {
                                txnId = it
                                    .uppercase()
                                    .replace(" ", "")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Transaction / UTR Number") },
                            placeholder = { Text("12-digit UTR Number") },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true
                        )

                        Spacer(Modifier.height(6.dp))

                        // 8. Share Receipt Hint
                        Text(
                            text = "Tip: Share receipt from Google Pay / PhonePe to auto-fill the UTR.",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        // 2. Submit Button Loading & Disabled State
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

                                // 9. Minimum Amount check
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
        }
    }

    // Success Pop-up Dialog
    if (showSuccessDialog) {
        Dialog(onDismissRequest = { /* Prevent dismiss on outside click */ }) {
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

                    // 1. Clear Fields & 7. Refresh Balance
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

                            // 7. Wallet Refresh (AuthVM-এ loadWallet থেকলে কল হবে)
//                            try {
//                                authVM.loadWallet(context)
//                            } catch (e: Exception) {
//                                // Ignore if method name differs
//                            }

                            onClose()
                        }
                    ) {
                        Text(
                            text = "DONE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}