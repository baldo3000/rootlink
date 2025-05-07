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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.model.ChatMessage
import me.baldo.rootlink.data.model.ChatRole
import me.baldo.rootlink.data.remote.MessagesDataSource
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    chatState: ChatState,
    chatActions: ChatActions,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val messagesDataSource = koinInject<MessagesDataSource>()

    val listState = rememberLazyListState()
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(index = chatState.messages.lastIndex)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            OutlinedTextField(
                value = chatState.fieldText,
                placeholder = { Text("Make a question...") },
                onValueChange = { chatActions.updateFieldText(it) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        val message = chatState.fieldText
                        if (message.isNotEmpty()) {
                            chatActions.addMessage(
                                ChatMessage(
                                    role = ChatRole.USER,
                                    content = chatState.fieldText,
                                    createdAt = Date()
                                )
                            )
                            scope.launch {
                                val response = messagesDataSource.sendMessage(chatState.fieldText)
                                chatActions.addMessage(response)
                            }
                            chatActions.updateFieldText("")
                        }
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
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            items(chatState.messages) { message ->
                ChatBubble(
                    message = message,
                    modifier = Modifier.fillMaxWidth()
                )
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
                        ChatRole.USER -> MaterialTheme.colorScheme.primaryContainer
                        ChatRole.ASSISTANT -> MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .align(
                    when (message.role) {
                        ChatRole.USER -> Alignment.TopEnd
                        ChatRole.ASSISTANT -> Alignment.TopStart
                    }
                )
        ) {
            Text(
                text = message.content,
                color = when (message.role) {
                    ChatRole.USER -> MaterialTheme.colorScheme.onPrimaryContainer
                    ChatRole.ASSISTANT -> MaterialTheme.colorScheme.onTertiaryContainer
                },
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.createdAt),
                color = when (message.role) {
                    ChatRole.USER -> MaterialTheme.colorScheme.onPrimaryContainer
                    ChatRole.ASSISTANT -> MaterialTheme.colorScheme.onTertiaryContainer
                },
                style = LocalTextStyle.current.copy(fontSize = 12.sp),
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatPreview() {
    val messageList = listOf<ChatMessage>(
        ChatMessage(
            role = ChatRole.USER,
            content = "Buondi albero saggio,\ncome va??",
            createdAt = Date()
        ),
        ChatMessage(
            role = ChatRole.ASSISTANT,
            content = "Ma salve,\ntutto bene qua",
            createdAt = Date()
        ),
        ChatMessage(
            role = ChatRole.USER,
            content = "Ma ne sei sicuro?",
            createdAt = Date()
        ),
        ChatMessage(
            role = ChatRole.ASSISTANT,
            content = "Sicurissimo!",
            createdAt = Date()
        ),
    )
    ChatScreen(
        chatActions = object : ChatActions {},
        chatState = ChatState(messageList),
    )
}
