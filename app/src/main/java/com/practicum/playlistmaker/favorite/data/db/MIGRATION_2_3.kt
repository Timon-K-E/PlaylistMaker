package com.practicum.playlistmaker.favorite.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val cursor = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='track_in_playlist_table'")
        val tableExists = cursor.moveToFirst()
        cursor.close()

        if (!tableExists) {
            database.execSQL("""
                CREATE TABLE `track_in_playlist_table` (
                    `trackId` INTEGER PRIMARY KEY NOT NULL,
                    `trackName` TEXT NOT NULL,
                    `artistName` TEXT NOT NULL,
                    `trackTimeMillis` INTEGER NOT NULL,
                    `artworkUrl100` TEXT NOT NULL,
                    `collectionName` TEXT,
                    `releaseDate` TEXT,
                    `primaryGenreName` TEXT NOT NULL,
                    `country` TEXT NOT NULL,
                    `previewUrl` TEXT
                )
            """)
        }
    }
}