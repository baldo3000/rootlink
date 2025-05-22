package me.baldo.rootlink.ui.screens.treeinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.remote.AirQualityDataSource
import me.baldo.rootlink.data.repositories.TreesRepository

data class TreeInfoState(
    val tree: Tree? = null,
    val currentAqiValue: Int = 0,
    val currentAqiDescription: String = ""
)

interface TreeInfoActions {
    fun updateTree(tree: String)
}

class TreeInfoViewModel(
    private val treesRepository: TreesRepository,
    private val airQualityDataSource: AirQualityDataSource
) : ViewModel() {
    private val _state = MutableStateFlow(TreeInfoState())
    val state = _state.asStateFlow()

    val actions = object : TreeInfoActions {
        override fun updateTree(treeID: String) {
            viewModelScope.launch {
                _state.update { it.copy(tree = treesRepository.getTree(treeID)) }
            }
            viewModelScope.launch {
                _state.update {
                    if (it.tree != null) {
                        val position = it.tree.position
                        val aqi = airQualityDataSource.getAirQuality(
                            position.latitude,
                            position.longitude
                        )
                        val aqiValue = aqi?.aqi ?: 0
                        val aqiDescription = aqi?.category ?: ""
                        it.copy(currentAqiValue = aqiValue, currentAqiDescription = aqiDescription)
                    } else {
                        it.copy(currentAqiValue = 0)
                    }
                }
            }
        }
    }
}