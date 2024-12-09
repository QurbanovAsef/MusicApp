package com.example.music.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.PlaylistItem
import com.example.music.data.model.response.Show
import com.example.music.data.model.response.ShowsResponse
import com.example.music.data.model.response.SongResponse
import com.example.music.data.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _favoriteSongs = MutableLiveData<MutableList<PlaylistItem>>(mutableListOf())
    val favoriteSongs: LiveData<MutableList<PlaylistItem>> get() = _favoriteSongs

    private val _allSongs = MutableLiveData<List<PlaylistItem>>()
    val allSongs: LiveData<List<PlaylistItem>> get() = _allSongs

    private val _allShows = MutableLiveData<List<Show>>()
    val allShows: LiveData<List<Show>> get() = _allShows

    private val _tracksByShow = MutableLiveData<List<SongResponse>>()
    val tracksByShow: LiveData<List<SongResponse>> get() = _tracksByShow

    private val _songDetails = MutableLiveData<SongResponse?>()
    val songDetails: LiveData<SongResponse?> get() = _songDetails

    fun getAllShows() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllShows()
                if (response.isSuccessful) {
                    _allShows.value = response.body()?.shows ?: emptyList()
                } else {
                    Log.e("SharedViewModel", "API Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error: ${e.message}")
            }
        }
    }

    fun getAllSongs() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllSongs()
                if (response.isSuccessful) {
                    _allSongs.value = response.body()?.map {
                        PlaylistItem(
                            id = it.id,
                            title = it.title,
                            artist = it.artist,
                            duration = it.duration,
                            url = it.url,
                            isLiked = false
                        )
                    } ?: emptyList()
                } else {
                    Log.e("SharedViewModel", "API Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error: ${e.message}")
            }
        }
    }

    fun getTracksByShow(showDate: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTracksByShow(showDate)
                if (response.isSuccessful) {
                    _tracksByShow.value = response.body() ?: emptyList()
                } else {
                    Log.e("SharedViewModel", "API Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error: ${e.message}")
            }
        }
    }

    fun getSongDetails(songId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getSongDetails(songId)
                if (response.isSuccessful) {
                    _songDetails.value = response.body()
                } else {
                    Log.e("SharedViewModel", "API Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error: ${e.message}")
            }
        }
    }

    fun addFavorite(song: PlaylistItem): Boolean {
        _favoriteSongs.value?.let {
            if (isFavorite(song)) return false
            it.add(song)
            _favoriteSongs.value = it
        }
        return true
    }

    fun removeFavorite(song: PlaylistItem) {
        _favoriteSongs.value?.let {
            it.removeIf { it.id == song.id }
            _favoriteSongs.value = it
        }
    }

    fun isFavorite(song: PlaylistItem): Boolean {
        return _favoriteSongs.value?.any { it.id == song.id } == true
    }

    fun toggleFavorite(song: PlaylistItem) {
        if (isFavorite(song)) {
            removeFavorite(song)
        } else {
            addFavorite(song)
        }
    }
}
