package com.jminnovatech.sbclub.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    // 🔥 OLD METHOD (keep for backward compatibility)
    fun save(token: String, role: String) {
        prefs.edit()
            .putString("token", token)
            .putString("role", role)
            .apply()
    }

    // 🔥 NEW METHOD (full user data)
    fun saveUser(
        token: String,
        role: String,
        userId: String,
        name: String,
        balance: String
    ) {
        prefs.edit()
            .putString("token", token)
            .putString("role", role)
            .putString("user_id", userId)
            .putString("name", name)
            .putString("balance", balance)
            .apply()
    }

    fun getToken(): String? = prefs.getString("token", null)
    fun getRole(): String? = prefs.getString("role", null)
    fun getUserId(): String? = prefs.getString("user_id", "0")
    fun getName(): String? = prefs.getString("name", "User")
    fun getBalance(): String? = prefs.getString("balance", "0")

    fun updateBalance(balance: String) {
        prefs.edit().putString("balance", balance).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}