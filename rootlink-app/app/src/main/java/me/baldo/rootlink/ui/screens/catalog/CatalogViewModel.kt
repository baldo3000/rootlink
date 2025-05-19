package me.baldo.rootlink.ui.screens.catalog

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

data class CatalogState(
    val trees: List<Tree> = emptyList()
)

interface CatalogActions {}

class CatalogViewModel(
    treesRepository: TreesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    val trees = treesRepository.getAllTrees().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            trees.collect { trees ->
                _state.update { it.copy(trees = trees) }
            }
        }
    }

    val actions = object : CatalogActions {}
}