package me.baldo.rootlink

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.remote.TreesDataSource
import me.baldo.rootlink.data.repositories.TreesRepository
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RootlinkApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val treesDataSource: TreesDataSource by inject()
    private val treesRepository: TreesRepository by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RootlinkApplication)
            modules(appModule)
        }
        loadTrees()
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }

    private fun loadTrees() {
        applicationScope.launch {
            val treesLocal = treesRepository.getAllTreesOneShot()
            treesRepository.loadNewTrees(treesLocal)
            treesDataSource.getTrees()?.let { treesUpdated ->
                treesRepository.loadNewTrees(treesUpdated)
            }
        }
    }
}
