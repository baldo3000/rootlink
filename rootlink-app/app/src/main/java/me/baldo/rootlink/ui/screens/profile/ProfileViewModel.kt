package me.baldo.rootlink.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.repositories.ProfileRepository

data class ProfileState(
    val name: String
)

interface ProfileActions {
    fun onSetName(name: String)
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState(""))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(name = profileRepository.name.first()) }
        }
    }

    val actions = object : ProfileActions {
        override fun onSetName(name: String) {
            _state.update { it.copy(name = name) }
            viewModelScope.launch {
                profileRepository.setName(name)
            }
        }
    }
}