package me.baldo.rootlink.ui.screens.settings

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.baldo.rootlink.R
import me.baldo.rootlink.SetupActivity
import me.baldo.rootlink.ui.composables.TopBar

@Composable
fun SettingsScreen(
    settingsState: SettingsState,
    settingsActions: SettingsActions,
    navController: NavHostController
) {
    val ctx = LocalContext.current

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
                onLabel = stringResource(R.string.settings_map_show_all_trees_on),
                offLabel = stringResource(R.string.settings_map_show_all_trees_off),
                checked = settingsState.showAllMonumentalTrees,
                onCheckedChange = settingsActions::onShowAllMonumentalTreesChanged
            )

            Spacer(Modifier.height(28.dp))
            Category(stringResource(R.string.settings_category_debug))
            SwitchRowWithDescription(
                text = stringResource(R.string.settings_debug_developer_options),
                onLabel = stringResource(R.string.settings_debug_developer_options_on),
                offLabel = stringResource(R.string.settings_debug_developer_options_off),
                checked = settingsState.showDeveloperOptions,
                onCheckedChange = settingsActions::onShowDeveloperOptions
            )
            if (settingsState.showDeveloperOptions) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    RowButton(
                        text = stringResource(R.string.settings_debug_launch_setup),
                        clickLabel = stringResource(R.string.settings_debug_launch_setup),
                        onClick = {
                            ctx.startActivity(Intent(ctx, SetupActivity::class.java))
                        }
                    )
                }
            }
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
    description: String? = null,
    onLabel: String,
    offLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Switch,
                onClickLabel = if (checked) offLabel else onLabel,
                onClick = { onCheckedChange(!checked) })
            .padding(28.dp)
    ) {
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
            )
            if (description != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = null
        )
    }
}

@Composable
private fun RowButton(
    text: String,
    description: String? = null,
    clickLabel: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick,
                onClickLabel = clickLabel,
            )
            .padding(28.dp)
    ) {
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
            )
            if (description != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
