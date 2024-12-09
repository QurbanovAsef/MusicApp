package com.example.music.presentation.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.PlaylistItem

class FavoriteAdapter(
    private val onItemClick: (PlaylistItem) -> Unit,
    private val onLikeDislike: (PlaylistItem) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var favoriteSongs: MutableList<PlaylistItem> = mutableListOf()

    inner class FavoriteViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(playlistItem: PlaylistItem) = with(binding) {
            songTitle.text = playlistItem.title // Mahnının adı
            songArtist.text = playlistItem.artist // İfaçının adı
            songDuration.text = "Müddət: ${playlistItem.duration}" // Mahnının müddəti



            // Mahnıya toxunduqda detallarına keçid
            root.setOnClickListener { onItemClick(playlistItem) }

            // Bəyənmə/bəyənməmə ikonuna toxunduqda dəyiş
            favoriteIcon.setImageResource(
                if (playlistItem.isLiked) R.drawable.ic_favorite_full
                else R.drawable.ic_favorite_empty
            )
            favoriteIcon.setOnClickListener {
                onLikeDislike(playlistItem) // Like/dislike funksiyasını çağırır
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding =
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteSongs[position])
    }

    override fun getItemCount(): Int = favoriteSongs.size

    // Favoritləri yeniləyən metod
    fun updateData(newSongs: List<PlaylistItem>) {
        favoriteSongs = newSongs.toMutableList()
        notifyDataSetChanged()
    }

    // Favoritdən elementi silmək üçün metod
    fun removeItem(song: PlaylistItem) {
        val position = favoriteSongs.indexOf(song)
        if (position != -1) {
            favoriteSongs.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}