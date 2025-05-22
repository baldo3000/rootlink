package me.baldo.rootlink.ui.screens.treeinfo

import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.composables.TopBar
import java.util.Locale

private data class AqiCategory(val start: Int, val end: Int, val color: Color)

private val aqiCategories = listOf(
    AqiCategory(0, 50, Color(0xff00e501)), // Good — green
    AqiCategory(51, 100, Color(0xfffeff01)), // Moderate — yellow
    AqiCategory(101, 150, Color(0xfffe7e01)), // Unhealthy for Sensitive — orange
    AqiCategory(151, 200, Color(0xffff0101)), // Unhealthy — red
    AqiCategory(201, 300, Color(0xff8e3e97)), // Very Unhealthy — purple
    AqiCategory(301, 400, Color(0xff7e0122))  // Hazardous — maroon
)

private val aqiCategoriesColorsDisplacement = listOf(
    Color(0xff00e501),
    Color(0xfffeff01),
    Color(0xfffe7e01),
    Color(0xffff0101),
    Color(0xff8e3e97),
    Color(0xff8e3e97),
    Color(0xff7e0122),
    Color(0xff7e0122)
)

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
                val aqiDescriptionFormatted =
                    if (treeInfoState.currentAqiDescription.isNotBlank()) treeInfoState.currentAqiDescription
                    else stringResource(R.string.tree_info_unknown_aqi)

                Text(
                    text = "${stringResource(R.string.tree_info_aqi)}: ${treeInfoState.currentAqiValue} - $aqiDescriptionFormatted",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                AqiBar(
                    treeInfoState.currentAqiValue,
                    labelColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))

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

/**
 * Draws a horizontal AQI bar with six colored segments,
 * plus a marker circle positioned according to [currentAqi].
 *
 * @param currentAqi the current AQI value (0…400). Values outside will be clamped.
 * @param height the thickness of the bar
 */
@Composable
private fun AqiBar(
    currentAqi: Int,
    height: Dp = 16.dp,
    labelTextSize: Dp = 12.dp,
    labelColor: Color = Color.Black
) {
    val aqi = currentAqi.coerceIn(0, 400)

    val gradient = Brush.horizontalGradient(
        colors = aqiCategoriesColorsDisplacement
    )
    val circleColor = MaterialTheme.colorScheme.surfaceContainer
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height + labelTextSize + 4.dp)
    ) {
        val w = size.width
        val h = height.toPx()

        // Draw the pill‐shaped gradient bar
        drawRoundRect(
            brush = gradient,
            topLeft = Offset(0f, 0f),
            size = androidx.compose.ui.geometry.Size(w, h),
            cornerRadius = CornerRadius(h / 2f, h / 2f)
        )

        // Draw the ping circle
        val pingCat = aqiCategories.first { aqi in it.start..it.end }
        val cx = (aqi / 400f) * w
        val cy = h / 2f
        val pingR = h * 0.75f

        drawCircle(
            color = pingCat.color,
            center = Offset(cx, cy),
            radius = pingR
        )
        drawCircle(
            color = circleColor,
            center = Offset(cx, cy),
            radius = pingR,
            style = Stroke(width = h * 0.1f)
        )

        // Prepare Paint for labels
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = labelTextSize.toPx()
            color = labelColor.toArgb()
        }
        // baseline Y: just below bar + small padding
        val y = h + 4.dp.toPx() - paint.ascent()

        // Draw internal boundary labels (50, 100, 150, 200, 300)
        paint.textAlign = Paint.Align.CENTER
        aqiCategories.forEach { cat ->
            if (cat.end < 400) {
                val x = (cat.end / 400f) * w
                drawContext.canvas.nativeCanvas.drawText(
                    cat.end.toString(),
                    x,
                    y,
                    paint
                )
            }
        }

        // Draw start (“0”) and end (“500”) labels
        // “0” at left
        paint.textAlign = Paint.Align.LEFT
        drawContext.canvas.nativeCanvas.drawText(
            "0",
            0f,
            y,
            paint
        )
        // “400” at right
        paint.textAlign = Paint.Align.RIGHT
        drawContext.canvas.nativeCanvas.drawText(
            "400+",
            w,
            y,
            paint
        )
    }
}