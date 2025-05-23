package me.baldo.rootlink.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.BottomBarTab
import me.baldo.rootlink.ui.RootlinkRoute
import me.baldo.rootlink.ui.composables.HomeOverlay

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    HomeOverlay(
        selectedTab = BottomBarTab.Home,
        onBottomTabClick = { tab ->
            if (tab != BottomBarTab.Home) {
                navController.navigate(tab.screen)
            }
        },
        navController = navController
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickNavButton(
                    stringResource(R.string.screen_map),
                    BottomBarTab.Map.icon
                ) {
                    navController.navigate(RootlinkRoute.Map)
                }
                QuickNavButton(
                    stringResource(R.string.screen_air_quality_map),
                    BottomBarTab.AirQualityMap.icon
                ) {
                    navController.navigate(RootlinkRoute.AirQualityMap)
                }
                QuickNavButton(
                    stringResource(
                        R.string.screen_catalog
                    ),
                    BottomBarTab.Catalog.icon
                ) {
                    navController.navigate(RootlinkRoute.Catalog)
                }
            }

            Button(
                onClick = { navController.navigate(RootlinkRoute.Map) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.home_explore_map))
            }

            Spacer(Modifier.height(128.dp))
        }
    }
}

@Composable
fun QuickNavButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(96.dp)
            .clickable(
                onClick = onClick,
                role = Role.Button,
                onClickLabel = label
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(48.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}