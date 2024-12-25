package com.example.music.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.ExactShow
import com.example.music.data.model.response.FavoriteTrack
import com.example.music.data.model.response.Playlist

import com.example.music.data.model.response.TrackResponse
import com.example.music.data.model.response.toFavoriteTrack
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

    private var playerActiveTrack: TrackResponse? = null

    private val _favoriteTracks = MutableLiveData<List<FavoriteTrack>>(emptyList())
    val favoriteTracks: LiveData<List<FavoriteTrack>> get() = _favoriteTracks


    private val _searchResults = MutableLiveData<List<ExactShow>>()
    val searchResults: LiveData<List<ExactShow>> = _searchResults


    private val musicApiService: MusicApiService = RetrofitInstance.api

    private val favoriteTrackDao: FavoriteTrackDao = AppDatabase.getDatabase(application).favoriteTrackDao()

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

    // Favoritləri əlavə etmək
    fun addFavorite(song: TrackResponse) {
        val favoriteTrack = FavoriteTrack(
            title = song.title,
            venueName = song.venueName,
            isLiked = true
        )
        viewModelScope.launch {
            favoriteTrackDao.insert(favoriteTrack)
            loadFavoriteTracks()  // Yenilənmiş favoritləri yükləyirik
        }
    }

    // Favoritləri silmək
    fun removeFavorite(song: TrackResponse) {
        val favoriteTrack = FavoriteTrack(
            title = song.title,
            venueName = song.venueName,
            isLiked = false
        )
        viewModelScope.launch {
            favoriteTrackDao.delete(favoriteTrack)
            loadFavoriteTracks()  // Yenilənmiş favoritləri yükləyirik
        }
    }

    // Favorit trackləri yükləmək
    fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTrackDao.getAllFavoriteTracks().collect { tracks ->
                _favoriteTracks.value = tracks
            }
        }
    }


    // Mahnının favorit olub olmadığını yoxlamaq
    fun isFavorite(song: TrackResponse): Boolean {
        return _favoriteTracks.value?.any { it.slug == song.slug } == true
    }

    // Favorit statusunu dəyişdirmək
    fun toggleFavorite(song: TrackResponse) {
        val updatedStatus = song.isLiked ?: false // Default olaraq false
        song.isLiked = !updatedStatus
        val favoriteTrack = song.toFavoriteTrack()
        viewModelScope.launch {
            if (song.isLiked == true) {
                favoriteTrackDao.insert(favoriteTrack)
            } else {
                favoriteTrackDao.delete(favoriteTrack)
            }
            loadFavoriteTracks()
        }
    }

    // Axtarış funksiyası
    fun searchSongs(query: String) {
        viewModelScope.launch {
            try {
                val response = musicApiService.searchExactShows(query)
                if (response.isSuccessful) {
                    // Cavab alındıqda success handler
                    val exactShow = response.body()?.exactShow
                    if (exactShow?.tracks.isNullOrEmpty()) {
                        _searchResults.postValue(emptyList()) // Track-lər boşsa nəticələri boş qaytarırıq
                        Log.d("Search", "ExactShow tracks: ${exactShow?.tracks}")
                    } else {
                        _searchResults.postValue(exactShow?.let { listOf(it) } ?: emptyList())
                    }
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



    fun setPlayerTracks(tracks: List<TrackResponse>, activeTrack: TrackResponse? = null) {
        if (_playerTracks.value != tracks)
            _playerTracks.postValue(tracks)
        playerActiveTrack = activeTrack
    }
    fun mapStringsToTrackResponses(tracks: List<String>): List<TrackResponse> {
        return tracks.map { trackString ->
            TrackResponse(
                title = trackString, // String dəyəri `title` kimi istifadə olunur
                mp3Url = null // Digər sahələri `null` olaraq saxlayırıq
            )
        }
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
