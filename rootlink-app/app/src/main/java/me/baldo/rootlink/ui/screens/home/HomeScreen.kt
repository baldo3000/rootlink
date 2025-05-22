package me.baldo.rootlink.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import me.baldo.rootlink.ui.BottomBarTab
import me.baldo.rootlink.ui.composables.HomeOverlay

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    HomeOverlay(
        selectedTab = BottomBarTab.Home,
        onBottomTabClick = { tab ->
            if (tab != BottomBarTab.Home) {
                navController.navigate(tab.screen)
            }
        },
        navController = navController
    ) { innerPadding ->
        val padding = innerPadding

    }
}