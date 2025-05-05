package me.baldo.rootlink.ui.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import me.baldo.rootlink.R

@Composable
fun MapScreen(modifier: Modifier) {
    val ctx = LocalContext.current
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(ctx, R.raw.map_style)
        ),
        uiSettings = MapUiSettings(
            tiltGesturesEnabled = false,
            zoomControlsEnabled = false
        )
    ) {
        MarkerComposable(
            state = rememberUpdatedMarkerState(singapore),
            onClick = { false }
        ) {
            Icon(
                painter = painterResource(R.drawable.tree_sample),
                contentDescription = "Tree sample",
                tint = Color.Unspecified
            )
        }
    }
}