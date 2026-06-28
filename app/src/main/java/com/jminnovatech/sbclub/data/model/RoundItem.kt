package com.jminnovatech.sbclub.data.model

data class RoundItem(
    val id: Int,
    val start_time: String,
    val end_time: String,
    val result_time: String,
    val result_number: String,
    val wallet_balance: String?,      // ✅ ADD
    val end_time_millis: Long,
    val bet_open: Boolean,

)