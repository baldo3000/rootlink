package me.baldo.rootlink.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import me.baldo.rootlink.ui.screens.chat.ChatScreen
import me.baldo.rootlink.ui.screens.chat.ChatViewModel
import me.baldo.rootlink.ui.screens.map.MapScreen
import me.baldo.rootlink.ui.screens.map.MapViewModel
import me.baldo.rootlink.ui.screens.settings.SettingsScreen
import me.baldo.rootlink.ui.screens.settings.SettingsViewModel
import me.baldo.rootlink.ui.screens.treeinfo.TreeInfoScreen
import me.baldo.rootlink.ui.screens.treeinfo.TreeInfoViewModel
import org.koin.androidx.compose.koinViewModel

sealed interface RootlinkRoute {
    @Serializable
    data object Home : RootlinkRoute

    @Serializable
    data object Map : RootlinkRoute

    @Serializable
    data object Favourites : RootlinkRoute

    @Serializable
    data object Chat : RootlinkRoute

    @Serializable
    data class TreeInfo(val tree: String) : RootlinkRoute

    @Serializable
    data object Settings : RootlinkRoute
}

@Composable
fun RootlinkNavGraph(navController: NavHostController) {
    val ctx = LocalContext.current
    val mapVM: MapViewModel = koinViewModel()
    val mapState by mapVM.state.collectAsStateWithLifecycle()
    val chatVM = koinViewModel<ChatViewModel>()
    val chatState by chatVM.state.collectAsStateWithLifecycle()
    val treeInfoVM = koinViewModel<TreeInfoViewModel>()
    val treeInfoState by treeInfoVM.state.collectAsStateWithLifecycle()
    val settingsVM = koinViewModel<SettingsViewModel>()
    val settingsState by settingsVM.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = RootlinkRoute.Map
    ) {
        composable<RootlinkRoute.Home> {

        }

        composable<RootlinkRoute.Map> {
            MapScreen(
                mapState = mapState,
                mapActions = mapVM.actions,
                showAllTrees = settingsState.showAllMonumentalTrees,
                openTreeChat = chatVM.actions::openTreeChat,
                navController = navController
            )
        }

        composable<RootlinkRoute.Favourites> {

        }

        composable<RootlinkRoute.Chat> {
            ChatScreen(
                chatState = chatState,
                chatActions = chatVM.actions,
                navController = navController
            )
        }

        composable<RootlinkRoute.TreeInfo> { backStackEntry ->
            val route = backStackEntry.toRoute<RootlinkRoute.TreeInfo>()
            val treeID = route.tree
            treeInfoVM.actions.updateTree(treeID)
            TreeInfoScreen(
                treeInfoState = treeInfoState,
                treeInfoActions = treeInfoVM.actions,
                navController = navController
            )
        }

        composable<RootlinkRoute.Settings> {
            SettingsScreen(
                settingsState = settingsState,
                settingsActions = settingsVM.actions,
                navController = navController
            )
        }
    }
}