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
import com.example.music.data.service.MusicApiService
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _favoriteSongs = MutableLiveData<List<Song>>(emptyList())
    val favoriteSongs: LiveData<List<Song>> get() = _favoriteSongs
    private val _searchResults = MutableLiveData<List<Song>>()
    val searchResults: LiveData<List<Song>> get() = _searchResults

    // RetrofitInstance vasitəsilə MusicApiService əldə edilir
    private val musicApiService: MusicApiService = RetrofitInstance.api

    private val _allSongs = MutableLiveData<List<Song>>()
    val allSongs: LiveData<List<Song>> get() = _allSongs

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
                val response = RetrofitInstance.api.getSongs() // 'getAllSongs' əvəzinə 'getSongs' istifadə olunur
                if (response.isSuccessful) {
                    // API-dən gələn cavab
                    val songsResponse = response.body()

                    // songs null olarsa, boş bir siyahı qaytarılır
                    val allSongsList = songsResponse?.songs?.orEmpty() ?: emptyList()

                    // Nəticəni təyin etmək
                    _allSongs.value = allSongsList
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

//    fun getSongDetails(songId: Int) {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.getSongDetails(songId)
//                if (response.isSuccessful) {
//                    _songDetails.value = response.body()
//                } else {
//                    Log.e("SharedViewModel", "API Error: ${response.message()}")
//                }
//            } catch (e: Exception) {
//                Log.e("SharedViewModel", "Error: ${e.message}")
//            }
//        }
//    }
//
fun addFavorite(song: Song) {
    val currentList = _favoriteSongs.value.orEmpty().toMutableList()
    if (currentList.none { it.slug == song.slug }) {
        currentList.add(song)
        song.isLiked = true
        _favoriteSongs.value = currentList
    }
}

    fun removeFavorite(song: Song) {
        val currentList = _favoriteSongs.value.orEmpty().toMutableList()
        if (currentList.removeIf { it.slug == song.slug }) {
            song.isLiked = false
            _favoriteSongs.value = currentList
        }
    }


    fun toggleFavorite(song: Song) {
        if (isFavorite(song)) {
            removeFavorite(song)
        } else {
            addFavorite(song)
        }
    }


    fun isFavorite(song: Song): Boolean {
        return _favoriteSongs.value?.any { it.slug == song.slug } == true
    }

    // Axtarış funksiyası
    fun searchSongs(query: String) {
        viewModelScope.launch {
            try {
                val response = musicApiService.searchSongs(query)
                if (response.isSuccessful) {
                    _searchResults.postValue(response.body()) // Nəticələri LiveData-ya ötürürük
                } else {
                    _searchResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                _searchResults.postValue(emptyList())
            }
        }
    }
}

