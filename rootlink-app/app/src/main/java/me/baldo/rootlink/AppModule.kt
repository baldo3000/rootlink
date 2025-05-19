package me.baldo.rootlink

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.baldo.rootlink.data.database.RootlinkLocalDatabase
import me.baldo.rootlink.data.remote.AirQualityDataSource
import me.baldo.rootlink.data.remote.MessagesDataSource
import me.baldo.rootlink.data.repositories.SettingsRepository
import me.baldo.rootlink.data.repositories.TreesRepository
import me.baldo.rootlink.ui.screens.catalog.CatalogViewModel
import me.baldo.rootlink.ui.screens.chat.ChatViewModel
import me.baldo.rootlink.ui.screens.map.MapViewModel
import me.baldo.rootlink.ui.screens.settings.SettingsViewModel
import me.baldo.rootlink.ui.screens.treeinfo.TreeInfoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

private val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single { MessagesDataSource(get()) }
    single { AirQualityDataSource(get()) }

    viewModel { ChatViewModel(get(), get(), get()) }
    viewModel { MapViewModel(get()) }
    viewModel { TreeInfoViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { CatalogViewModel(get()) }

    single {
        Room.databaseBuilder(
            get(),
            RootlinkLocalDatabase::class.java,
            "rootlink"
        )
            // TODO: Use a proper migration strategy
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { TreesRepository(get<RootlinkLocalDatabase>().treesDAO()) }
    single { SettingsRepository(get()) }

    single {
        HttpClient(OkHttp) {
            engine {
                config {
                    // WARNING: Allowing every hostname is insecure and should not be used in production
                    hostnameVerifier { _, _ -> true }
                    // WARNING: Allowing every certificate is insecure and should not be used in production
                    sslSocketFactory(createInsecureSslSocketFactory(), createInsecureTrustManager())
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}

private fun createInsecureSslSocketFactory(): SSLSocketFactory {
    val trustAllCertificates = arrayOf<X509TrustManager>(createInsecureTrustManager())
    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustAllCertificates, SecureRandom())
    }
    return sslContext.socketFactory
}

@SuppressLint("TrustAllX509TrustManager", "CustomX509TrustManager")
private fun createInsecureTrustManager(): X509TrustManager {
    return object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
    }
}