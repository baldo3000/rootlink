package me.baldo.rootlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import me.baldo.rootlink.ui.RootlinkNavGraph
import me.baldo.rootlink.ui.theme.RootlinkTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RootlinkTheme {
                val navController = rememberNavController()
                RootlinkNavGraph(navController)
            }
        }
    }
}
