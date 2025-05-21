package me.baldo.rootlink.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.composables.TopBar

@Composable
fun SettingsScreen(
    settingsState: SettingsState,
    settingsActions: SettingsActions,
    navController: NavHostController
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopBar(
                title = stringResource(R.string.screen_settings),
                onBackPressed = navController::navigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(28.dp))
            Category(stringResource(R.string.settings_category_map))
            SwitchRowWithDescription(
                text = stringResource(R.string.settings_map_show_all_trees),
                description = stringResource(R.string.settings_map_show_all_trees_details),
                checked = settingsState.showAllMonumentalTrees,
                onCheckedChange = settingsActions::onShowAllMonumentalTreesChanged
            )
        }
    }
}

@Composable
private fun Category(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 28.dp)
    )
}

@Composable
private fun SwitchRowWithDescription(
    text: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = true,
                role = Role.Switch,
                onClickLabel = stringResource(if (checked) R.string.settings_map_show_all_trees_off else R.string.settings_map_show_all_trees_on),
                onClick = { onCheckedChange(!checked) })
            .padding(28.dp)
    ) {
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}
