package com.example.music.presentation.auth.bottomMenu.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.FavoriteTrack
import com.example.music.data.model.response.TrackResponse
import com.example.music.data.service.FavoriteTrackDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
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

    /**
     * Bazadan favoritləri yükləyir və UI-ni yeniləyir
     */
    private fun loadFavoriteTracks() {
        viewModelScope.launch {
            val favoriteTracks = favoriteTrackDao.getAllFavoriteTracks().first()
            _favoriteTracks.postValue(favoriteTracks.map { it.toTrackResponse() })
        }
    }

    /**
     * Mahnını favoritlərə əlavə edir və UI-ni yeniləyir
     */
    fun addFavorite(track: TrackResponse): Boolean {
        return try {
            viewModelScope.launch {
                val favoriteTrack = track.toFavoriteTrack()
                favoriteTrackDao.insert(favoriteTrack)
                track.isLiked = true
                loadFavoriteTracks()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    /**
     * Mahnını favoritlərdən silir və UI-ni yeniləyir
     */

    fun removeFavorite(track: TrackResponse): Boolean {
        return try {
            viewModelScope.launch {
                val favoriteTrack = track.toFavoriteTrack()
                favoriteTrackDao.delete(favoriteTrack)
                track.isLiked = false
                loadFavoriteTracks()
            }
            true
        } catch (e: Exception) {
            false
        }
    }



    /**
     * Favorit olub-olmadığını dəyişir və statusu yeniləyir
     */
    fun toggleFavorite(track: TrackResponse) {
        viewModelScope.launch {
            val isFavorite = isFavorite(track)
            track.isLiked = !isFavorite

            if (isFavorite) {
                removeFavorite(track)
            } else {
                addFavorite(track)
            }

            // UI-ni yeniləyir ki, gecikmə olmasın
            val updatedList = _favoriteTracks.value?.map {
                if (it.id == track.id) it.copy(isLiked = track.isLiked) else it
            } ?: emptyList()

            _favoriteTracks.postValue(updatedList)
        }
    }


    /**
     * Mahnının favorit olub-olmadığını yoxlayır və statusunu yeniləyir
     */
    fun isFavorite(track: TrackResponse?): Boolean {
        val isFav = _favoriteTracks.value?.any { it.id == track?.id } == true
        track?.isLiked = isFav
        return isFav
    }

    /**
     * UI-ni dərhal yeniləyir ki, gecikmə olmasın
     */
    private fun updateUI(track: TrackResponse, isLiked: Boolean) {
        val updatedList = _favoriteTracks.value?.map {
            if (it.id == track.id) it.copy(isLiked = isLiked) else it
        } ?: emptyList()
        _favoriteTracks.postValue(updatedList)
    }

    /**
     * TrackResponse -> FavoriteTrack çevirmə metodu
     */
    private fun TrackResponse.toFavoriteTrack(): FavoriteTrack {
        return FavoriteTrack(
            id = this.id ?: 0,
            trackName = this.title ?: "Naməlum Mahnı",
            artistName = this.slug ?: "Naməlum İfaçı",
            isLiked = this.isLiked ?: false,
            showAlbumCoverURL = this.showAlbumCoverURL ?: ""
        )
    }

    /**
     * FavoriteTrack -> TrackResponse çevirmə metodu
     */
    private fun FavoriteTrack.toTrackResponse(): TrackResponse {
        return TrackResponse(
            id = this.id,
            title = this.trackName,
            slug = this.artistName,
            isLiked = this.isLiked,
            showAlbumCoverURL = this.showAlbumCoverURL
        )
    }
}
