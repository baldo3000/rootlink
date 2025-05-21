package me.baldo.rootlink.ui.screens.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.GpsNotFixed
import androidx.compose.material.icons.outlined.GpsOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.OpenWith
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.clustering.rememberClusterManager
import com.google.maps.android.compose.clustering.rememberClusterRenderer
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import me.baldo.rootlink.R
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.ui.BottomBarTab
import me.baldo.rootlink.ui.RootlinkRoute
import me.baldo.rootlink.ui.composables.HomeOverlay
import me.baldo.rootlink.utils.LocationService
import me.baldo.rootlink.utils.PermissionStatus
import me.baldo.rootlink.utils.calculateDistance
import me.baldo.rootlink.utils.isOnline
import me.baldo.rootlink.utils.openWirelessSettings
import me.baldo.rootlink.utils.rememberMultiplePermissions
import java.util.Locale

private const val INTERACTION_DISTANCE = 250.0

@Composable
fun MapScreen(
    mapState: MapState,
    mapActions: ChatActions,
    openTreeChat: (String) -> Unit,
    showAllTrees: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current

    val locationPermissions = rememberMultiplePermissions(
        listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    ) { statuses ->
        mapActions.disableAllWarnings()
        when {
            statuses.any { it.value == PermissionStatus.Granted } -> {}
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } -> {
                mapActions.setShowLocationPermissionPermanentlyDeniedWarning(true)
            }

            else -> {
                mapActions.setShowLocationPermissionDeniedWarning(true)
            }
        }
    }

    fun update() {
        mapActions.setShowNoInternetConnectivityWarning(!isOnline(ctx))
        locationPermissions.updateStatuses()
        if (locationPermissions.statuses.all { !it.value.isGranted }) {
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
        locationPermissions.launchPermissionRequest()
        update()
    }

    HomeOverlay(
        selectedTab = BottomBarTab.Map,
        onBottomTabClick = { tab ->
            if (tab != BottomBarTab.Map) {
                navController.navigate(tab.screen)
            }
        },
        navController = navController
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
                    trees = mapState.trees.filter { it.significantPublicInterest || showAllTrees },
                    isFollowingUser = mapState.isFollowingUser,
                    setFollowUser = mapActions::setFollowingUser,
                    onTreeInfoClick = {
                        navController.navigate(RootlinkRoute.TreeInfo(it.cardId))
                    },
                    onTreeChatClick = {
                        openTreeChat(it.cardId)
                        navController.navigate(RootlinkRoute.Chat)
                    },
                    modifier = modifier.padding(innerPadding)
                )
        }
    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
