package com.jminnovatech.sbclub.utils

import okhttp3.Interceptor
import okhttp3.Response

class RetryInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var tryCount = 0
        val maxTry = 2
        var response: Response

        while (true) {
            try {
                response = chain.proceed(chain.request())
                return response
            } catch (e: Exception) {

                if (++tryCount > maxTry) throw e
            }
        }
    }
}