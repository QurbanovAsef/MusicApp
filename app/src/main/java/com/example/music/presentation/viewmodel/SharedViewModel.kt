package com.example.music.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.FavoriteTrack
import com.example.music.data.model.response.Playlist
import com.example.music.data.model.response.TrackResponse
import com.example.music.data.retrofit.RetrofitInstance
import com.example.music.data.service.AppDatabase
import com.example.music.data.service.FavoriteTrackDao
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

    private val _searchResults = MutableLiveData<List<TrackResponse>>()
    val searchResults: LiveData<List<TrackResponse>> = _searchResults
    private val musicApiService: MusicApiService = RetrofitInstance.api


    private val _playlistsFlow = MutableStateFlow<List<Playlist>?>(null)
    val playlistsFlow = _playlistsFlow.asStateFlow()

    private fun playlistApiCall() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPlaylists()
                if (response.isSuccessful) {
                    val playlists = response.body()?.playlists.orEmpty()
                    _playlistsFlow.emit(playlists)
                    getPlaylistDetailsBySlug(playlists[0].slug)
                } else {
                    Log.e("SharedViewModel", "API Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error: ${e.message}")
            }
        }
    }

    fun getPlaylists(force: Boolean = true) {
        if (force)
            playlistApiCall()
        else if (playlistsFlow.value == null)
            playlistApiCall()
    }


    // Axtarış funksiyası
    fun searchSongs(query: String) {
        viewModelScope.launch {
            try {
                val response = musicApiService.searchExactShows(query)
                if (response.isSuccessful) {
                    // Cavab alındıqda success handler
                    val tracks = response.body()?.tracks
                    _searchResults.postValue(tracks ?: listOf())
                } else {
                    // Əgər API-dən səhv cavab alınıbsa
                    Log.e("Search", "Xəta: ${response.message()}")
                    _searchResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                // Xəta baş verdikdə
                Log.e("Search", "İstisna: ${e.message}")
                _searchResults.postValue(emptyList())
            }
        }
    }


    fun setPlayerTracks(tracks: List<TrackResponse>) {
        if (_playerTracks.value != tracks)
            _playerTracks.postValue(tracks)
    }

    fun getPlaylistDetailsBySlug(
        slug: String?,
        activeTrack: TrackResponse? = null,
    ) = slug?.let {
        viewModelScope.launch {
            try {
                val response = musicApiService.getPlaylistDetails(slug)
                if (response.isSuccessful) {
                    _playerTracks.postValue(response.body()?.entries?.filterNot { it.track == null }
                        ?.map { it.track!! } ?: listOf())
                } else {
                    _playerTracks.postValue(emptyList())
                }
            } catch (e: Exception) {
                _searchResults.postValue(emptyList())
            }
        }
    }
}