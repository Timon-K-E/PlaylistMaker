package com.practicum.playlistmaker.favorite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.favorite.data.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.favorite.data.db.entity.TrackEntity
import com.practicum.playlistmaker.playlists.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.playlists.data.db.entity.PlaylistEntity

@Database(
    version = 2,
    entities = [TrackEntity::class, PlaylistEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
}