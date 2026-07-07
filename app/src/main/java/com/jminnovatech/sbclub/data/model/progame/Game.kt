package com.jminnovatech.sbclub.data.model.progame



data class Game(

    val id:Int,

    val game_name:String,

    val game_code:String,

    val digit_length:Int,

    val min_bet:Double,

    val max_bet:Double,

    val max_entries:Int,

    val allow_multiple:Int,

    val is_active:Int,

    val display_order:Int

)

data class GameListResponse(

    val status:Boolean,

    val data:List<Game>

)



data class Schedule(

    val id:Int,

    val game_id:Int,

    val baji_no:Int,

    val title:String?,

    val start_time:String,

    val end_time:String,

    val result_time:String,

    val status:String,

    val is_active:Int

)



data class ScheduleResponse(

    val status:Boolean,

    val game:Game,

    val cards:List<Schedule>

)



data class CurrentScheduleResponse(

    val status:Boolean,

    val data:Schedule?

)



data class ProBetItem(

    val number:String,

    val amount:Double

)



data class ProBetRequest(

    val game_id:Int,

    val schedule_id:Int,

    val bets:List<ProBetItem>

)

data class BetHistoryResponse(
    val status: Boolean,
    val data: List<BetHistory>
)

data class BetHistory(
    val id: Int,
    val status: String,
    val total_amount: Double,
    val items: List<BetHistoryItem>
)

data class BetHistoryItem(
    val bet_number: String,
    val amount: Double,
    val win_amount: Double
)
data class HistoryItem(

    val id:Int,

    val game_name:String,

    val baji_no:Int,

    val title:String?,

    val start_time:String,

    val end_time:String,

    val total_amount:Double,

    val win_amount:Double,

    val status:String,

    val result_number:String?,

    val created_at:String

)



data class HistoryResponse(

    val status:Boolean,

    val data:HistoryPage

)



data class HistoryPage(

    val current_page:Int,

    val data:List<HistoryItem>,

    val last_page:Int,

    val total:Int

)



data class ResultItem(

    val id:Int,

    val result_number:String,

    val baji_no:Int,

    val title:String?,

    val start_time:String,

    val end_time:String,

    val declared_at:String

)



data class ResultResponse(

    val status:Boolean,

    val data:ResultPage

)


data class ResultPage(

    val current_page:Int,

    val data:List<ResultItem>,

    val last_page:Int,

    val total:Int

)