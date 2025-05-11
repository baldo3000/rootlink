package me.baldo.rootlink.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
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

    data object Favourites : BottomBarTab {
        override val icon = Icons.Outlined.FavoriteBorder
        override val iconSelected = Icons.Filled.Favorite
        override val titleResID = R.string.screen_favourites
        override val screen = RootlinkRoute.Favourites
    }

    companion object {
        val tabs = listOf(
            Home,
            Map,
            Favourites
        )
    }
}