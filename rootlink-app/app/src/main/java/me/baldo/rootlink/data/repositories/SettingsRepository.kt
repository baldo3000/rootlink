package me.baldo.rootlink.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SHOW_ALL_TREES_KEY = booleanPreferencesKey("show_all_trees")
    }

    // val username = dataStore.data.map { it[USERNAME_KEY] ?: "" }
    // suspend fun setUsername(username: String) = dataStore.edit { it[USERNAME_KEY] = username }

    val showAllTrees = dataStore.data.map { it[SHOW_ALL_TREES_KEY] == true }
    suspend fun setShowAllTrees(showAll: Boolean) =
        dataStore.edit { it[SHOW_ALL_TREES_KEY] = showAll }
}