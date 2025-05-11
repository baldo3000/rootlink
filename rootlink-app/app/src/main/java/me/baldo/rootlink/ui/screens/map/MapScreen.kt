package me.baldo.rootlink.ui.screens.map

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import me.baldo.rootlink.R
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.ui.RootlinkRoute
import me.baldo.rootlink.ui.composables.HomeOverlay
import me.baldo.rootlink.ui.BottomBarTab
import me.baldo.rootlink.utils.parseCoordinate

@Composable
fun MapScreen(
    mapState: MapState,
    mapActions: ChatActions,
    openTreeChat: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState()

    HomeOverlay(
        selectedTab = BottomBarTab.Map,
        onBottomTabClick = { tab ->
            if (tab != BottomBarTab.Map) {
                navController.navigate(tab.screen)
            }
        }
    ) { innerPadding ->
        Map(
            trees = mapState.trees,
            cameraPositionState = cameraPositionState,
            onTreeClick = {
                openTreeChat(it.cardId)
                navController.navigate(RootlinkRoute.Chat)
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun Map(
    cameraPositionState: CameraPositionState,
    trees: List<Tree>,
    onTreeClick: (Tree) -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(ctx, R.raw.map_style)
        ),
        uiSettings = MapUiSettings(
            mapToolbarEnabled = false,
            zoomControlsEnabled = false
        ),
        mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
    ) {
        val startTime = System.currentTimeMillis()
        for (tree in trees) {
            val lat: Double? =
                runCatching { parseCoordinate(tree.latitude) }.getOrElse { null }
            val lon: Double? =
                runCatching { parseCoordinate(tree.longitude) }.getOrElse { null }
            if (lat != null && lon != null) {
                MarkerInfoWindowComposable(
                    state = rememberUpdatedMarkerState(LatLng(lat, lon)),
                    onClick = {
                        onTreeClick(tree)
                        false
                    },
                    infoContent = {
                        Text(tree.species)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.tree_sample),
                        contentDescription = tree.species,
                        tint = Color.Unspecified
                    )
                }
            }
        }
        val endTime = System.currentTimeMillis()
        Log.i("TIME", "Markers drawn in ${endTime - startTime} ms")
    }
}