package com.jminnovatech.sbclub.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jminnovatech.sbclub.viewmodel.AuthVM
import com.jminnovatech.sbclub.utils.ApiState

fun isInternetAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val act = cm.getNetworkCapabilities(network) ?: return false
    return act.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(nav: NavController, context: Context) {

    val vm: AuthVM = viewModel()

    var phone by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                )
            )
    ) {

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {

            Column(
                Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("🎯 Welcome Back", style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() } && it.length <= 10) {
                            phone = it
                        }
                    },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(Modifier.height(10.dp))

                AnimatedVisibility(visible = errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Red)
                }

                Spacer(Modifier.height(15.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        // 🔥 VALIDATION
                        if (phone.isEmpty() || pass.isEmpty()) {
                            errorMsg = "All fields required"
                            return@Button
                        }
                        if (phone.length != 10) {
                            errorMsg = "Enter valid 10 digit mobile number"
                            return@Button
                        }

                        if (pass.isEmpty()) {
                            errorMsg = "Password required"
                            return@Button
                        }
                        if (!isInternetAvailable(context)) {
                            errorMsg = "No internet connection"
                            return@Button
                        }

                        errorMsg = ""
                        vm.login(context, nav, phone, pass)
                    }
                ) {
                    Text("Login")
                }

                Spacer(Modifier.height(10.dp))

                when (val state = vm.state) {

                    is ApiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is ApiState.Error -> {
                        Text(state.message, color = Color.Red)
                    }

                    else -> {}
                }
            }
        }
    }
}