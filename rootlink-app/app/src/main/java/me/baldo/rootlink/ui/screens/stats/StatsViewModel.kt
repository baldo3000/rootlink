package me.baldo.rootlink.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.repositories.StatsRepository

data class StatCompletion(val currentValue: Int, val maxValue: Int)

data class StatsState(
    val treesInteractedWith: StatCompletion = StatCompletion(0, 0),
    val regionsInteractedWith: StatCompletion = StatCompletion(0, 0),
    val treesInRegionsInteractedWith: Map<String, StatCompletion> = emptyMap(),
)

class StatsViewModel(
    private val statsRepository: StatsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StatsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val treesInteractedWithCount = statsRepository.getTreesInteractedWithCount()
            val treesCount = statsRepository.getTreesCount()
            val regionsInteractedWithCount = statsRepository.getRegionsInteractedWithCount()
            val regions = statsRepository.getRegions()

            val treesInRegionsStats = regions.associateWith { region ->
                val treesInteractedWithInRegion =
                    statsRepository.getTreesInteractedWithInRegionCount(region)
                val treesInRegion = statsRepository.getTreesInRegionCount(region)
                StatCompletion(
                    currentValue = treesInteractedWithInRegion,
                    maxValue = treesInRegion
                )
            }

            _state.value = StatsState(
                treesInteractedWith = StatCompletion(
                    currentValue = treesInteractedWithCount,
                    maxValue = treesCount
                ),
                regionsInteractedWith = StatCompletion(
                    currentValue = regionsInteractedWithCount,
                    maxValue = regions.size
                ),
                treesInRegionsInteractedWith = treesInRegionsStats
            )
        }
    }
}