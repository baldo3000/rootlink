package me.baldo.rootlink.ui.screens.airqualitymap

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AirQualityMapState(
    val showNoInternetConnectivityWarning: Boolean
)

interface AirQualityMapActions {
    fun setShowNoInternetConnectivityWarning(show: Boolean)
}

class AirQualityMapViewModel() : ViewModel() {
    private val _state = MutableStateFlow(AirQualityMapState(false))
    val state = _state.asStateFlow()

    val action = object : AirQualityMapActions {
        override fun setShowNoInternetConnectivityWarning(show: Boolean) {
            _state.update { it.copy(showNoInternetConnectivityWarning = show) }
        }
    }
}