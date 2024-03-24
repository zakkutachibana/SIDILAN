package com.zak.sidilan.data.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {

    private const val BASE_URL = "https://www.googleapis.com/books/v1/"
    private const val API_KEY = "YOUR_API_KEY_HERE"

    private val client = OkHttpClient.Builder()
        .addInterceptor(getApiKeyInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val instance: GoogleBooksApiService = retrofit.create(GoogleBooksApiService::class.java)

    private fun getApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val originalUrl = original.url

            val url = originalUrl.newBuilder()
                .addQueryParameter("key", API_KEY)
                .build()

            val requestBuilder = original.newBuilder().url(url)
            val request = requestBuilder.build()

            chain.proceed(request)
        }
    }
}

