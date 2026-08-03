package com.jminnovatech.sbclub.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jminnovatech.sbclub.viewmodel.AuthVM
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import coil.compose.AsyncImage
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(

    context: Context,

    onClose: () -> Unit

) {

    val authVM: AuthVM = viewModel()

    LaunchedEffect(Unit) {

        authVM.loadMessage(context)

    }
    var upiId by remember {
        mutableStateOf("")
    }

    LaunchedEffect(authVM.upiId) {
        if (authVM.upiId.isNotBlank()) {
            upiId = authVM.upiId
        }
    }
    var amount by remember {
        mutableStateOf("")
    }

    var txnId by remember {
        mutableStateOf("")
    }
    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text("Deposit Money")

                },

                navigationIcon = {

                    IconButton(

                        onClick = onClose

                    ) {

                        Icon(

                            Icons.Default.Close,

                            null

                        )

                    }

                }

            )

        }

    ) { padding ->

        val clipboard = LocalClipboardManager.current

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7FA))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Card(

                shape = RoundedCornerShape(22.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Text(

                        "PAY USING UPI",

                        fontSize = 20.sp,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(Modifier.height(20.dp))

                    if (authVM.qrImage.isNotBlank()) {

                        AsyncImage(

                            model = authVM.qrImage,

                            contentDescription = null,

                            modifier = Modifier.size(220.dp)

                        )

                    }

                    Spacer(Modifier.height(20.dp))

                    Text(

                        authVM.upiName,

                        fontWeight = FontWeight.Bold,

                        fontSize = 22.sp

                    )

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(

                        value = upiId,

                        onValueChange = {
                            upiId = it
                        },

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("UPI ID")
                        },

                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    clipboard.setText(
                                        AnnotatedString(upiId)
                                    )
                                }
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = null
                                )
                            }

                        },

                        singleLine = true

                    )

                }

            }

//            Spacer(Modifier.height(20.dp))
//
//            Card(
//
//                colors = CardDefaults.cardColors(
//                    containerColor = Color(0xFF1E3A8A)
//                ),
//
//                shape = RoundedCornerShape(18.dp)
//
//            ) {
//
//                Text(
//
//                    text = authVM.message,
//
//                    modifier = Modifier.padding(16.dp),
//
//                    color = Color.White
//
//                )
//
//            }

            Spacer(Modifier.height(25.dp))

            OutlinedTextField(

                value = amount,

                onValueChange = {

                    amount = it.filter { c -> c.isDigit() }

                },

                modifier = Modifier.fillMaxWidth(),

                label = {

                    Text("Amount")

                },

                singleLine = true

            )

            Spacer(Modifier.height(15.dp))

            Button(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                onClick = {

                    authVM.openUpi(

                        context = context,

                        upiId = upiId,

                        upiName = authVM.upiName,

                        amount = amount

                    )

                }

            ) {

                Text("PAY NOW")

            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(

                value = txnId,

                onValueChange = {

                    txnId = it

                },

                modifier = Modifier.fillMaxWidth(),

                label = {

                    Text("Transaction / UTR Number")

                },

                singleLine = true

            )

            Spacer(Modifier.height(20.dp))

            Button(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                enabled =
                    amount.isNotBlank()
                            &&
                            txnId.isNotBlank(),

                onClick = {

                    // API next step

                }

            ) {

                Text("SUBMIT REQUEST")

            }

        }

}}