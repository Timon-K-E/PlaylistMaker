// com/practicum/playlistmaker/favorite/data/db/AppDatabase.kt
package com.practicum.playlistmaker.favorite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.favorite.data.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.favorite.data.db.entity.TrackEntity
import com.practicum.playlistmaker.playlists.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.playlists.data.db.dao.TrackInPlaylistDao
import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlists.data.db.entity.TrackInPlaylistEntity

@Database(
    version = 3,
    entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}