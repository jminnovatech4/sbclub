package com.jminnovatech.sbclub.data.model

data class WithdrawItem(
    val id: Int,
    val user_id: Int,
    val amount: Double,
    val status: String,
    val requested_to: Int,
    val created_at: String,
    // 🔥 ADD THESE
    val payment_type: String?,
    val account_number: String?,
    val ifsc_code: String?,
    val holder_name: String?,

    val user_name: String? = null
)


data class WithdrawResponse(
    val status: Boolean,
    val data: List<WithdrawItem>
)
