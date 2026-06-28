package com.jminnovatech.sbclub.data.model.admin

import com.jminnovatech.sbclub.model.UserNew

data class Transaction(
    val id: Int,
    val user_id: Int,
    val amount: Double,
    val type: String,
    val remark: String,
    val created_at: String,
    val user: UserNew? // 🔥 IMPORTANT (for name)
)

data class PendingUser(
    val user_id: String,
    val name: String,
    val phone: String,
    val pending_amount: Double,
    val available_amount: Double
)

data class PendingResponse(
    val status: Boolean,
    val total_pending: Double,
    val total_users: Int,
    val data: List<PendingUser>
)