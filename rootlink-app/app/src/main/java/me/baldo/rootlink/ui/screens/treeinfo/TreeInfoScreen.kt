package me.baldo.rootlink.ui.screens.treeinfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.composables.TopBar
import java.util.Locale

@Composable
fun TreeInfoScreen(
    treeInfoState: TreeInfoState,
    treeInfoActions: TreeInfoActions,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopBar(
                title = stringResource(R.string.screen_tree_info),
                onBackPressed = navController::navigateUp
            )
        }
    ) { innerPadding ->
        if (treeInfoState.tree != null) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Identification & Location
                SectionCard(title = stringResource(R.string.tree_info_section_identification)) {
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_id),
                        value = treeInfoState.tree.cardId
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_region),
                        value = treeInfoState.tree.region
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_province),
                        value = treeInfoState.tree.province
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_municipality),
                        value = treeInfoState.tree.municipality
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_location),
                        value = treeInfoState.tree.location
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Coordinates & Altitude
                SectionCard(title = stringResource(R.string.tree_info_section_coordinates)) {
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_latitude),
                        value = treeInfoState.tree.latitude
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_longitude),
                        value = treeInfoState.tree.longitude
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_altitude),
                        value = treeInfoState.tree.altitude.toString()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Species Info
                SectionCard(title = stringResource(R.string.tree_info_section_species)) {
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_species),
                        value = treeInfoState.tree.species
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_species_scientific),
                        value = treeInfoState.tree.speciesScientificName
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Measurements
                SectionCard(title = stringResource(R.string.tree_info_section_measurements)) {
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_circumference),
                        value = treeInfoState.tree.circumference.toString()
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_height),
                        value = treeInfoState.tree.height.toString()
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_age),
                        value = treeInfoState.tree.age.toString()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Other
                SectionCard(title = stringResource(R.string.tree_info_section_other)) {
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_monumentality_criteria),
                        value = treeInfoState.tree.monumentalityCriteria,
                        capitalize = false
                    )
                    InfoRow(
                        label = stringResource(R.string.tree_info_value_public_interest),
                        value = if (treeInfoState.tree.significantPublicInterest) stringResource(R.string.tree_info_yes) else stringResource(
                            R.string.tree_info_no
                        )
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Text(stringResource(R.string.tree_info_unknown))
            }
        }

    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    imageVector = if (expanded) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
                    contentDescription =
                        if (expanded) stringResource(R.string.tree_info_action_collapse)
                        else stringResource(R.string.tree_info_action_expand)
                )
            }

            val transitionState =
                remember { MutableTransitionState(expanded).apply { targetState = expanded } }
            AnimatedVisibility(visibleState = transitionState) {
                if (expanded) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.12f)
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    capitalize: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (capitalize)
                value.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                } else value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}