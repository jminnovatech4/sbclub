package com.jminnovatech.sbclub.data.api

import com.jminnovatech.sbclub.data.model.DepositRequest
import com.jminnovatech.sbclub.data.model.DepositResponse
import com.jminnovatech.sbclub.data.model.LedgerItem
import com.jminnovatech.sbclub.data.model.MessageResponse
import com.jminnovatech.sbclub.data.model.ProfileResponse
import com.jminnovatech.sbclub.data.model.ProfitListResponse
import com.jminnovatech.sbclub.data.model.ResultWithBetResponse
import com.jminnovatech.sbclub.data.model.RoundItem
import com.jminnovatech.sbclub.data.model.SummaryResponse
import com.jminnovatech.sbclub.data.model.WithdrawResponse
import com.jminnovatech.sbclub.data.model.admin.MasterReportResponse
import com.jminnovatech.sbclub.data.model.admin.PendingResponse
import com.jminnovatech.sbclub.data.model.admin.Transaction
import com.jminnovatech.sbclub.data.model.admin.game.AdminGameResponse
import com.jminnovatech.sbclub.data.model.admin.game.AdminScheduleResponse
import com.jminnovatech.sbclub.data.model.admin.game.*
import com.jminnovatech.sbclub.data.model.admin.game.PublishResultRequest

import com.jminnovatech.sbclub.data.model.admin.game.UpdateRateRequest
import com.jminnovatech.sbclub.data.model.admin.game.UpdateScheduleRequest
import com.jminnovatech.sbclub.data.model.master.MyUsersResponse
import com.jminnovatech.sbclub.data.model.master.TransferRequest
import com.jminnovatech.sbclub.data.model.progame.*
import com.jminnovatech.sbclub.data.model.user.BetItem.BetItem
import com.jminnovatech.sbclub.data.model.user.BetItem.BetRequest

import com.jminnovatech.sbclub.model.UserNew
import retrofit2.http.*

data class CommonResponse(val status:Boolean, val msg:String)

data class ProfitGraphItem(
    val round_id:Int,
    val collection:Int,
    val payout:Int,
    val profit:Int
)

data class NumberReport(
    val number:Int,
    val total_users:Int,
    val total_bet:Int,
    val payout:Int,
    val color:String
)

data class ProfitResponse(
    val total_collection:Int,
    val numbers: List<NumberReport>
)

data class ResultItem(
    val id:Int,
    val result_number:String,
    val start_time:String,
    val result_time:String,
    val patti_result_number:String?,
)

interface ApiService {
    @POST("login")
    suspend fun login(@Body body: Map<String, String>): LoginResponse
    @POST("admin/create-master")
    suspend fun createMaster(@Body body: Map<String,String>): CommonResponse

    @GET("admin/profit-graph")
    suspend fun profitGraph(): List<ProfitGraphItem>

    @GET("admin/create-rounds")
    suspend fun createRounds(): CommonResponse

    @POST("admin/wallet-add")
    suspend fun walletAdd(
        @Body body: Map<String, String>
    ): CommonResponse



    @POST("admin/manual-result")
    suspend fun manualResult(@Body body: Map<String,String>): CommonResponse

    @GET("admin/profit")
    suspend fun profit(
        @Query("round_id") round_id:Int,
        @Query("game_type") game_type:String
    ): ProfitResponse

    @GET("results")
    suspend fun results(): List<ResultItem>
    @GET("results")
    suspend fun resultHistory(): List<RoundItem>



    @GET("admin/current-round-preview")
    suspend fun currentRoundPreview(): CurrentPreviewResponse

    @GET("admin/users")
    suspend fun getUsers(
        @Query("search") search:String="",
        @Query("type") type:String=""
    ): List<UserNew>

    @GET("admin/transactions")
    suspend fun getTransactions(): List<Transaction>
    @POST("bet/multi")
    suspend fun placeBet(@Body body: BetRequest): CommonResponse

    @GET("user/current-round")
    suspend fun currentRound(): RoundItem
    @GET("user/current-bets")
    suspend fun currentBets(): List<BetItem>
    @GET("transactions-summary")
    suspend fun getSummary(): SummaryResponse

    @GET("transactions")
    suspend fun getLedger(): List<LedgerItem>

    @GET("user/result-with-bets")
    suspend fun getUserResults(): ResultWithBetResponse
    @GET("admin/pending-users")
    suspend fun getPendingUsers(): PendingResponse

    @POST("admin/transfer-pending")
    suspend fun transferPending(): CommonResponse

    @POST("admin/manual-patti-result")
    suspend fun manualPattiResult(@Body body: Map<String,String>): CommonResponse
    @GET("master/my-users")
    suspend fun myUsers(
        @Query("search") search:String = ""
    ): MyUsersResponse

    @POST("master/create-user")
    suspend fun createUser(@Body body: Map<String,String>): CommonResponse

