package me.baldo.rootlink.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.composables.TopBar

@Composable
fun StatsScreen(
    statsState: StatsState,
    navController: NavHostController
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopBar(
                title = stringResource(R.string.screen_stats),
                onBackPressed = navController::navigateUp
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                StatRow(
                    label = stringResource(R.string.stats_total_trees),
                    currentValue = statsState.treesInteractedWith.currentValue,
                    maxValue = statsState.treesInteractedWith.maxValue
                )
            }
            item {
                StatRow(
                    label = stringResource(R.string.stats_regions),
                    currentValue = statsState.regionsInteractedWith.currentValue,
                    maxValue = statsState.regionsInteractedWith.maxValue
                )
            }
            items(statsState.treesInRegionsInteractedWith.toList()) { (region, stat) ->
                StatRow(
                    label = stringResource(R.string.stats_region, region.uppercase()),
                    currentValue = stat.currentValue,
                    maxValue = stat.maxValue
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, currentValue: Int, maxValue: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            softWrap = true
        )
        StatCircle(currentValue, maxValue)
    }
}

@Composable
private fun StatCircle(currentValue: Int, maxValue: Int) {
    val progress = if (maxValue > 0) currentValue.toFloat() / maxValue else 0f
    val strokeWidth = 16.dp
    val circleSize = 80.dp
    val boxSize = circleSize + strokeWidth
    val color = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier.size(boxSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(circleSize)) {
            drawCircle(
                color = Color.LightGray,
                style = Stroke(width = strokeWidth.toPx())
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx())
            )
        }
        Text(
            text = "$currentValue/$maxValue",
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}