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
)

interface ChatActions {
    fun addTree(tree: Tree)
    fun addTrees(trees: List<Tree>)
    fun updateTrees(trees: List<Tree>)
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
    }
}