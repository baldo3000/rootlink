package me.baldo.rootlink.ui.screens.catalog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.BottomBarTab
import me.baldo.rootlink.ui.RootlinkRoute
import me.baldo.rootlink.ui.composables.HomeOverlay
import java.util.Locale

@Composable
fun CatalogScreen(
    catalogState: CatalogState,
    catalogActions: CatalogActions,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    HomeOverlay(
        selectedTab = BottomBarTab.Catalog,
        onBottomTabClick = { tab ->
            if (tab != BottomBarTab.Catalog) {
                navController.navigate(tab.screen)
            }
        },
        navController = navController
    ) { innerPadding ->
        // Group trees by region
        val groupedTreesByRegion = catalogState.trees.groupBy { it.region }
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            groupedTreesByRegion.forEach { (region, regionTrees) ->
                item {
                    SectionCard(title = region.uppercase()) {
                        val groupedTreesByMunicipality = regionTrees.groupBy { it.province }
                        groupedTreesByMunicipality.forEach { (province, provinceTrees) ->
                            SectionCard(
                                title = province.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                }
                            ) {
                                provinceTrees.forEach { tree ->
                                    TreeCard(
                                        species = tree.species,
                                        id = tree.cardId,
                                        onTreeClick = {
                                            navController.navigate(RootlinkRoute.TreeInfo(tree.cardId))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription =
                        if (expanded) stringResource(R.string.catalog_action_collapse)
                        else stringResource(R.string.catalog_action_expand)
                )
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun TreeCard(
    species: String,
    id: String,
    onTreeClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { onTreeClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = id.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = species.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = stringResource(R.string.catalog_open_tree_info)
                )
            }
        }
    }
}
