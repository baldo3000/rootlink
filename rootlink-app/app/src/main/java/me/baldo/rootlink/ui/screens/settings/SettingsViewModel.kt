package me.baldo.rootlink.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.repositories.SettingsRepository

data class SettingsState(
    val showAllMonumentalTrees: Boolean
)

interface SettingsActions {
    fun onShowAllMonumentalTreesChanged(showAll: Boolean)
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow<SettingsState>(SettingsState(false))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(showAllMonumentalTrees = settingsRepository.showAllTrees.first()) }
        }
    }

    val actions = object : SettingsActions {
        override fun onShowAllMonumentalTreesChanged(showAll: Boolean) {
            _state.update { it.copy(showAllMonumentalTrees = showAll) }
            viewModelScope.launch {
                settingsRepository.setShowAllTrees(showAll)
            }
        }
    }
}