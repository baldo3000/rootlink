package me.baldo.rootlink.ui.screens.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.baldo.rootlink.data.model.Tree

enum class MapTab { EXPLORE, MAP }

data class MapState(
    val tab: MapTab,
    val trees: List<Tree> = emptyList(),
)

interface ChatActions {
    fun onTabChange(tab: MapTab)
    fun addTree(tree: Tree)
    fun addTrees(trees: List<Tree>)
    fun updateTrees(trees: List<Tree>)
}

class MapViewModel : ViewModel() {
    private val _state = MutableStateFlow(MapState(tab = MapTab.EXPLORE))
    val state = _state.asStateFlow()

    val actions = object : ChatActions {
        override fun onTabChange(tab: MapTab) {
            _state.update { it.copy(tab = tab) }
        }

        override fun addTree(tree: Tree) {
            _state.update { it.copy(trees = it.trees + tree) }
        }

        override fun addTrees(trees: List<Tree>) {
            _state.update { it.copy(trees = it.trees + trees) }
        }

        override fun updateTrees(trees: List<Tree>) {
            _state.update { it.copy(trees = trees) }
        }
    }
}