package com.jminnovatech.sbclub.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CreateMasterDialog(
    onClose: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {

            Button(onClick = {
                onSubmit(name, phone, pass)
            }) {
                Text("Create")
            }

        },
        dismissButton = {
            Button(onClick = onClose) {
                Text("Cancel")
            }
        },
        title = { Text("Create Master") },
        text = {

            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )


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

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password") }
                )
            }
        }
    )
}