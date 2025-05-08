package me.baldo.rootlink.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.baldo.rootlink.R
import me.baldo.rootlink.data.model.Tree
import me.baldo.rootlink.ui.screens.chat.ChatScreen
import me.baldo.rootlink.ui.screens.chat.ChatViewModel
import me.baldo.rootlink.ui.screens.map.MapScreen
import me.baldo.rootlink.ui.screens.map.MapViewModel
import org.koin.androidx.compose.koinViewModel

sealed interface RootlinkRoute {
    @Serializable
    data object Map : RootlinkRoute

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

    val rawResources = listOf(
        R.raw.trees_emilia_romagna,
        // R.raw.trees_sardegna
    )
    val json = Json { ignoreUnknownKeys = true }
    val trees = mutableListOf<Tree>()

    rawResources.forEach { resourceId ->
        val text = ctx.resources.openRawResource(resourceId)
            .bufferedReader()
            .use { it.readText() }
        trees += json.decodeFromString<List<Tree>>(text)
    }
    Log.i("TreeLoading", "Loaded trees")
    mapVM.actions.updateTrees(trees)

    NavHost(
        navController = navController,
        startDestination = RootlinkRoute.Map
    ) {
        composable<RootlinkRoute.Map> {
            Log.i("RootlinkNavGraph", "MapVM: $mapVM")
            MapScreen(
                mapState = mapState,
                mapActions = mapVM.actions,
                navController = navController
            )
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