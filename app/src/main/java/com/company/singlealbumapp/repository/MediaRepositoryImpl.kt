package com.company.singlealbumapp.repository

import com.company.singlealbumapp.api.MediaApiService
import com.company.singlealbumapp.dto.Album
import com.company.singlealbumapp.error.ApiError
import com.company.singlealbumapp.error.NetworkError
import com.company.singlealbumapp.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(val apiService: MediaApiService) : MediaRepository {

    override suspend fun getAlbum(): Album {
        try {
            val response = apiService.getAlbum()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}