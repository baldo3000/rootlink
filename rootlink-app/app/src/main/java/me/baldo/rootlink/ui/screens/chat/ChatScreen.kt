package me.baldo.rootlink.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.baldo.rootlink.R
import me.baldo.rootlink.data.database.ChatMessage
import me.baldo.rootlink.ui.composables.AppBarWithDrawer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    chatState: ChatState,
    chatActions: ChatActions,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(chatState.chatMessages.size) {
        if (chatState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(index = chatState.chatMessages.lastIndex)
        }
    }

    AppBarWithDrawer(
        title = stringResource(R.string.screens_chat)
    ) { innerPadding ->
        Scaffold(
            modifier = modifier.padding(top = innerPadding.calculateTopPadding()),
            bottomBar = {
                OutlinedTextField(
                    value = chatState.fieldText,
                    placeholder = { Text("Make a question...") },
                    onValueChange = { chatActions.updateFieldText(it) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(bottom = 16.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            val message = chatState.fieldText
                            if (message.isNotEmpty()) chatActions.sendMessage()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, "Send")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                items(chatState.chatMessages) { message ->
                    ChatBubble(
                        message = message,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = this.maxWidth * 0.25f, max = this.maxWidth * 0.75f)
                .background(
                    color = when (message.role) {
                        "user" -> MaterialTheme.colorScheme.primaryContainer
                        "assistant" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> error("Unknown role")
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .align(
                    when (message.role) {
                        "user" -> Alignment.TopEnd
                        "assistant" -> Alignment.TopStart
                        else -> error("Unknown role")
                    }
                )
        ) {
            Text(
                text = message.content,
                color = when (message.role) {
                    "user" -> MaterialTheme.colorScheme.onPrimaryContainer
                    "assistant" -> MaterialTheme.colorScheme.onTertiaryContainer
                    else -> error("Unknown role")
                },
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.createdAt),
                color = when (message.role) {
                    "user" -> MaterialTheme.colorScheme.onPrimaryContainer
                    "assistant" -> MaterialTheme.colorScheme.onTertiaryContainer
                    else -> error("Unknown role")
                },
                style = LocalTextStyle.current.copy(fontSize = 12.sp),
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatPreview() {
    val messageList = listOf<ChatMessage>(
        ChatMessage(
            treeId = "",
            role = "user",
            content = "Buondi albero saggio,\ncome va??",
            createdAt = Date()
        ),
        ChatMessage(
            treeId = "",
            role = "assistant",
            content = "Ma salve,\ntutto bene qua",
            createdAt = Date()
        ),
        ChatMessage(
            treeId = "",
            role = "user",
            content = "Ma ne sei sicuro?",
            createdAt = Date()
        ),
        ChatMessage(
            treeId = "",
            role = "assistant",
            content = "Sicurissimo!",
            createdAt = Date()
        ),
    )
    ChatScreen(
        chatActions = object : ChatActions {},
        chatState = ChatState(chatMessages = messageList),
        navController = rememberNavController()
    )
}
