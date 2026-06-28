package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistLayoutBinding
import com.practicum.playlistmaker.playlists.domain.Playlist
import java.io.File

class PlaylistBottomSheetAdapter(
    private var playlists: List<Playlist>
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.PlaylistViewHolder>() {

    private var onItemClick: ((Playlist) -> Unit)? = null

    fun setOnItemClickListener(listener: (Playlist) -> Unit) {
        onItemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size

    fun updateData(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(
        private val binding: PlaylistLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.tvPlaylistName2.text = playlist.name

            val trackCount = playlist.trackCount
            binding.tvTrackCount2.text = binding.root.resources.getQuantityString(
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
                        .into(binding.CoverTack)
                } else {
                    Glide.with(binding.root.context)
                        .load(R.drawable.ic_placeholder_cover)
                        .centerCrop()
                        .transform(RoundedCorners(8))
                        .into(binding.CoverTack)
                }
            } else {
                Glide.with(binding.root.context)
                    .load(R.drawable.ic_placeholder_cover)
                    .centerCrop()
                    .transform(RoundedCorners(8))
                    .into(binding.CoverTack)
            }
        }
    }
}