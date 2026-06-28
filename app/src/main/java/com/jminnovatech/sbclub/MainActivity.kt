package com.jminnovatech.sbclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jminnovatech.sbclub.navigation.AppNav
import com.jminnovatech.sbclub.utils.NetworkMonitor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkMonitor.start(this)
        setContent {
            AppNav(this)
        }
    }
}