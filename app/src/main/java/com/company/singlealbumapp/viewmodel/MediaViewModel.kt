package com.company.singlealbumapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.singlealbumapp.dto.Album
import com.company.singlealbumapp.dto.Track
import com.company.singlealbumapp.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaViewModel @Inject constructor(private val repository: MediaRepository) : ViewModel() {

    val albumData: MutableLiveData<Album> = MutableLiveData<Album>()

    val trackData: MutableLiveData<Track> = MutableLiveData<Track>()

    init {
        loadAlbum()
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            try {
                albumData.value = repository.getAlbum()
            } catch (e: Exception) {

            }
        }
    }

    fun playTrack(track: Track) {
        trackData.value = Track(track.id, track.file, isPlaying = true)
    }

    fun pauseTrack(track: Track) {
        trackData.value = Track(track.id, track.file, isPlaying = false)
    }
}