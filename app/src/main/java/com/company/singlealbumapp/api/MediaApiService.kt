package com.company.singlealbumapp.api

import com.company.singlealbumapp.dto.Album
import retrofit2.http.GET

interface MediaApiService {
    @GET("album.json")
    suspend fun getAlbum(): Album
}