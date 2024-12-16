package com.example.music.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.music.data.entity.MusicEntity
import com.example.music.data.model.response.Show
import com.example.music.data.model.response.Song
import com.example.music.data.retrofit.RetrofitInstance
import com.example.music.data.service.MusicApiService
import kotlinx.coroutines.launch


class SharedViewModel : ViewModel() {

    private val songUrls = listOf(
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-17.mp3",
    )

    private val songImagesUrls = listOf(
        "https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png",
        "https://images.unsplash.com/photo-1593642634315-48f5414c3ad9",
        "https://images.pexels.com/photos/34950/pexels-photo.jpg",
        "https://images.pexels.com/photos/414612/pexels-photo-414612.jpeg",
        "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885_1280.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/3/3f/Fronalpstock_big.jpg",
    )


    private val _playerMusicList = MutableLiveData<List<MusicEntity>>(emptyList())
    val playerMusicList: LiveData<List<MusicEntity>> get() = _playerMusicList

    private val _favoriteSongs = MutableLiveData<List<Song>>(emptyList())
    val favoriteSongs: LiveData<List<Song>> get() = _favoriteSongs

    private val _searchResults = MutableLiveData<List<Show>>()
    val searchResults: LiveData<List<Show>> get() = _searchResults

    // RetrofitInstance vasitəsilə MusicApiService əldə edilir
    private val musicApiService: MusicApiService = RetrofitInstance.api

    private val _allSongs = MutableLiveData<List<Song>>()
    val allSongs: LiveData<List<Song>>
        get() = _allSongs.map {
            val modifiedList = mutableListOf<Song>()

            it.forEachIndexed { index, song ->
                val songIndex = index % songUrls.size
                val imageIndex = index % songImagesUrls.size
                modifiedList.add(
                    song.copy(
                        trackUrl = songUrls[songIndex],
                        imageUrl = songImagesUrls[imageIndex]
                    )
                )
            }

            modifiedList
        }

    var playerActiveMusic: MusicEntity? = null
        private set

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
                    val allSongsList = songsResponse?.songs.orEmpty() ?: emptyList()

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


    fun addFavorite(song: Song) {
        val currentList = _favoriteSongs.value.orEmpty().toMutableList()
        if (currentList.none { it.slug == song.slug }) {
            currentList.add(song.copy(isLiked = true))
            _favoriteSongs.value = currentList
        }
    }

    fun removeFavorite(song: Song) {
        val currentList = _favoriteSongs.value.orEmpty().toMutableList()
        if (currentList.removeIf { it.slug == song.slug }) {
            _favoriteSongs.value = currentList
        }
    }

    fun isFavorite(song: Song): Boolean {
        return _favoriteSongs.value?.any { it.slug == song.slug } == true
    }


    fun toggleFavorite(song: Song) {
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
                    _searchResults.postValue(response.body()?.shows ?: listOf()) // Nəticələri LiveData-ya ötürürük
                } else {
                    _searchResults.postValue(emptyList())
                }
            } catch (e: Exception) {
                _searchResults.postValue(emptyList())
            }
        }
    }

    fun setPlayerSongs(songs: List<MusicEntity>, activeSong: MusicEntity? = null) {
        _playerMusicList.postValue(songs)
        playerActiveMusic = activeSong
    }
}
