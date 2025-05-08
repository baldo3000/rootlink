package me.baldo.rootlink.ui.screens.map

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerInfoWindowComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import me.baldo.rootlink.R
import me.baldo.rootlink.data.model.Tree
import me.baldo.rootlink.ui.RootlinkRoute
import me.baldo.rootlink.ui.composables.AppBarWithDrawer
import me.baldo.rootlink.utils.parseCoordinate

@Composable
fun MapScreen(
    mapState: MapState,
    mapActions: ChatActions,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState()

    AppBarWithDrawer(
        title = stringResource(R.string.screens_map)
    ) { innerPadding ->
        Scaffold(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            bottomBar = {
                MapBottomBar(
                    tab = mapState.tab,
                    onTabClick = { mapActions.onTabChange(it) }
                )
            }
        ) { innerPadding ->
            when (mapState.tab) {
                MapTab.EXPLORE -> Explore(
                    trees = mapState.trees,
                    cameraPositionState = cameraPositionState,
                    onTreeClick = { navController.navigate(RootlinkRoute.Chat) },
                    modifier = modifier.padding(bottom = innerPadding.calculateBottomPadding())
                )

                MapTab.MAP -> Spacer(modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun MapBottomBar(
    tab: MapTab,
    onTabClick: (MapTab) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (tab == MapTab.EXPLORE) Icons.Filled.MyLocation else Icons.Outlined.MyLocation,
                    contentDescription = stringResource(R.string.map_explore)
                )
            },
            label = { Text(stringResource(R.string.map_explore)) },
            selected = tab == MapTab.EXPLORE,
            onClick = { onTabClick(MapTab.EXPLORE) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (tab == MapTab.MAP) Icons.Filled.Map else Icons.Outlined.Map,
                    contentDescription = stringResource(R.string.map_explore)
                )
            },
            label = { Text(stringResource(R.string.map_map)) },
            selected = tab == MapTab.MAP,
            onClick = { onTabClick(MapTab.MAP) }
        )
    }
}

@Composable
private fun Explore(
    cameraPositionState: CameraPositionState,
    trees: List<Tree>,
    onTreeClick: () -> Unit,
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
        )
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
                        onTreeClick()
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