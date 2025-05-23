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
        private val SETUP_DONE_KEY = booleanPreferencesKey("setup_done")
        private val SHOW_ALL_TREES_KEY = booleanPreferencesKey("show_all_trees")
        private val SIMPLER_MARKERS_KEY = booleanPreferencesKey("simpler_markers")
    }

    val setupDone = dataStore.data.map { it[SETUP_DONE_KEY] == true }
    suspend fun setSetupDone(done: Boolean) =
        dataStore.edit { it[SETUP_DONE_KEY] = done }

    val showAllTrees = dataStore.data.map { it[SHOW_ALL_TREES_KEY] == true }
    suspend fun setShowAllTrees(showAll: Boolean) =
        dataStore.edit { it[SHOW_ALL_TREES_KEY] = showAll }

    val simplerMarkers = dataStore.data.map { it[SIMPLER_MARKERS_KEY] == true }
    suspend fun setSimplerMarkers(simpler: Boolean) =
        dataStore.edit { it[SIMPLER_MARKERS_KEY] = simpler }
}