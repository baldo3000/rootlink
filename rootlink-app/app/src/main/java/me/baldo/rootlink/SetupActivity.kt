package me.baldo.rootlink

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.repositories.SettingsRepository
import me.baldo.rootlink.ui.SetupNavGraph
import me.baldo.rootlink.ui.theme.RootlinkTheme
import org.koin.android.ext.android.inject

class SetupActivity : ComponentActivity() {
    companion object {
        private const val TAG = "SetupActivity"
    }

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val setupDone =
                settingsRepository.setupDone.first()
            if (setupDone) {
                startActivity(Intent(this@SetupActivity, MainActivity::class.java))
                finish()
            } else {
                setContent {
                    RootlinkTheme {
                        val navController = rememberNavController()
                        SetupNavGraph(navController)
                    }
                }
            }
        }
    }
}