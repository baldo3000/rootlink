package me.baldo.rootlink.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            NavCard(
                stringResource(R.string.screen_map),
                description = stringResource(R.string.screen_map_description),
                important = true,
                icon = BottomBarTab.Map.icon
            ) {
                navController.navigate(RootlinkRoute.Map)
            }
            Spacer(Modifier.height(16.dp))
            NavCard(
                stringResource(R.string.screen_air_quality_map),
                description = stringResource(R.string.screen_air_quality_map_description),
                icon = BottomBarTab.AirQualityMap.icon
            ) {
                navController.navigate(RootlinkRoute.AirQualityMap)
            }
            Spacer(Modifier.height(16.dp))
            NavCard(
                stringResource(
                    R.string.screen_catalog
                ),
                description = stringResource(R.string.screen_catalog_description),
                icon = BottomBarTab.Catalog.icon
            ) {
                navController.navigate(RootlinkRoute.Catalog)
            }
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun NavCard(
    label: String,
    description: String,
    icon: ImageVector,
    important: Boolean = false,
    onClick: () -> Unit
) {
    val containerColor = if (important) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }
    val textColor = if (important) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSecondary
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
        }
    }
}