package com.example.music.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.PlaylistItem
import com.example.music.data.model.response.ShowResponse
import com.example.music.data.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _favoriteSongs = MutableLiveData<MutableList<PlaylistItem>>(mutableListOf())
    val favoriteSongs: LiveData<MutableList<PlaylistItem>> get() = _favoriteSongs

    private val _allSongs = MutableLiveData<List<PlaylistItem>>()
    val allSongs: LiveData<List<PlaylistItem>> get() = _allSongs

    private val _allShows = MutableLiveData<List<ShowResponse>>()
    val allShows: LiveData<List<ShowResponse>> get() = _allShows

    fun getAllShows() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.create().getAllShows()
                if (response.isSuccessful) {
                    val show = response.body()

                    _allShows.value = show?.shows.orEmpty()

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
                val response = RetrofitInstance.create().getAllSongs()
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
    fun toggleFavorite(song: PlaylistItem) {
        if (isFavorite(song)) {
            removeFavorite(song)
        } else {
            addFavorite(song)
        }
    }


//    fun getSongDetails(songId: Int) {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.create().getSongDetails(songId)
//                if (response.isSuccessful) {
//                    val songDetail = response.body()
//                    Log.d("SongDetails", songDetail.toString())
//                } else {
//                    Log.e("SharedViewModel", "API Error: ${response.message()}")
//                }
//            } catch (e: Exception) {
//                Log.e("SharedViewModel", "Error: ${e.message}")
//            }
//        }
//    }


    // Favoritlərə musiqi əlavə edir
    fun addFavorite(song: PlaylistItem): Boolean {
        _favoriteSongs.value?.let {
            if (isFavorite(song)) return false
            it.add(song)
            _favoriteSongs.value = it
        }
        return true
    }

    // Favoritdən musiqini silir
    fun removeFavorite(song: PlaylistItem) {
        _favoriteSongs.value?.let {
            it.removeIf { it.id == song.id }
            _favoriteSongs.value = it
        }
    }

    // Mahnının favoritdə olub-olmamasını yoxlayırıq
    fun isFavorite(song: PlaylistItem): Boolean {
        return _favoriteSongs.value?.any { it.id == song.id } == true
    }
}
