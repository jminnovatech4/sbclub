package com.jminnovatech.sbclub.data.api

data class LoginResponse(
    val status: Boolean,
    val token: String,
    val role: String,
    val user: User,
    val balance: String,
    val user_code: Int,
)
data class User(
    val id: Int,
    val name: String,
    val user_code: Int,

)