private fun Map(
    trees: List<Tree>,
    isFollowingUser: Boolean,
    setFollowUser: (Boolean) -> Unit,
    onTreeInfoClick: (Tree) -> Unit,
    onTreeChatClick: (Tree) -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val locationService = remember { LocationService(ctx) }
    val scope = rememberCoroutineScope()
    var selectedTree by remember { mutableStateOf<Tree>(Tree()) }
    var showTreeDialog by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(42.7189196, 12.8998566), 6f)
    }
    val userPosition = rememberCameraPositionState()
    val fusedLocationClient = remember { getFusedLocationProviderClient(ctx) }
    val currentIsFollowingUser by rememberUpdatedState(isFollowingUser)
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations.reversed()) {
                    if (location != null) {
                        val position = CameraPosition(
                            LatLng(location.latitude, location.longitude),
                            cameraPositionState.position.zoom,
                            cameraPositionState.position.tilt,
                            0f
                        )
                        userPosition.position = position
                        if (currentIsFollowingUser) {
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newCameraPosition(
                                        position
                                    )
                                )
                            }
                        }
                        return
                    }
                }
            }
        }
    }

    // Start location updates when entering the Map route
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.Builder(1000L).build(),
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    // Stop location updates when exiting the Map route
    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (isFollowingUser) {
                FollowingUserFAB {
                    setFollowUser(false)
                }
            } else {
                NotFollowingUserFAB {
                    scope.launch {
                        locationService.getCurrentLocation()?.let { newPosition ->
                            cameraPositionState.animate(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(
                                        LatLng(newPosition.latitude, newPosition.longitude),
                                        maxOf(cameraPositionState.position.zoom, 9f), 0f, 0f
                                    )
                                )
                            )
                        }
                    }
                    setFollowUser(true)
                }
            }
        }
    ) { innerPadding ->
        val tmp = innerPadding
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                latLngBoundsForCameraTarget = LatLngBounds(
                    LatLng(36.331253, 6.505598),
                    LatLng(47.224792, 18.774244)
                ),
                mapType = MapType.NORMAL,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(ctx, R.raw.map_style),
                minZoomPreference = 6f
            ),
            uiSettings = MapUiSettings(
                scrollGesturesEnabled = !currentIsFollowingUser,
                scrollGesturesEnabledDuringRotateOrZoom = !currentIsFollowingUser,
                rotationGesturesEnabled = !currentIsFollowingUser,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false,
                zoomControlsEnabled = false
            ),
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
        ) {
            val windowInfo = LocalWindowInfo.current
            val screenHeight = windowInfo.containerSize.height
            val screenWidth = windowInfo.containerSize.width
            val clusterManager = rememberClusterManager<Tree>()

            clusterManager?.setAlgorithm(
                NonHierarchicalViewBasedAlgorithm(screenWidth, screenHeight)
            )

            val renderer = rememberClusterRenderer(
                clusterContent = null,
                clusterItemContent = { tree ->
                    Icon(
                        painter = painterResource(R.drawable.tree),
                        contentDescription = tree.species,
                        tint = Color.Unspecified
                    )
                },
                clusterManager = clusterManager,
            )

            SideEffect {
                clusterManager ?: return@SideEffect
                clusterManager.setOnClusterItemClickListener { tree ->
                    selectedTree = tree
                    showTreeDialog = true
                    isFollowingUser
                }
            }
            SideEffect {
                if (clusterManager?.renderer != renderer) {
                    clusterManager?.renderer = renderer ?: return@SideEffect
                }
            }

            if (clusterManager != null) {
                Clustering(
                    items = trees,
                    clusterManager = clusterManager,
                )
            }

            // Draw a black circle around the user's location
            Circle(
                center = userPosition.position.target,
                radius = INTERACTION_DISTANCE,
                fillColor = Color.Black.copy(alpha = 0.1f),
                strokeColor = Color.Black,
                strokeWidth = 3f
            )
        }

        // Show a dialog or bottom sheet when a tree is selected
        selectedTree.let { tree ->
            if (showTreeDialog) {
                TreeInfoDialog(
                    tree = tree,
                    chatEnabled = calculateDistance(
                        userPosition.position.target,
                        selectedTree.position
                    ) <= INTERACTION_DISTANCE,
                    onDismiss = { showTreeDialog = false },
                    onInfoClick = {
                        showTreeDialog = false
                        onTreeInfoClick(tree)
                    },
                    onChatClick = {
                        showTreeDialog = false
                        onTreeChatClick(tree)
                    }
                )
            }
        }
    }
}

@Composable
private fun FollowingUserFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
    ) {
        Icon(
            Icons.Outlined.GpsFixed,
            stringResource(R.string.map_unfollow_user)
        )
    }
}

@Composable
private fun NotFollowingUserFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Icon(
            Icons.Outlined.GpsNotFixed,
            stringResource(R.string.map_follow_user)
        )
    }
}

@Composable
private fun TreeInfoDialog(
    tree: Tree,
    chatEnabled: Boolean,
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
            val text =
                if (chatEnabled) stringResource(R.string.map_tree_chat)
                else stringResource(R.string.map_tree_chat_disabled)
            Button(
                // enabled = chatEnabled,
                onClick = onChatClick
            ) {
                Icon(
                    Icons.Outlined.OpenWith,
                    text
                )
                Spacer(Modifier.width(8.dp))
                Text(text)
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
