package me.baldo.rootlink.ui.screens.airqualitymap

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import me.baldo.rootlink.BuildConfig
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.BottomBarTab
import me.baldo.rootlink.ui.composables.HomeOverlay
import me.baldo.rootlink.utils.isOnline
import me.baldo.rootlink.utils.openWirelessSettings
import java.net.MalformedURLException
import java.net.URL

@Composable
fun AirQualityMapScreen(
    airQualityMapState: AirQualityMapState,
    airQualityMapActions: AirQualityMapActions,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    fun update() {
        airQualityMapActions.setShowNoInternetConnectivityWarning(!isOnline(ctx))
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        update()
    }

    HomeOverlay(
        selectedTab = BottomBarTab.AirQualityMap,
        onBottomTabClick = { tab ->
            if (tab != BottomBarTab.AirQualityMap) {
                navController.navigate(tab.screen)
            }
        },
        navController = navController
    ) { innerPadding ->
        when {
            airQualityMapState.showNoInternetConnectivityWarning ->
                Warning(
                    icon = Icons.Outlined.CloudOff,
                    title = stringResource(R.string.map_internet_disabled),
                    description = stringResource(R.string.map_internet_disabled_explanation),
                    buttonText = stringResource(R.string.map_internet_disabled_button),
                    modifier = modifier.padding(innerPadding)
                ) { openWirelessSettings(ctx) }

            else ->
                Map(
                    modifier = modifier.padding(innerPadding)
                )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun Map(
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(42.7189196, 12.4998566), 5.55f)
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) {
        val tileProvider = remember { HeatmapTileProvider(BuildConfig.MAPS_KEY) }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                latLngBoundsForCameraTarget = LatLngBounds(
                    LatLng(36.331253, 6.505598),
                    LatLng(47.224792, 18.774244)
                ),
                mapType = MapType.NORMAL,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    ctx,
                    R.raw.map_style_airquality
                )
            ),
            uiSettings = MapUiSettings(
                // zoomGesturesEnabled = false,
                // scrollGesturesEnabled = false,
                // scrollGesturesEnabledDuringRotateOrZoom = false,
                // rotationGesturesEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false,
                zoomControlsEnabled = false
            ),
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
        ) {
            TileOverlay(
                tileProvider = tileProvider,
                zIndex = 1f,
                fadeIn = true,
                transparency = 0.5f
            )
        }
    }
}

class HeatmapTileProvider(
    private val apiKey: String
) : UrlTileProvider(256, 256) {
    override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
        val url =
            "https://airquality.googleapis.com/v1/mapTypes/US_AQI/heatmapTiles/$zoom/$x/$y?key=$apiKey"
        return try {
            URL(url)
        } catch (_: MalformedURLException) {
            null
        }
    }
}

@Composable
private fun Warning(
    icon: ImageVector,
    title: String,
    description: String,
    buttonText: String,
    modifier: Modifier = Modifier,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(Modifier.height(16.dp))
        Button(onAction) {
            Text(buttonText)
        }
    }
}
