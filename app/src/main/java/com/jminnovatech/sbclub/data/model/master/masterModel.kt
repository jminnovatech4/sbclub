package com.jminnovatech.sbclub.data.model.master

data class MyUsersResponse(
    val status:Boolean,
    val total:Int,
    val data: List<MasterUser>
)

data class MasterUser(
    val id: Int,
    val name: String,
    val phone: String,
    val user_code: String?,   // ✅ MUST NULLABLE
    val balance: Double,
    val created_at: String
)
data class TransferRequest(
    val code: String,
    val amount: Int
)