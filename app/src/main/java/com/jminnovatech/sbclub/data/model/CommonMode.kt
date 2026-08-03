package com.jminnovatech.sbclub.data.model

import com.jminnovatech.sbclub.data.api.User

data class SummaryResponse(
    val balance: Double,
    val total_credit: Double,
    val total_debit: Double,
    val profit_loss: Double,
    val today_profit: Double,
    val total_bet: Double,
    val total_win: Double,
    val win_count: Int,
    val bet_count: Int,
    val avg_bet: Double
)
data class LedgerItem(
    val date: String,
    val remark: String,
    val user_name: String,
    val credit: Double,
    val debit: Double,
    val balance: Double?
)
data class ResultWithBetResponse(
    val status: Boolean,
    val data: List<ResultData>
)

data class ResultData(
    val round_id: Int,
    val result: String,
    val result_time: String,

    val normal_total_bet: Double,
    val normal_total_win: Double,
    val normal_profit: Double,
    val normal_status: String,

    val patti_result: String?,
    val patti_total_bet: Double,
    val patti_total_win: Double,
    val patti_profit: Double,
    val patti_status: String,

    val my_bets: List<MyBet>
)

data class MyBet(
    val number: String,
    val amount: String
)

data class TransactionResponse(
    val status: Boolean,
    val data: List<LedgerItem>,
    val pagination: Pagination
)

data class Pagination(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)
data class TransactionItem(
    val id: Int,
    val date: String,
    val remark: String,
    val credit: Double,
    val debit: Double,
    val balance: Double
)


data class ProfileResponse(
    val status: Boolean,
    val user: User,
    val wallet: String,
    val payment: Payment?
)

data class Payment(
    val payment_type: String?,
    val account_number: String?,
    val ifsc_code: String?,
    val holder_name: String?
)

data class ProfitListResponse(
    val status: Boolean,
    val data: List<ProfitDay>
)

data class ProfitDay(
    val date: String,
    val raw_date: String,
    val collection: Double,
    val payout: Double,
    val profit: Double,
    val games: List<ProfitGame>
)

data class ProfitGame(
    val time: String,
    val collection: Double,
    val payout: Double,
    val profit: Double
)

data class MessageResponse(

    val status: Boolean,

    val message: String,

    val upi_name: String = "",

    val upi_id: String = "",

    val qr_image: String = ""

)