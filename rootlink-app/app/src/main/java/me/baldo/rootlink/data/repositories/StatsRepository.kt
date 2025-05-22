package me.baldo.rootlink.data.repositories

import me.baldo.rootlink.data.database.TreesDAO

class StatsRepository(
    private val treesDAO: TreesDAO
) {
    suspend fun getTreesInteractedWithInRegionCount(region: String) =
        treesDAO.getTreesInteractedWithInRegionCount(region)

    suspend fun getTreesInRegionCount(region: String) = treesDAO.getTreesInRegionCount(region)

    suspend fun getTreesInteractedWithCount() = treesDAO.getTreesInteractedWithCount()

    suspend fun getTreesCount() = treesDAO.getTreesCount()

    suspend fun getRegionsInteractedWithCount() = treesDAO.getRegionsInteractedWithCount()

    suspend fun getRegions() = treesDAO.getRegions()
}