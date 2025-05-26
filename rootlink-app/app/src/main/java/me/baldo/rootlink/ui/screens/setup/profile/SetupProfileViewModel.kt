package me.baldo.rootlink.ui.screens.setup.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.repositories.ProfileRepository

data class SetupProfileState(
    val name: String
)

interface SetupProfileActions {
    fun onSetName(name: String)
}

class SetupProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SetupProfileState(""))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(name = profileRepository.name.first()) }
        }
    }

    val actions = object : SetupProfileActions {
        override fun onSetName(name: String) {
            _state.update { it.copy(name = name) }
            viewModelScope.launch {
                profileRepository.setName(name)
            }
        }
    }
}