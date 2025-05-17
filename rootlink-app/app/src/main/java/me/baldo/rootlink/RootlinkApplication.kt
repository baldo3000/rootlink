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
            R.raw.trees_sardegna,
            R.raw.trees_abruzzo,
            R.raw.trees_bolzano,
            R.raw.trees_campania,
            R.raw.trees_friuli_venezia_giulia,
            R.raw.trees_liguria,
            R.raw.trees_marche,
            R.raw.trees_piemonte,
            R.raw.trees_toscana,
            R.raw.trees_umbria,
            R.raw.trees_veneto,
            R.raw.trees_basilicata,
            R.raw.trees_calabria,
            R.raw.trees_lazio,
            R.raw.trees_lombardia,
            R.raw.trees_molise,
            R.raw.trees_puglia,
            R.raw.trees_sicilia,
            R.raw.trees_trento,
            R.raw.trees_valle_daosta
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
