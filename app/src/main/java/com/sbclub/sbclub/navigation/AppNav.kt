package com.sbclub.sbclub.navigation

import android.content.Context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*

import com.sbclub.sbclub.repository.AppRepository
import com.sbclub.sbclub.ui.screens.*
import com.sbclub.sbclub.ui.screens.admin.AdminScreen
import com.sbclub.sbclub.ui.screens.master.MasterScreen
import com.sbclub.sbclub.ui.screens.progame.DashboardScreen
import com.sbclub.sbclub.ui.screens.user.ResultScreen
import com.sbclub.sbclub.ui.screens.user.progame.PlayGameScreen
import com.sbclub.sbclub.ui.screens.user.progame.ProGameScreen
import com.sbclub.sbclub.viewmodel.MasterVM
import com.sbclub.sbclub.viewmodel.WalletVM
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sbclub.sbclub.ui.screens.admin.game.ResultPanelScreen
import com.sbclub.sbclub.ui.screens.user.progame.GroupGameScreen
import com.sbclub.sbclub.viewmodel.AdminVM


@Composable
fun AppNav(
    context: Context,

    startDestination: String = "splash",

    // =========================================
    // DEPOSIT / UPI SHARE
    // =========================================

    openDeposit: Boolean = false,

    sharedAmount: String = "",

    sharedUtr: String = "",

    receiptText: String = "",

    upiApp: String = "",


    // =========================================
    // FCM RESULT NOTIFICATION
    // =========================================

    openResult: Boolean = false,

    resultId: String? = null,

    resultGroupId: String? = null,

    resultBajiNo: String? = null,


    // =========================================
    // FCM ADMIN MESSAGE NOTIFICATION
    // =========================================

    openMessage: Boolean = false,

    messageId: String? = null

) {

    val nav = rememberNavController()


    NavHost(
        navController = nav,
        startDestination = startDestination
    ) {


        // =========================================
        // 🔹 SPLASH
        // =========================================

        composable("splash") {

            SplashScreen(

                nav = nav,

                context = context,

                openDeposit = openDeposit,

                sharedAmount = sharedAmount,

                sharedUtr = sharedUtr

            )
        }


        // =========================================
        // 🔹 LOGIN
        // =========================================

        composable("login") {

            LoginScreen(
                nav,
                context
            )
        }


        // =========================================
        // 🔹 ADMIN
        // =========================================

        composable("admin") {

            AdminScreen(nav)
        }


        composable("result_panel") {

            val vm: AdminVM = viewModel()

            ResultPanelScreen(vm)
        }


        // =========================================
        // 🔹 MASTER
        // =========================================

        composable("master") {

            val vm =
                remember {
                    MasterVM(context)
                }

            MasterScreen(
                vm,
                nav
            )
        }


        // =========================================
        // 🔹 WITHDRAW
        // =========================================

        composable("withdraw") {

            val context =
                LocalContext.current

            val walletVM =
                remember {
                    WalletVM(
                        AppRepository(context)
                    )
                }

            WithdrawScreen(walletVM)
        }


        // =========================================
        // 🔥 USER MAIN
        // =========================================

        composable("user/{groupId}/{gameId}") {

            val groupId =
                it.arguments
                    ?.getString("groupId")
                    ?.toInt()
                    ?: 1

            val gameId =
                it.arguments
                    ?.getString("gameId")
                    ?.toInt()
                    ?: 1

            UserMainScreen(

                nav = nav,

                context = context,

                groupId = groupId,

                gameId = gameId
            )
        }


        // =========================================
        // 🔹 OPTIONAL RESULTS
        // =========================================

        composable("results") {

            ResultScreen()
        }


        // =========================================
        // 🔥 DASHBOARD
        // =========================================

        composable("dashboard") {

            DashboardScreen(

                nav = nav,

                context = context,

                openDeposit = openDeposit,

                sharedAmount = sharedAmount,

                sharedUtr = sharedUtr

            )
        }


        // =========================================
        // 🔹 PRO GAME
        // =========================================

        composable(
            route = "pro_game/{groupId}/{gameId}"
        ) {

            val groupId =
                it.arguments
                    ?.getString("groupId")
                    ?.toInt()
                    ?: 1

            val gameId =
                it.arguments
                    ?.getString("gameId")
                    ?.toInt()
                    ?: 1

            ProGameScreen(

                nav = nav,

                context = context,

                groupId = groupId,

                gameId = gameId
            )
        }


        // =========================================
        // 🔹 PRO GAME GROUP
        // =========================================

        composable(
            route = "pro_game_group/{groupId}"
        ) {

            val groupId =
                it.arguments
                    ?.getString("groupId")
                    ?.toInt()
                    ?: 1

            GroupGameScreen(

                nav = nav,

                context = context,

                groupId = groupId
            )
        }


        // =========================================
        // 🔹 PLAY GAME
        // =========================================

        composable(
            "play_game/{groupId}/{gameId}/{scheduleId}/{wallet}"
        ) {

            val groupId =
                it.arguments
                    ?.getString("groupId")
                    ?.toIntOrNull()
                    ?: 1

            val gameId =
                it.arguments
                    ?.getString("gameId")
                    ?.toIntOrNull()
                    ?: 1

            val scheduleId =
                it.arguments
                    ?.getString("scheduleId")
                    ?.toIntOrNull()
                    ?: 0

            val wallet =
                it.arguments
                    ?.getString("wallet")
                    ?.toDoubleOrNull()
                    ?: 0.0

            PlayGameScreen(

                nav = nav,

                context = context,

                groupId = groupId,

                gameId = gameId,

                scheduleId = scheduleId,

                initialWallet = wallet
            )
        }
    }
}