package com.jminnovatech.sbclub.data.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.jminnovatech.sbclub.utils.SessionManager

object RetrofitClient {

    private const val BASE_URL = "https://sbclub.live/sf/public/api/"

    fun getApi(context: Context): ApiService {

        val session = SessionManager(context)

        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->

                val token = session.getToken()

                val request = chain.request().newBuilder()

                if(token != null){
                    request.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(request.build())
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}