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