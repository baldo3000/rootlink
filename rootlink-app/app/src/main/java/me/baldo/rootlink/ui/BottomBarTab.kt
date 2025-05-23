package me.baldo.rootlink.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.ui.graphics.vector.ImageVector
import me.baldo.rootlink.R

sealed interface BottomBarTab {
    val icon: ImageVector
    val iconSelected: ImageVector
    val titleResID: Int
    val screen: RootlinkRoute

    data object Home : BottomBarTab {
        override val icon = Icons.Outlined.Home
        override val iconSelected = Icons.Filled.Home
        override val titleResID = R.string.screen_home
        override val screen = RootlinkRoute.Home
    }

    data object Map : BottomBarTab {
        override val icon = Icons.Outlined.Map
        override val iconSelected = Icons.Filled.Map
        override val titleResID = R.string.screen_map
        override val screen = RootlinkRoute.Map
    }

    data object AirQualityMap : BottomBarTab {
        override val icon = Icons.Outlined.Air
        override val iconSelected = Icons.Filled.Air
        override val titleResID = R.string.screen_air_quality_map
        override val screen = RootlinkRoute.AirQualityMap
    }

    data object Catalog : BottomBarTab {
        override val icon = Icons.AutoMirrored.Outlined.ViewList
        override val iconSelected = Icons.AutoMirrored.Filled.ViewList
        override val titleResID = R.string.screen_catalog
        override val screen = RootlinkRoute.Catalog
    }

    companion object {
        val tabs = listOf(
            Home,
            Map,
            AirQualityMap,
            Catalog
        )
    }
}