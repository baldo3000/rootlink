package me.baldo.rootlink

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.repositories.TreesRepository
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RootlinkApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RootlinkApplication)
            modules(appModule)
        }

        val treesRepository: TreesRepository by inject()

        val rawResources = listOf(
            R.raw.trees_emilia_romagna,
            // R.raw.trees_sardegna
        )
        val json = Json { ignoreUnknownKeys = true }


        rawResources.forEach { resourceId ->
            val text = resources.openRawResource(resourceId)
                .bufferedReader()
                .use { it.readText() }
            json.decodeFromString<List<Tree>>(text).forEach { tree ->
                applicationScope.launch {
                    treesRepository.upsertTree(tree)
                }
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}
