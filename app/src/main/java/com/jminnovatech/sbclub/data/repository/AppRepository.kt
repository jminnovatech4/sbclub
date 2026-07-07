package com.jminnovatech.sbclub.repository

import android.content.Context
import com.jminnovatech.sbclub.data.api.RetrofitClient
import com.jminnovatech.sbclub.utils.ApiState
import retrofit2.HttpException
import com.jminnovatech.sbclub.data.api.*
import com.jminnovatech.sbclub.data.api.LoginResponse
import com.jminnovatech.sbclub.data.model.LedgerItem
import com.jminnovatech.sbclub.data.model.MessageResponse
import com.jminnovatech.sbclub.data.model.ProfileResponse
import com.jminnovatech.sbclub.data.model.ProfitDay
import com.jminnovatech.sbclub.data.model.ResultWithBetResponse
import com.jminnovatech.sbclub.data.model.RoundItem
import com.jminnovatech.sbclub.data.model.RoundItemUI
import com.jminnovatech.sbclub.data.model.SummaryResponse
import com.jminnovatech.sbclub.data.model.WithdrawItem
import com.jminnovatech.sbclub.data.model.admin.MasterReportResponse
import com.jminnovatech.sbclub.data.model.admin.PendingResponse
import com.jminnovatech.sbclub.data.model.admin.Transaction
import com.jminnovatech.sbclub.data.model.master.MasterUser
import com.jminnovatech.sbclub.data.model.master.TransferRequest

import com.jminnovatech.sbclub.data.model.user.BetItem.BetItem
import com.jminnovatech.sbclub.data.model.user.BetItem.BetRequest
import com.jminnovatech.sbclub.model.UserNew
import com.jminnovatech.sbclub.utils.NetworkErrorHandler
import com.jminnovatech.sbclub.data.model.progame.*

class AppRepository(private val context: Context) {

    private val api = RetrofitClient.getApi(context)

