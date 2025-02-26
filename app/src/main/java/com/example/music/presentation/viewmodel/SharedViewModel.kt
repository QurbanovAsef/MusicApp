package com.example.music.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.Playlist
import com.example.music.data.model.response.TrackResponse
import com.example.music.data.retrofit.RetrofitInstance
import com.example.music.data.service.MusicApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    private val _playerTracks = MutableLiveData<List<TrackResponse>>(emptyList())
    val playerTracks: LiveData<List<TrackResponse>> get() = _playerTracks

    private val _loadingTracks = MutableLiveData<Boolean>(false)
    val loadingTracks: LiveData<Boolean> get() = _loadingTracks

    private val _searchResults = MutableLiveData<List<TrackResponse>>()
    val searchResults: LiveData<List<TrackResponse>> = _searchResults

    private val _playlistsFlow = MutableStateFlow<List<Playlist>?>(null)
    val playlistsFlow = _playlistsFlow.asStateFlow()

    private val musicApiService: MusicApiService = RetrofitInstance.api

    private fun playlistApiCall() {
        viewModelScope.launch {
            try {
                val response = musicApiService.getPlaylists()
                if (response.isSuccessful) {
                    val playlists = response.body()?.playlists.orEmpty()
                    _playlistsFlow.emit(playlists)
                    getPlaylistDetailsBySlug(playlists.firstOrNull()?.slug)
                } else {
                    Log.e("SharedViewModel", "API Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error: ${e.message}")
            }
        }
    }

    fun getPlaylists(force: Boolean = true) {
        if (force || playlistsFlow.value == null) {
            playlistApiCall()
        }
    }

    fun searchSongs(query: String) {
        viewModelScope.launch {
            try {
                val response = musicApiService.searchExactShows(query)
                if (response.isSuccessful) {
                    _searchResults.postValue(response.body()?.tracks ?: emptyList())
                } else {
                    Log.e("Search", "Xəta: ${response.message()}")
                    _searchResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("Search", "İstisna: ${e.message}")
                _searchResults.postValue(emptyList())
            }
        }
    }

    fun getPlaylistDetailsBySlug(slug: String?) {
        if (slug == null) return
        viewModelScope.launch {
            _playerTracks.postValue(emptyList())
            _loadingTracks.postValue(true)

            try {
                val response = musicApiService.getPlaylistDetails(slug)
                _playerTracks.postValue(response.body()?.entries?.mapNotNull { it.track } ?: emptyList())
            } catch (e: Exception) {
                _playerTracks.postValue(emptyList())
            } finally {
                _loadingTracks.postValue(false)
            }
        }
    }
}
