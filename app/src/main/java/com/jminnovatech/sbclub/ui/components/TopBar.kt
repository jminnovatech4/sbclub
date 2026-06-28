package com.jminnovatech.sbclub.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title:String) {
    TopAppBar(title = { Text(title) })
}