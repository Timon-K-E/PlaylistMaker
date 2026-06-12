package com.practicum.playlistmaker.search.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            return@withContext Response().apply { resultCode = -1 }
        }

        if (dto !is TrackSearchRequest) {
            return@withContext Response().apply { resultCode = 400 }
        }

        try {
            val response = iTunesService.search(dto.expression)
            val body = response.body()

            body?.apply {
                resultCode = response.code()
            } ?: Response().apply {
                resultCode = response.code()
            }

        } catch (e: Exception) {
            Response().apply { resultCode = 500 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}