package com.example.music.presentation.auth.bottomMenu.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.FavoriteTrack
import com.example.music.data.model.response.TrackResponse
import com.example.music.data.service.FavoriteTrackDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteTrackViewModel @Inject constructor(
    private val favoriteTrackDao: FavoriteTrackDao
) : ViewModel() {

    private val _favoriteTracks = MutableLiveData<List<TrackResponse>>()
    val favoriteTracks: LiveData<List<TrackResponse>> get() = _favoriteTracks

    init {
        loadFavoriteTracks()
    }

    private fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTrackDao.getAllFavoriteTracks().collect { favoriteTracks ->
                _favoriteTracks.value = favoriteTracks.map { favoriteTrack ->
                    TrackResponse(
                        id = favoriteTrack.id,
                        title = favoriteTrack.trackName,
                        slug = favoriteTrack.artistName,
                        isLiked = favoriteTrack.isLiked,
                        showAlbumCoverURL = favoriteTrack.showAlbumCoverURL // Şəkil URL-i əlavə edildi
                    )
                }
            }
        }
    }

    private fun TrackResponse.toFavoriteTrack(): FavoriteTrack {
        return FavoriteTrack(
            id = this.id ?: 0,
            trackName = this.title ?: "",
            artistName = this.slug ?: "",
            isLiked = this.isLiked == true,
            showAlbumCoverURL = this.showAlbumCoverURL ?: "" // Şəkil URL-i əlavə edildi
        )
    }

    fun addFavorite(track: TrackResponse): Boolean {
        return try {
            viewModelScope.launch {
                val favoriteTrack = track.toFavoriteTrack()
                favoriteTrackDao.insert(favoriteTrack)
                _favoriteTracks.value = _favoriteTracks.value?.toMutableList()?.apply {
                    // Yeni mahnını əlavə edir
                    val index = indexOfFirst { it.id == track.id }
                    if (index != -1) {
                        this[index] = track // Mövcud mahnını yeniləyir
                    } else {
                        add(track)
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun removeFavorite(track: TrackResponse): Boolean {
        return try {
            viewModelScope.launch {
                val favoriteTrack = track.toFavoriteTrack()
                favoriteTrackDao.delete(favoriteTrack)
                _favoriteTracks.value = _favoriteTracks.value?.toMutableList()?.apply {
                    remove(track)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun toggleFavorite(track: TrackResponse) {
        viewModelScope.launch {
            if (track.isLiked == true) {
                removeFavorite(track)
            } else {
                addFavorite(track)
            }
        }
    }
    fun isFavorite(song: TrackResponse?): Boolean {
        if (song == null) return false
        return _favoriteTracks.value?.any { it.slug == song.slug } == true
    }

}
