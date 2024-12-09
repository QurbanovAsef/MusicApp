package com.example.music.presentation.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.PlaylistItem
import com.example.music.data.model.response.Show
import com.example.music.data.model.response.Song
import com.example.music.data.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _favoriteSongs = MutableLiveData<List<PlaylistItem>>(emptyList())
    val favoriteSongs: LiveData<List<PlaylistItem>> get() = _favoriteSongs

    private val _allSongs = MutableLiveData<List<PlaylistItem>>()
    val allSongs: LiveData<List<PlaylistItem>> get() = _allSongs

    private val _allShows = MutableLiveData<List<Show>>()
    val allShows: LiveData<List<Show>> get() = _allShows

    private val _tracksByShow = MutableLiveData<List<Song>>()
    val tracksByShow: LiveData<List<Song>> get() = _tracksByShow

    private val _songDetails = MutableLiveData<Song?>()
    val songDetails: LiveData<Song?> get() = _songDetails

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
                            id = it.slug.hashCode(), // Assuming `slug` is unique
                            title = it.title ?: "Unknown Title",
                            artist = it.artist ?: "Unknown Artist",
                            duration = it.tracksCount?.toInt() ?: 0
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

//    fun getTracksByShow(showDate: String) {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.getTracksByShow(showDate)
//                if (response.isSuccessful) {
//                    _tracksByShow.value = response.body() ?: emptyList()
//                } else {
//                    Log.e("SharedViewModel", "API Error: ${response.message()}")
//                }
//            } catch (e: Exception) {
//                Log.e("SharedViewModel", "Error: ${e.message}")
//            }
//        }
//    }

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
        val currentList = _favoriteSongs.value.orEmpty().toMutableList()
        if (currentList.any { it.id == song.id }) return false
        currentList.add(song)
        _favoriteSongs.value = currentList
        return true
    }
    fun isFavorite(song: PlaylistItem): Boolean {
        return _favoriteSongs.value?.any { it.id == song.id } == true
    }

    fun removeFavorite(song: PlaylistItem) {
        val currentList = _favoriteSongs.value.orEmpty().toMutableList()
        currentList.removeIf { it.id == song.id }
        _favoriteSongs.value = currentList
    }

    fun toggleFavorite(song: PlaylistItem) {
        if (addFavorite(song).not()) {
            removeFavorite(song)
        }
    }
}

