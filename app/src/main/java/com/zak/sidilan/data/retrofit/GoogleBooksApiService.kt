package com.zak.sidilan.data.retrofit

import com.zak.sidilan.data.entities.GoogleBooksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApiService {

    @GET("volumes")
    fun getBookByIsbn(
        @Query("q") isbn: String,
        @Query("key") apiKey: String
    ): Call<GoogleBooksResponse>

}