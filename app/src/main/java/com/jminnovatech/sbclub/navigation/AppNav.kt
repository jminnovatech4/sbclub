package com.jminnovatech.sbclub.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.ui.screens.*
import com.jminnovatech.sbclub.ui.screens.admin.AdminScreen
import com.jminnovatech.sbclub.ui.screens.master.MasterScreen
import com.jminnovatech.sbclub.ui.screens.user.ResultScreen
import com.jminnovatech.sbclub.viewmodel.MasterVM
import com.jminnovatech.sbclub.viewmodel.WalletVM

@Composable
fun AppNav(context: Context) {

    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "splash") {

        // 🔹 SPLASH
        composable("splash") { SplashScreen(nav, context) }

        // 🔹 LOGIN
        composable("login") { LoginScreen(nav, context) }

        // 🔹 ADMIN
        composable("admin") { AdminScreen(nav) }

        // 🔹 MASTER
        composable("master") {

            val vm = remember { MasterVM(context) }

            MasterScreen(vm, nav) // ✅ FIXED
        }
        composable("withdraw") {
            val context = LocalContext.current
            val walletVM = remember { WalletVM(AppRepository(context)) }

            WithdrawScreen(walletVM)
        }
        // 🔥 USER MAIN (IMPORTANT FIX)
        composable("user") { UserMainScreen(nav, context) }

        // 🔹 OPTIONAL (KEEP FOR DIRECT NAV)
        composable("results") { ResultScreen() }
    }
}