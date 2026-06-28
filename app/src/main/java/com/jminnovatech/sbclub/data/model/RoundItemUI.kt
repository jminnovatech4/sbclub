package com.jminnovatech.sbclub.data.model

data class RoundItemUI(
    val id: Int,
    val start_time: String,
    val result_time: String,
    val result_number: String?,
    val status: String   // 🔥 MUST
)