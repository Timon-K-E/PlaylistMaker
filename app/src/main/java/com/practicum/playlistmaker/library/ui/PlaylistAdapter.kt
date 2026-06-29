package com.practicum.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.playlists.domain.Playlist
import java.io.File

class PlaylistAdapter(
    private var playlists: List<Playlist>
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.tvPlaylistName.text = playlist.name

            val trackCount = playlist.trackCount
            binding.tvTrackCount.text = binding.root.resources.getQuantityString(
                R.plurals.tracks_count,
                trackCount,
                trackCount
            )

            if (!playlist.coverPath.isNullOrEmpty()) {
                val file = File(playlist.coverPath)
                if (file.exists()) {
                    Glide.with(binding.root.context)
                        .load(file)
                        .centerCrop()
                        .transform(RoundedCorners(8))
                        .into(binding.ivPlaylistCover)
                } else {
                    Glide.with(binding.root.context)
                        .load(R.drawable.ic_placeholder_cover)
                        .centerCrop()
                        .transform(RoundedCorners(8))
                        .into(binding.ivPlaylistCover)
                }
            } else {
                Glide.with(binding.root.context)
                    .load(R.drawable.ic_placeholder_cover)
                    .centerCrop()
                    .transform(RoundedCorners(8))
                    .into(binding.ivPlaylistCover)
            }
        }
    }
}