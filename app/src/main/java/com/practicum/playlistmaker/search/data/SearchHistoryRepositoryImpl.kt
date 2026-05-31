package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.Track

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun read(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()

        val dtoList = gson.fromJson(json, Array<TrackHistoryDto>::class.java).toList()

        return dtoList.map { TrackHistoryMapper.mapToTrack(it) }
    }

    override fun add(track: Track) {
        val history = read().toMutableList()

        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(MAX_HISTORY_SIZE)
        }

        val dtoList = history.map { TrackHistoryMapper.mapToDto(it) }

        sharedPreferences.edit {
            putString(HISTORY_KEY, gson.toJson(dtoList))
        }
    }

    override fun clear() {
        sharedPreferences.edit { remove(HISTORY_KEY) }
    }

    companion object {
        const val HISTORY_KEY = "HISTORY_KEY"
        private const val MAX_HISTORY_SIZE = 10
    }
}