    // 🔐 LOGIN
    suspend fun login(phone:String, pass:String): ApiState<LoginResponse> {
        return try {

            val res = api.login(
                mapOf("phone" to phone, "password" to pass)
            )

            if(res.status){
                ApiState.Success(res)
            }else{
                ApiState.Error("Invalid login")
            }

        } catch (e: HttpException){
            ApiState.Error("Server ${e.code()}")
        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    // 👑 CREATE MASTER
    suspend fun createMaster(name:String, phone:String, pass:String): ApiState<String> {
        return try {

            val res = api.createMaster(
                mapOf(
                    "name" to name,
                    "phone" to phone,
                    "password" to pass
                )
            )

            if(res.status){
                ApiState.Success(res.msg)
            }else{
                ApiState.Error(res.msg)
            }

        } catch (e: Exception){
            e.printStackTrace()   // 🔥 LOG
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    // 📊 PROFIT GRAPH
    suspend fun profitGraph(): ApiState<List<ProfitGraphItem>> {
        return try {
            ApiState.Success(api.profitGraph())
        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    // 🔁 CREATE ROUNDS
    suspend fun createRounds(): ApiState<String> {
        return try {
            val res = api.createRounds()
            ApiState.Success(res.msg)
        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    // 💰 WALLET ADD
    suspend fun walletAdd(userId: Int, amount: Int): ApiState<String> {
        return try {

            val res = api.walletAdd(
                mapOf(
                    "user_id" to userId.toString(),   // ✅ FIX
                    "amount" to amount.toString()     // ✅ FIX
                )
            )

            if(res.status){
                ApiState.Success(res.msg)
            }else{
                ApiState.Error(res.msg)
            }

        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e)?: "Transfer failed")
        }
    }
    // 🤖 AUTO RESULT


    // 🎯 MANUAL RESULT
    suspend fun manualResult(round:Int, number:String): ApiState<String> {
        return try {
            val res = api.manualResult(
                mapOf("round_id" to "$round", "number" to number)
            )
            ApiState.Success(res.msg)
        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    // 🔴🟢🔵 PROFIT REPORT
    suspend fun profit(round:Int): ApiState<ProfitResponse> {
        return try {
            ApiState.Success(api.profit(round, "1no"))
        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }


    suspend fun getRounds(): ApiState<List<RoundItemUI>> {
        return try {

            val completed = api.resultHistory()   // completed
            val running = api.currentRound()      // running

            val list = mutableListOf<RoundItemUI>()

            // ✅ completed
            completed.forEach {
                list.add(
                    RoundItemUI(
                        id = it.id,
                        start_time = it.start_time,
                        result_time = it.result_time,
                        result_number = it.result_number,
                        status = "completed"
                    )
                )
            }

            // ✅ running
            list.add(
                RoundItemUI(
                    id = running.id,
                    start_time = running.start_time,
                    result_time = running.result_time,
                    result_number = null,
                    status = "running"
                )
            )

            ApiState.Success(list)

        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }


    suspend fun currentPreview(): ApiState<CurrentPreviewResponse>{
        return try {
            val res = api.currentRoundPreview()
            ApiState.Success(res)
        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun getUsers(search:String, type:String): ApiState<List<UserNew>>{
        return try{
            val res = api.getUsers(search, type)
            ApiState.Success(res)
        }catch (e:Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    suspend fun getTransactions(): ApiState<List<Transaction>>{
        return try{
            val res = api.getTransactions()
            ApiState.Success(res)
        }catch (e:Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }



    suspend fun getCurrentRound(): ApiState<RoundItem> {
        return try {
            val res = api.currentRound()
            ApiState.Success(res)
        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    suspend fun placeBet(body: BetRequest): ApiState<String> {
        return try {

            val res = api.placeBet(body)

            if (res.status) {
                ApiState.Success(res.msg)
            } else {
                ApiState.Error(res.msg)
            }

        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }



    suspend fun getCurrentBets(): ApiState<List<BetItem>> {
        return try {
            val res = api.currentBets()
            ApiState.Success(res)
        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun getSummary(): ApiState<SummaryResponse> {
        return try {
            val res = api.getSummary()
            println("🔥 API SUCCESS: $res")
            ApiState.Success(res)
        } catch (e: Exception) {
            println("❌ API ERROR: ${e.message}")
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

//    suspend fun getLedger(): ApiState<List<LedgerItem>> {
//        return try {
//            ApiState.Success(api.getLedger())
//        } catch (e: Exception) {
//            ApiState.Error("Ledger error")
//        }
//    }

    suspend fun getUserResults(): ApiState<ResultWithBetResponse> {
        return try {
            ApiState.Success(api.getUserResults())
        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun getLedger(): ApiState<List<LedgerItem>> {
        return try {
            val res = api.getLedger()
            ApiState.Success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    suspend fun getPendingUsers(): ApiState<PendingResponse> {
        return try {
            val res = api.getPendingUsers()
            ApiState.Success(res)
        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun transferPending(): ApiState<String> {
        return try {
            val res = api.transferPending()
            ApiState.Success(res.msg)
        } catch (e: Exception){
            ApiState.Error("Transfer failed")
        }
    }
    suspend fun manualPattiResult(number:String): ApiState<String>{
        return try {
            val res = api.manualPattiResult(
                mapOf("number" to number)
            )
            if(res.status){
                ApiState.Success(res.msg)
            }else{
                ApiState.Error(res.msg)
            }
        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    suspend fun getMyUsers(search:String): ApiState<List<MasterUser>>{
        return try{
            val res = api.myUsers(search)
            ApiState.Success(res.data)
        }catch (e: Exception){
            e.printStackTrace()
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    suspend fun createUser(name:String, phone:String, pass:String): ApiState<String>{
        return try{
            val res = api.createUser(
                mapOf(
                    "name" to name,
                    "phone" to phone,
                    "password" to pass
                )
            )
            if(res.status) ApiState.Success(res.msg)
            else ApiState.Error(res.msg)
        }catch (e: Exception){
            e.printStackTrace()
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun transfer(code: String, amount: Int): ApiState<String> {
        return try {

            val req = TransferRequest(code, amount)

            val res = api.transfer(req)

            if (res.status) {
                ApiState.Success(res.msg)
            } else {
                ApiState.Error(res.msg)
            }

        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    // 💸 WITHDRAW WITH PAYMENT
    suspend fun withdrawWithPayment(
        amount: Double,
        type: String,
        acc: String,
        holder: String,   // ✅ required
        ifsc: String
    ): ApiState<String> {

        return try {

            val res: CommonResponse = api.withdrawWithPayment(
                mapOf(
                    "amount" to amount.toString(),
                    "payment_type" to type,
                    "account_number" to acc,
                    "holder_name" to holder,   // ✅ correct
                    "ifsc_code" to ifsc
                )
            )

            if (res.status) {
                ApiState.Success(res.msg)
            } else {
                ApiState.Error(res.msg)
            }

        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    suspend fun getPendingWithdraws(): ApiState<List<WithdrawItem>> {
        return try {

            val res = api.getPendingWithdraws()

            if(res.status){
                ApiState.Success(res.data)
            }else{
                ApiState.Error("No data")
            }

        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    suspend fun approveWithdraw(id:Int): ApiState<String> {
        return try {

            val res = api.approveWithdraw(id)   // ✅ token remove

            if(res.status){
                ApiState.Success(res.msg)
            }else{
                ApiState.Error(res.msg)
            }

        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun rejectWithdraw(id:Int): ApiState<String> {
        return try {

            val res = api.rejectWithdraw(id)

            if(res.status){
                ApiState.Success(res.msg)
            }else{
                ApiState.Error(res.msg)
            }

        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun getWithdrawHistory(): ApiState<List<WithdrawItem>> {
        return try {
            val res = api.getWithdrawHistory()

            if(res.status){
                ApiState.Success(res.data)
            }else{
                ApiState.Error("No data")
            }

        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }


    suspend fun getProfile(): ApiState<ProfileResponse> {
        return try {
            val res = api.getProfileDetails()
            ApiState.Success(res)
        } catch (e: Exception) {
            ApiState.Error(e.message ?: "Profile load failed")
        }
    }
    suspend fun getProfitList(
        from:String? = null,
        to:String? = null
    ): ApiState<List<ProfitDay>> {

        return try {

            val res = api.getProfitList(from, to)

            if(res.status){
                ApiState.Success(res.data)
            }else{
                ApiState.Error("No data")
            }

        } catch (e: Exception){
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun changePassword(
        oldPass: String,
        newPass: String
    ): ApiState<String> {

        return try {

            val res = api.changePassword(
                mapOf(
                    "old_password" to oldPass,
                    "new_password" to newPass
                )
            )

            if (res.status) {
                ApiState.Success(res.msg)
            } else {
                ApiState.Error(res.msg)
            }

        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }
    suspend fun getAppMessage(): MessageResponse {
        return api.getAppMessage()
    }

    suspend fun updateMessage(message: String): ApiState<String> {
        return try {

            val res = api.updateAppMessage(
                mapOf("message" to message)
            )

            if (res.status) {
                ApiState.Success(res.message)
            } else {
                ApiState.Error(res.message)
            }

        } catch (e: Exception) {
            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

    suspend fun masterTeamReport(
        from: String? = null,
        to: String? = null
    ): MasterReportResponse {

        return RetrofitClient
            .getApi(context)
            .masterTeamReport(from, to)
    }


//pro game start



    suspend fun getSchedules(gameId:Int): ApiState<ScheduleResponse> {

        return try {

            val res = api.getSchedules(gameId)

            ApiState.Success(res)

        } catch (e: Exception){

            ApiState.Error(NetworkErrorHandler.getMessage(e))

        }

    }



    suspend fun placeProBet(
        body: ProBetRequest
    ): ApiState<String> {

        return try {

            val res = api.placeProBet(body)
            android.util.Log.d("PRO_BET", "status=${res.status}, msg=${res.msg}")
            if(res.status){

                ApiState.Success(res.msg)

            }else{

                ApiState.Error(res.msg)

            }

        } catch (e: Exception){

            ApiState.Error(NetworkErrorHandler.getMessage(e))

        }

    }



    suspend fun gameResults(
        gameId:Int
    ): ApiState<ResultResponse> {

        return try {

            val res = api.getProResults(gameId)

            ApiState.Success(res)

        } catch (e: Exception){

            ApiState.Error(NetworkErrorHandler.getMessage(e))

        }

    }

    suspend fun getProGames(): ApiState<List<Game>> {

        return try {

            val res = api.getGames()

            if (res.status) {
                ApiState.Success(res.data)
            } else {
                ApiState.Error("Game not found")
            }

        } catch (e: Exception) {

            e.printStackTrace()

            ApiState.Error(NetworkErrorHandler.getMessage(e))

        }

    }

    suspend fun getProSchedules(

        gameId: Int

    ): ApiState<ScheduleResponse> {

        return try {

            val res = api.getSchedules(gameId)

            if (res.status) {
                ApiState.Success(res)
            } else {
                ApiState.Error("Schedule not found")
            }

        } catch (e: Exception) {

            e.printStackTrace()

            android.util.Log.e("PRO_GAME_ERROR", e.stackTraceToString())

            ApiState.Error(e.message ?: "Unknown Error")
        }

    }

    suspend fun getCurrentProSchedule(

        gameId: Int

    ): ApiState<CurrentScheduleResponse> {

        return try {

            val res = api.getCurrentSchedule(gameId)

            if (res.status) {
                ApiState.Success(res)
            } else {
                ApiState.Error("Game Closed")
            }

        } catch (e: Exception) {

            e.printStackTrace()

            ApiState.Error(NetworkErrorHandler.getMessage(e))

        }

    }



    suspend fun getProHistory(

        gameId: Int,

        scheduleId: Int? = null

    ): ApiState<HistoryResponse> {

        return try {

            val res = api.getProHistory(

                gameId,

                scheduleId

            )

            if (res.status) {

                ApiState.Success(res)

            } else {

                ApiState.Error("No History")

            }

        } catch (e: Exception) {

            e.printStackTrace()

            ApiState.Error(NetworkErrorHandler.getMessage(e))


        }

    }

    suspend fun getProResults(
        gameId: Int
    ): ApiState<ResultResponse> {

        return try {

            val res = api.getProResults(gameId)

            if (res.status) {

                ApiState.Success(res)

            } else {

                ApiState.Error("No Result")

            }

        } catch (e: Exception) {

            e.printStackTrace()

            ApiState.Error(NetworkErrorHandler.getMessage(e))

        }

    }

    suspend fun getBetHistory(
        gameId: Int,
        scheduleId: Int
    ): ApiState<BetHistoryResponse> {

        return try {

            val res = api.getBetHistory(gameId, scheduleId)

            android.util.Log.d("BET_HISTORY", "status=${res.status}")
            android.util.Log.d("BET_HISTORY", "data=${res.data}")

            if (res.status) {
                ApiState.Success(res)
            } else {
                ApiState.Error("No Bet History")
            }

        } catch (e: Exception) {

            android.util.Log.e(
                "BET_HISTORY",
                e.stackTraceToString()
            )

            ApiState.Error(NetworkErrorHandler.getMessage(e))
        }
    }

}

