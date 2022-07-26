package com.company.singlealbumapp.api

import com.company.singlealbumapp.BuildConfig.BASE_URL
import com.company.singlealbumapp.dto.Album
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface MediaApiService {
    @GET("album.json")
    suspend fun getAlbum(): Album
}


object MediaApi {
    val retrofitService: MediaApiService by lazy {
        retrofit.create(MediaApiService::class.java)
    }
}