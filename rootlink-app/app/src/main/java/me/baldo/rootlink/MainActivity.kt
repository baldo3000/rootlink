package me.baldo.rootlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.json.Json
import me.baldo.rootlink.data.model.Tree
import me.baldo.rootlink.ui.screens.chat.ChatViewModel
import me.baldo.rootlink.ui.screens.map.MapScreen
import me.baldo.rootlink.ui.screens.map.MapViewModel
import me.baldo.rootlink.ui.theme.RootlinkTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val chatVM: ChatViewModel = koinViewModel()
            val chatState by chatVM.state.collectAsStateWithLifecycle()
            val mapVM: MapViewModel = koinViewModel()
            val mapState by mapVM.state.collectAsStateWithLifecycle()

            val rawResources = listOf(
                R.raw.trees_emilia_romagna,
                // R.raw.trees_sardegna
            )
            val json = Json { ignoreUnknownKeys = true }
            val trees = mutableListOf<Tree>()

            rawResources.forEach { resourceId ->
                val text = resources.openRawResource(resourceId)
                    .bufferedReader()
                    .use { it.readText() }
                trees += json.decodeFromString<List<Tree>>(text)
            }

            mapVM.actions.updateTrees(trees)
            RootlinkTheme {
                MapScreen(
                    mapState = mapState,
                    mapActions = mapVM.actions
                )
            }
        }
    }
}
