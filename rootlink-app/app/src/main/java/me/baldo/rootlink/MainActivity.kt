package me.baldo.rootlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.baldo.rootlink.ui.screens.chat.ChatScreen
import me.baldo.rootlink.ui.screens.chat.ChatViewModel
import me.baldo.rootlink.ui.screens.map.MapScreen
import me.baldo.rootlink.ui.theme.RootlinkTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RootlinkTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val chatVM: ChatViewModel = koinViewModel()
                    val chatState by chatVM.state.collectAsStateWithLifecycle()
                    ChatScreen(
                        chatState = chatState,
                        chatActions = chatVM.actions,
                        modifier = Modifier.padding(innerPadding)
                    )
                    // MapScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}