    @POST("master/transfer")
    suspend fun transfer(@Body body: TransferRequest): CommonResponse
    // 🔥 Withdraw with payment
    @POST("withdraw-with-payment")
    suspend fun withdrawWithPayment(
        @Body body: Map<String, String>
    ): CommonResponse   // ✅ MUST ADD
    // 🔥 Pending list (master/admin)
    @GET("withdraw/pending")
    suspend fun getPendingWithdraws(): WithdrawResponse

    // 🔥 Approve
    @POST("withdraw/approve/{id}")
    suspend fun approveWithdraw(
        @Path("id") id: Int
    ): CommonResponse

    @POST("withdraw/reject/{id}")
    suspend fun rejectWithdraw(
        @Path("id") id: Int
    ): CommonResponse
    @GET("withdraw/history")
    suspend fun getWithdrawHistory(): WithdrawResponse

    @GET("profiledetails")
    suspend fun getProfileDetails(): ProfileResponse

    @GET("admin/profitlist")
    suspend fun getProfitList(
        @Query("from") from:String? = null,
        @Query("to") to:String? = null
    ): ProfitListResponse

    @POST("change-password")
    suspend fun changePassword(
        @Body body: Map<String, String>
    ): CommonResponse

    @GET("app-message")
    suspend fun getAppMessage(): MessageResponse

    @POST("admin/update-message")
    suspend fun updateAppMessage(
        @Body body: Map<String, String>
    ): MessageResponse
    @GET("admin/team-report")
    suspend fun masterTeamReport(
        @Query("from") from: String?,
        @Query("to") to: String?
    ): MasterReportResponse

    /*
|--------------------------------------------------------------------------
| PRO GAME
|--------------------------------------------------------------------------
*/

    @GET("pro-game/games")
    suspend fun getGames(): GameListResponse

    @GET("pro-game/games/{gameId}/schedules")
    suspend fun getSchedules(
        @Path("gameId") gameId:Int
    ): ScheduleResponse

    @GET("pro-game/games/{gameId}/current")
    suspend fun getCurrentSchedule(
        @Path("gameId") gameId:Int
    ): CurrentScheduleResponse

    @POST("pro-game/place-bet")
    suspend fun placeProBet(
        @Body body: ProBetRequest
    ): CommonResponse





    // ==============================
// PRO GAME
// ==============================




    @GET("pro-game/history")
    suspend fun getProHistory(

        @Query("game_id")
        gameId:Int,

        @Query("schedule_id")
        scheduleId:Int? = null,

        @Query("page")
        page:Int = 1

    ): HistoryResponse

    @GET("pro-game/results")
    suspend fun getProResults(

        @Query("game_id")
        gameId:Int,

        @Query("page")
        page:Int = 1

    ): ResultResponse


    @GET("pro-game/bethistory")
    suspend fun getBetHistory(
        @Query("game_id") gameId: Int,
        @Query("schedule_id") scheduleId: Int
    ): BetHistoryResponse

    @GET("running-bets")
    suspend fun runningBets(

        @Query("game_id") gameId: Int,

        @Query("schedule_id") scheduleId: Int

    ): RunningBetResponse

// ==============================
// ADMIN GAME
// ==============================

    @GET("admin/game/dashboard")
    suspend fun adminGameDashboard(): AdminDashboardResponse

    @GET("admin/game/games")
    suspend fun adminGames(): AdminGamesResponse

    @GET("admin/game/schedules")
    suspend fun adminSchedules(
        @Query("game_id") gameId: Int
    ): AdminScheduleResponse

    @GET("admin/game/rates")
    suspend fun adminRates(
        @Query("game_id") gameId: Int
    ): AdminRateResponse

    @POST("admin/game/update-schedule")
    suspend fun updateSchedule(
        @Body body:UpdateScheduleRequest
    ): AdminGameResponse

    @POST("admin/game/update-rate")
    suspend fun updateRate(
        @Body body:UpdateRateRequest
    ): AdminGameResponse

    @POST("admin/game/result")
    suspend fun publishResult(
        @Body body:PublishResultRequest
    ): AdminGameResponse
    @GET("admin/game/result-report")
    suspend fun resultReport(

        @Query("game_id")
        gameId: Int,

        @Query("schedule_id")
        scheduleId: Int,

        @Query("number")
        number: String? = null

    ): ResultReportResponse

    // =======================================
// RESULT PANEL
// =======================================

    @GET("admin/game/result-panel")
    suspend fun resultPanel(): ResultPanelResponse

// =======================================
// RESULT PREVIEW
// =======================================

    @POST("admin/game/result-preview")
    suspend fun resultPreview(

        @Body request: ResultPreviewRequest

    ): ResultPreviewResponse

// =======================================
// PUBLISH ALL
// =======================================

    @POST("admin/game/publish-all")
    suspend fun publishAll(

        @Body request: PublishAllRequest

    ): MessageResponse

    @POST("wallet/request")
    suspend fun depositRequest(
        @Body body: DepositRequest
    ): DepositResponse
}