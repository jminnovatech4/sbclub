package com.jminnovatech.sbclub.data.model.admin.game

// =============================
// Dashboard
// =============================

data class AdminDashboardResponse(
    val status: Boolean,
    val summary: AdminDashboardSummary,
    val games: List<AdminGameItem>
)

data class AdminDashboardSummary(
    val games: Int,
    val today_bet: Double,
    val today_win: Double,
    val running_games: Int
)

// =============================
// Games
// =============================

data class AdminGamesResponse(
    val status: Boolean,
    val games: List<AdminGameItem>
)

data class AdminGameItem(
    val id: Int,
    val game_name: String,
    val game_code: String,
    val digit_length: Int,
    val display_order: Int,
    val is_active: Int
)

// =============================
// Schedule
// =============================

data class AdminScheduleResponse(
    val status: Boolean,
    val data: List<AdminScheduleItem>
)

data class AdminScheduleItem(
    val id: Int,
    val game_id: Int,
    val baji_no: Int,
    val title: String,
    val start_time: String,
    val end_time: String,
    val result_time: String,
    val result_number: String?,

    val declared_at: String?,
    val status: String,
    val is_active: Int
)

// =============================
// Rate
// =============================

data class AdminRateResponse(
    val status: Boolean,
    val data: List<AdminRateItem>
)

data class AdminRateItem(
    val id: Int,
    val game_id: Int,
    val win_multiplier: Double,
    val is_active: Int
)

// =============================
// Common Response
// =============================

data class AdminGameResponse(
    val status: Boolean,
    val msg: String
)

// =============================
// Requests
// =============================

data class UpdateScheduleRequest(
    val schedule_id: Int,
    val title: String,
    val start_time: String,
    val end_time: String,
    val result_time: String,
    val status: String,
    val is_active: Boolean
)

data class UpdateRateRequest(
    val rate_id: Int,
    val win_multiplier: Double,
    val is_active: Boolean
)

data class PublishResultRequest(
    val game_id: Int,
    val schedule_id: Int,
    val result_number: String
)

data class ResultReportResponse(

    val status: Boolean,

    val summary: ResultSummary,

    val preview: ResultPreview?

)

data class ResultSummary(

    val total_bet: Double,

    val total_players: Int,

    val win_multiplier: Double

)

data class ResultPreview(

    val number: String,

    val bet_amount: Double,

    val players: Int,

    val payable: Double,

    val profit: Double,

    val win_multiplier: Double

)



data class ResultPanelResponse(
    val status: Boolean,
    val current_schedule: CurrentSchedule?,
    val games: List<ResultGame>,
    val history: List<ScheduleHistory>
)

data class CurrentSchedule(
    val id: Int,
    val baji_no: Int,
    val title: String?,
    val start_time: String,
    val end_time: String,
    val result_time: String,
    val status: String
)

data class ResultGame(
    val id: Int,
    val game_name: String,
    val game_code: String,
    val digit_length: Int,
    val display_order: Int,
    val result_number: String
)

data class ScheduleHistory(
    val id: Int,
    val baji_no: Int,
    val title: String?,
    val start_time: String,
    val end_time: String,
    val result_time: String,
    val status: String,
    val results: List<HistoryResult>
)

data class HistoryResult(
    val id: Int,
    val game_name: String,
    val game_code: String,
    val result_number: String?,
    val declared_at: String?
)

data class ResultPreviewResponse(
    val status: Boolean,
    val preview: PreviewData
)

data class PreviewData(
    val number: String,
    val bet_amount: Double,
    val players: Int,
    val payable: Double,
    val profit: Double
)
data class PublishAllRequest(
    val schedule_id: Int,
    val results: List<PublishGameResult>
)

data class PublishGameResult(
    val game_id: Int,
    val result_number: String
)
data class ResultPreviewRequest(
    val game_id: Int,
    val schedule_id: Int,
    val result_number: String
)