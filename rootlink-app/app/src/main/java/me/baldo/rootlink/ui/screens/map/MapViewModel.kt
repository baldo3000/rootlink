package me.baldo.rootlink.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.repositories.TreesRepository

data class MapState(
    val trees: List<Tree> = emptyList(),
    val isFollowingUser: Boolean = false,

    val showLocationDisabledWarning: Boolean = false,
    val showLocationPermissionDeniedWarning: Boolean = false,
    val showLocationPermissionPermanentlyDeniedWarning: Boolean = false,
    val showNoInternetConnectivityWarning: Boolean = false
)

interface ChatActions {
    fun setFollowingUser(follow: Boolean)

    fun setShowLocationDisabledWarning(show: Boolean)
    fun setShowLocationPermissionDeniedWarning(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedWarning(show: Boolean)
    fun setShowNoInternetConnectivityWarning(show: Boolean)
    fun disableAllWarnings()
}

class MapViewModel(
    treesRepository: TreesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    val trees = treesRepository.loadedTrees

    init {
        viewModelScope.launch {
            trees.collect { trees ->
                _state.update { it.copy(trees = trees) }
            }
        }
    }

    val actions = object : ChatActions {

        override fun setFollowingUser(follow: Boolean) {
            _state.update { it.copy(isFollowingUser = follow) }
        }

        override fun setShowLocationDisabledWarning(show: Boolean) {
            _state.update { it.copy(showLocationDisabledWarning = show) }
        }

        override fun setShowLocationPermissionDeniedWarning(show: Boolean) {
            _state.update { it.copy(showLocationPermissionDeniedWarning = show) }
        }

        override fun setShowLocationPermissionPermanentlyDeniedWarning(show: Boolean) {
            _state.update { it.copy(showLocationPermissionPermanentlyDeniedWarning = show) }
        }

        override fun setShowNoInternetConnectivityWarning(show: Boolean) {
            _state.update { it.copy(showNoInternetConnectivityWarning = show) }
        }

        override fun disableAllWarnings() {
            _state.update {
                it.copy(
                    showLocationDisabledWarning = false,
                    showLocationPermissionDeniedWarning = false,
                    showLocationPermissionPermanentlyDeniedWarning = false,
                    showNoInternetConnectivityWarning = false
                )
            }
        }
    }
}