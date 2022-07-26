package com.company.singlealbumapp.repository

import com.company.singlealbumapp.dto.Album

interface MediaRepository {
suspend fun getAlbum(): Album
}