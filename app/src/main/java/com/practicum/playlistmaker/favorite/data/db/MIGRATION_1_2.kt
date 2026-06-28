package com.practicum.playlistmaker.favorite.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Проверяем, существует ли уже таблица
        val cursor = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='playlists_table'")
        val tableExists = cursor.moveToFirst()
        cursor.close()

        if (!tableExists) {
            // Создаем таблицу только если она не существует
            database.execSQL("""
                CREATE TABLE `playlists_table` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `description` TEXT,
                    `coverPath` TEXT,
                    `trackIds` TEXT NOT NULL,
                    `trackCount` INTEGER NOT NULL
                )
            """)
        }
    }
}