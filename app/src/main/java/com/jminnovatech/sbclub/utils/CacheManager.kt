package com.jminnovatech.sbclub.utils

import android.content.Context

class CacheManager(context: Context) {

    private val prefs = context.getSharedPreferences("cache", Context.MODE_PRIVATE)

    fun save(key:String, value:String){
        prefs.edit().putString(key, value).apply()
    }

    fun get(key:String): String? = prefs.getString(key, null)
}