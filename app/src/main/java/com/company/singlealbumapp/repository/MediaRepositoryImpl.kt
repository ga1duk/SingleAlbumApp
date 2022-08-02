package com.company.singlealbumapp.repository

import com.company.singlealbumapp.BuildConfig.BASE_URL
import com.company.singlealbumapp.dto.Album
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(private val client: OkHttpClient) : MediaRepository {

    private val gson = Gson()

    private val typeToken = object : TypeToken<Album>() {}

    override suspend fun getAlbum(): Album {
        lateinit var album: Album
        withContext(Dispatchers.IO) {
            runCatching {
                val request: Request = Request.Builder()
                    .url("$BASE_URL/album.json")
                    .build()
                album = client.newCall(request)
                    .execute()
                    .let { it.body?.string() ?: throw RuntimeException("body is null") }
                    .let {
                        gson.fromJson(it, typeToken.type)
                    }
            }
        }
        return album
    }
}