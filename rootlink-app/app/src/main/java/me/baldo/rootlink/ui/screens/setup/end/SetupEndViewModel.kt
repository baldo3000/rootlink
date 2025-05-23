package me.baldo.rootlink.ui.screens.setup.end

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.repositories.SettingsRepository

interface SetupEndActions {
    fun endSetup()
}

class SetupEndViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val actions = object : SetupEndActions {
        override fun endSetup() {
            viewModelScope.launch {
                settingsRepository.setSetupDone(true)
            }
        }
    }
}