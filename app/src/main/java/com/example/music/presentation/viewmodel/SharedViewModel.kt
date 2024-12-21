package com.example.music.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.Playlist
import com.example.music.data.model.response.Show
import com.example.music.data.model.response.TrackResponse
import com.example.music.data.retrofit.RetrofitInstance
import com.example.music.data.service.MusicApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SharedViewModel : ViewModel() {

    private val _playerTracks = MutableLiveData<List<TrackResponse>>(emptyList())
    val playerTracks: LiveData<List<TrackResponse>> get() = _playerTracks

    private var playerActiveTrack: TrackResponse? = null

    private val _favoriteSongs = MutableLiveData<List<TrackResponse>>(emptyList())
    val favoriteSongs: LiveData<List<TrackResponse>> get() = _favoriteSongs

    private val _searchResults = MutableLiveData<List<Show>>()
    val searchResults: LiveData<List<Show>> get() = _searchResults

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

    fun addFavorite(song: TrackResponse) {
        val currentList = _favoriteSongs.value.orEmpty().toMutableList()
        if (currentList.none { it.slug == song?.slug }) {
            currentList.add(song.copy(isLiked = true))
            _favoriteSongs.value = currentList
        }
    }

    fun removeFavorite(song: TrackResponse) {
        val currentList = _favoriteSongs.value.orEmpty().toMutableList()
        if (currentList.removeIf { it.slug == song.slug }) {
            _favoriteSongs.value = currentList
        }
    }

    fun isFavorite(song: TrackResponse): Boolean {
        return _favoriteSongs.value?.any { it.slug == song.slug } == true
    }


    fun toggleFavorite(song: TrackResponse) {
        if (isFavorite(song)) {
            removeFavorite(song)
        } else {
            addFavorite(song)
        }
    }

    // Axtarış funksiyası
    fun searchSongs(query: String) {
        viewModelScope.launch {
            try {
                val response = musicApiService.searchSongs(query)
                if (response.isSuccessful) {
                    _searchResults.postValue(
                        response.body()?.shows ?: listOf()
                    ) // Nəticələri LiveData-ya ötürürük
                } else {
                    _searchResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                _searchResults.postValue(emptyList())
            }
        }
    }

    fun setPlayerTracks(tracks: List<TrackResponse>, activeTrack: TrackResponse? = null) {
        if (_playerTracks.value != tracks)
            _playerTracks.postValue(tracks)
        playerActiveTrack = activeTrack
    }

    fun getPlaylistDetailsBySlug(
        slug: String?,
        activeTrack: TrackResponse? = null,
    ) = slug?.let {
        viewModelScope.launch {
            try {
                val response = musicApiService.getPlaylistDetails(slug)
                if (response.isSuccessful) {
                    playerActiveTrack = activeTrack
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