package me.baldo.rootlink.ui.screens.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.repositories.MessagesRepository

data class MapState(
    val trees: List<Tree> = emptyList(),

    val showLocationDisabledWarning: Boolean = false,
    val showLocationPermissionDeniedWarning: Boolean = false,
    val showLocationPermissionPermanentlyDeniedWarning: Boolean = false,
    val showNoInternetConnectivityWarning: Boolean = false
)

interface ChatActions {
    fun addTree(tree: Tree)
    fun addTrees(trees: List<Tree>)
    fun updateTrees(trees: List<Tree>)

    fun setShowLocationDisabledWarning(show: Boolean)
    fun setShowLocationPermissionDeniedWarning(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedWarning(show: Boolean)
    fun setShowNoInternetConnectivityWarning(show: Boolean)
    fun disableAllWarnings()
}

class MapViewModel(
    private val messagesRepository: MessagesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val trees = messagesRepository.getTrees()
            Log.i("MapViewModel", "Loaded ${trees.size} trees")
            _state.update { it.copy(trees = trees) }
        }
    }

    val actions = object : ChatActions {

        override fun addTree(tree: Tree) {
            _state.update { it.copy(trees = it.trees + tree) }
        }

        override fun addTrees(trees: List<Tree>) {
            _state.update { it.copy(trees = it.trees + trees) }
        }

        override fun updateTrees(trees: List<Tree>) {
            _state.update { it.copy(trees = trees) }
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