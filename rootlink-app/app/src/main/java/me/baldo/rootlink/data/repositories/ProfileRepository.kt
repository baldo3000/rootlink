package me.baldo.rootlink.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val NAME_KEY = stringPreferencesKey("profile_name")
    }

    val name = dataStore.data.map { it[NAME_KEY] ?: "" }
    suspend fun setName(name: String) = dataStore.edit { it[NAME_KEY] = name }
}