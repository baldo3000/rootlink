package me.baldo.rootlink.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import me.baldo.rootlink.BuildConfig
import me.baldo.rootlink.data.database.Tree

class TreesDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val TAG = "TreesDataSource"
        private const val BASE_URL = BuildConfig.SERVER_ADDRESS
    }

    suspend fun getTrees(): List<Tree>? {
        Log.i(TAG, "Getting trees from remote")
        val url = "$BASE_URL/api/trees"
        val trees = try {
            Log.d(TAG, "Trees received")
            httpClient.get(url).body<List<Tree>>()
        } catch (e: Exception) {
            Log.e(TAG, "Error while requesting trees: ${e.message}")
            null
        }
        Log.d(TAG, "Trees received: ${trees?.size}")
        return trees
    }
}