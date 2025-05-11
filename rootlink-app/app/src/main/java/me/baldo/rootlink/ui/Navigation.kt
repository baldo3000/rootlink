package me.baldo.rootlink.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import me.baldo.rootlink.ui.screens.chat.ChatScreen
import me.baldo.rootlink.ui.screens.chat.ChatViewModel
import me.baldo.rootlink.ui.screens.map.MapScreen
import me.baldo.rootlink.ui.screens.map.MapViewModel
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
}

@Composable
fun RootlinkNavGraph(navController: NavHostController) {
    val ctx = LocalContext.current
    val mapVM: MapViewModel = koinViewModel()
    val mapState by mapVM.state.collectAsStateWithLifecycle()
    val chatVM = koinViewModel<ChatViewModel>()
    val chatState by chatVM.state.collectAsStateWithLifecycle()

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
    }
}