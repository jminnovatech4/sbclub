package com.jminnovatech.sbclub.utils

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

object NetworkErrorHandler {

    fun getMessage(e: Exception): String {

        return when (e) {

            is SocketTimeoutException -> "📡 Slow internet connection"
            is UnknownHostException -> "📡 No internet connection"
            is HttpException -> "⚠ Server error (${e.code()})"

            else -> "Something went wrong"
        }
    }
}