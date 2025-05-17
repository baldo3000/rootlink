package me.baldo.rootlink.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.BottomBarTab
import me.baldo.rootlink.ui.RootlinkRoute

@Composable
fun HomeOverlay(
    selectedTab: BottomBarTab,
    onBottomTabClick: (BottomBarTab) -> Unit,
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerState = drawerState
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon_round),
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier
                                .height(96.dp)
                        )
                        Text(
                            stringResource(R.string.app_name),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.menu_account),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_item_login)) },
                        selected = false,
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.Login,
                                contentDescription = stringResource(R.string.menu_item_login)
                            )
                        },
                        onClick = { /* TODO: handle click */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = stringResource(R.string.menu_utils),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_item_catalog)) },
                        selected = false,
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.ViewList,
                                contentDescription = stringResource(R.string.menu_item_catalog)
                            )
                        },
                        onClick = { /* TODO: handle click */ }
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_item_source)) },
                        selected = false,
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.OpenInNew,
                                contentDescription = stringResource(R.string.menu_item_source)
                            )
                        },
                        onClick = { /* TODO: handle click */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = stringResource(R.string.menu_options),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_item_settings)) },
                        selected = false,
                        icon = {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = stringResource(R.string.menu_item_settings)
                            )
                        },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(RootlinkRoute.Settings)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.menu_item_help)) },
                        selected = false,
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.Help,
                                contentDescription = stringResource(R.string.menu_item_help)
                            )
                        },
                        onClick = { /* TODO: handle click */ }
                    )
                }
            }
        },
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            topBar = {
                HomeTopBar(
                    title = selectedTab.toString(),
                ) {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }
            },
            bottomBar = { BottomBar(selectedTab, onBottomTabClick) }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}
