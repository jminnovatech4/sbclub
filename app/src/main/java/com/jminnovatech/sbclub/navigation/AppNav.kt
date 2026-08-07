package com.jminnovatech.sbclub.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.jminnovatech.sbclub.repository.AppRepository
import com.jminnovatech.sbclub.ui.screens.*
import com.jminnovatech.sbclub.ui.screens.admin.AdminScreen
import com.jminnovatech.sbclub.ui.screens.master.MasterScreen
import com.jminnovatech.sbclub.ui.screens.progame.DashboardScreen
import com.jminnovatech.sbclub.ui.screens.user.ResultScreen
import com.jminnovatech.sbclub.ui.screens.user.progame.PlayGameScreen
import com.jminnovatech.sbclub.ui.screens.user.progame.ProGameScreen
import com.jminnovatech.sbclub.viewmodel.MasterVM
import com.jminnovatech.sbclub.viewmodel.WalletVM
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jminnovatech.sbclub.ui.screens.admin.game.ResultPanelScreen
import com.jminnovatech.sbclub.viewmodel.AdminVM
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNav(
    context: Context,

    openDeposit: Boolean = false,

    sharedAmount: String = "",

    sharedUtr: String = "",

    receiptText: String = "",

    upiApp: String = ""

) {

    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "splash") {

        // 🔹 SPLASH
        composable("splash") {

            SplashScreen(

                nav = nav,

                context = context,

                openDeposit = openDeposit,

                sharedAmount = sharedAmount,

                sharedUtr = sharedUtr

            )

        }

        // 🔹 LOGIN
        composable("login") { LoginScreen(nav, context) }

        // 🔹 ADMIN
        composable("admin") { AdminScreen(nav) }
        composable("result_panel") {

            val vm: AdminVM = viewModel()

            ResultPanelScreen(vm)

        }
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
        composable("user/{gameId}") {

            val gameId =
                it.arguments?.getString("gameId")?.toInt() ?: 1

            UserMainScreen(
                nav = nav,
                context = context,
                gameId = gameId
            )
        }

        // 🔹 OPTIONAL (KEEP FOR DIRECT NAV)
        composable("results") { ResultScreen() }
        composable("dashboard") {

            DashboardScreen(

                nav = nav,

                context = context,

                openDeposit = openDeposit,

                sharedAmount = sharedAmount,

                sharedUtr = sharedUtr

            )

        }
        composable(
            route = "pro_game/{gameId}"
        ) {

            val gameId =
                it.arguments?.getString("gameId")?.toInt() ?: 1

            ProGameScreen(
                nav = nav,
                context = context,
                gameId = gameId
            )

        }
        composable(

            "play_game/{gameId}/{scheduleId}"

        ) {

            val gameId =

                it.arguments?.getString("gameId")!!.toInt()

            val scheduleId =

                it.arguments?.getString("scheduleId")!!.toInt()

            PlayGameScreen(

                nav = nav,

                context = context,

                gameId = gameId,

                scheduleId = scheduleId

            )

        }
    }
}