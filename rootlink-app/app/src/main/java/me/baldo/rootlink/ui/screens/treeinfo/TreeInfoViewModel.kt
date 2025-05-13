package me.baldo.rootlink.ui.screens.treeinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.repositories.TreesRepository

data class TreeInfoState(
    val tree: Tree? = null,
)

interface TreeInfoActions {
    fun updateTree(tree: String)
}

class TreeInfoViewModel(
    private val treesRepository: TreesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TreeInfoState())
    val state = _state.asStateFlow()

    val actions = object : TreeInfoActions {
        override fun updateTree(treeID: String) {
            viewModelScope.launch {
                val tree = treesRepository.getTree(treeID)
                _state.update { it.copy(tree = tree) }
            }
        }
    }
}