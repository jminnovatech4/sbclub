package com.jminnovatech.sbclub.utils

sealed class ApiState<out T> {

    object Loading : ApiState<Nothing>()

    data class Success<T>(val data: T) : ApiState<T>()

    data class Error(val message: String) : ApiState<Nothing>()
    object Idle : ApiState<Nothing>()
}

