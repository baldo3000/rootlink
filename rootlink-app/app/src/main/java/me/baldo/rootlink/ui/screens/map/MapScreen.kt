package me.baldo.rootlink.ui.screens.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.GpsOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.OpenWith
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
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
import me.baldo.rootlink.ui.BottomBarTab
import me.baldo.rootlink.ui.RootlinkRoute
import me.baldo.rootlink.ui.composables.HomeOverlay
import me.baldo.rootlink.utils.PermissionStatus
import me.baldo.rootlink.utils.isOnline
import me.baldo.rootlink.utils.openWirelessSettings
import me.baldo.rootlink.utils.parseCoordinate
import me.baldo.rootlink.utils.rememberMultiplePermissions
import java.util.Locale

@Composable
fun MapScreen(
    mapState: MapState,
    mapActions: ChatActions,
    openTreeChat: (String) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    val locationPermissions = rememberMultiplePermissions(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    ) { statuses ->
        when {
            statuses.any { it.value == PermissionStatus.Granted } -> {}
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } -> {
                mapActions.disableAllWarnings()
                mapActions.setShowLocationPermissionPermanentlyDeniedWarning(true)
            }

            else -> {
                mapActions.disableAllWarnings()
                mapActions.setShowLocationPermissionDeniedWarning(true)
            }
        }
    }

    fun update() {
        mapActions.setShowNoInternetConnectivityWarning(!isOnline(ctx))
        if (locationPermissions.statuses.any { !it.value.isGranted }) {
            if (locationPermissions.shouldShowRequestPermissionRationale()) {
                mapActions.setShowLocationPermissionDeniedWarning(true)
            } else {
                mapActions.setShowLocationPermissionPermanentlyDeniedWarning(true)
            }
        } else {
            mapActions.setShowLocationPermissionDeniedWarning(false)
            mapActions.setShowLocationPermissionPermanentlyDeniedWarning(false)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        locationPermissions.updateStatuses()
        update()
    }

    HomeOverlay(
        selectedTab = BottomBarTab.Map,
        onBottomTabClick = { tab ->
            if (tab != BottomBarTab.Map) {
                navController.navigate(tab.screen)
            }
        }
    ) { innerPadding ->
        when {
            mapState.showLocationPermissionDeniedWarning ->
                Warning(
                    icon = Icons.Outlined.GpsOff,
                    title = stringResource(R.string.map_location_permissions_missing),
                    description = stringResource(R.string.map_location_permissions_missing_explanation),
                    buttonText = stringResource(R.string.map_location_permissions_missing_button),
                    modifier = modifier.padding(innerPadding)
                ) { locationPermissions.launchPermissionRequest() }

            mapState.showLocationPermissionPermanentlyDeniedWarning ->
                Warning(
                    icon = Icons.Outlined.GpsOff,
                    title = stringResource(R.string.map_location_permissions_missing_permanently),
                    description = stringResource(R.string.map_location_permissions_missing_permanently_explanation),
                    buttonText = stringResource(R.string.map_location_permissions_missing_permanently_button),
                    modifier = modifier.padding(innerPadding)
                ) {
                    ctx.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", ctx.packageName, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                }

            mapState.showNoInternetConnectivityWarning ->
                Warning(
                    icon = Icons.Outlined.CloudOff,
                    title = stringResource(R.string.map_internet_disabled),
                    description = stringResource(R.string.map_internet_disabled_explanation),
                    buttonText = stringResource(R.string.map_internet_disabled_button),
                    modifier = modifier.padding(innerPadding)
                ) { openWirelessSettings(ctx) }

            else ->
                Map(
                    trees = mapState.trees,
                    cameraPositionState = cameraPositionState,
                    onTreeChatClick = {
                        openTreeChat(it.cardId)
                        navController.navigate(RootlinkRoute.Chat)
                    },
                    modifier = modifier.padding(innerPadding)
                )
        }
    }
}

@Composable
private fun Map(
    cameraPositionState: CameraPositionState,
    trees: List<Tree>,
    onTreeChatClick: (Tree) -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    var selectedTree by remember { mutableStateOf<Tree?>(null) }
    var showTreeDialog by remember { mutableStateOf(false) }

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
                    onInfoWindowClick = { selectedTree = tree; showTreeDialog = true },
                    onInfoWindowClose = { selectedTree = null },
                    infoContent = { TreeInfoWindow(tree) }
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

    // Show a dialog or bottom sheet when a tree is selected
    selectedTree?.let { tree ->
        if (showTreeDialog) {
            TreeInfoDialog(
                tree = tree,
                onDismiss = { showTreeDialog = false },
                onInfoClick = { },
                onChatClick = {
                    onTreeChatClick(tree)
                    showTreeDialog = false
                }
            )
        }
    }
}

@Composable
private fun TreeInfoWindow(tree: Tree) {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .sizeIn(minWidth = 128.dp, minHeight = 64.dp, maxWidth = 256.dp, maxHeight = 160.dp)
            .padding(12.dp),
    ) {
        Text(
            text = tree.species.uppercase(),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = tree.location.replaceFirstChar { it.titlecase(Locale.getDefault()) },
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(4.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            onClick = {}
        ) {
            Icon(
                Icons.Outlined.OpenWith,
                stringResource(R.string.map_tree_expand)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.map_tree_expand))
        }

    }
}

@Composable
private fun TreeInfoDialog(
    tree: Tree,
    onDismiss: () -> Unit,
    onInfoClick: () -> Unit,
    onChatClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(text = tree.species.uppercase())
                Text(
                    text = tree.speciesScientificName,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        text = {
            Text(text = tree.location.replaceFirstChar { it.titlecase(Locale.getDefault()) })
        },
        confirmButton = {
            Button(
                onClick = onChatClick
            ) {
                Icon(
                    Icons.Outlined.OpenWith,
                    stringResource(R.string.map_tree_chat)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.map_tree_chat))
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                onClick = onInfoClick
            ) {
                Icon(
                    Icons.Outlined.Info,
                    stringResource(R.string.map_tree_info)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.map_tree_info))
            }
        }
    )
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
