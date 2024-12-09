package com.example.music.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.music.data.model.response.Song

class SongDiffCallback(
    private val oldList: List<Song>,
    private val newList: List<Song>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].slug == newList[newItemPosition].slug
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
