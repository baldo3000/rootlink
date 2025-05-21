package me.baldo.rootlink.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.baldo.rootlink.R
import me.baldo.rootlink.ui.composables.TopBar

@Composable
fun ProfileScreen(
    profileState: ProfileState,
    profileActions: ProfileActions,
    navController: NavHostController
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopBar(
                title = stringResource(R.string.screen_profile),
                onBackPressed = navController::navigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = profileState.name,
                onValueChange = profileActions::onSetName,
                label = {
                    Text(
                        text = stringResource(R.string.profile_name),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                placeholder = { Text(stringResource(R.string.profile_name_not_set)) },
                supportingText = { Text(stringResource(R.string.profile_name_support)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}