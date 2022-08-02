package com.company.singlealbumapp.dto

data class Track(
    val id: Int,
    val file: String,
    var isPlaying: Boolean = false,
    var albumName: String? = null
)
