package com.company.singlealbumapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.singlealbumapp.dto.Album
import com.company.singlealbumapp.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(private val repository: MediaRepository) : ViewModel() {

    val data: MutableLiveData<Album> = MutableLiveData<Album>()

    init {
        loadAlbum()
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            try {
                data.value = repository.getAlbum()
            } catch (e: Exception) {

            }
        }
    }
}