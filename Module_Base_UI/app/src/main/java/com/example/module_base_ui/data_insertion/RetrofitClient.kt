package com.example.module_base_ui.data_insertion

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Verified URL: Ensure no hidden characters or typos
    private const val BASE_URL = "https://kampus-life-1ajs.onrender.com/api/"

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .connectTimeout(50, TimeUnit.SECONDS)
        .readTimeout(50, TimeUnit.SECONDS)
        .writeTimeout(50, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Accept", "application/json").build()
            chain.proceed(request)
        }.build()

    val api: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}