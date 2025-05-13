package me.baldo.rootlink.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.database.ChatMessage
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.remote.MessagesDataSource
import me.baldo.rootlink.data.repositories.TreesRepository
import java.util.Date

data class ChatState(
    val tree: Tree? = null,
    val chatMessages: List<ChatMessage> = emptyList(),
    val fieldText: String = ""
)

interface ChatActions {
    fun openTreeChat(cardId: String) {}
    fun sendMessage() {}
    fun updateFieldText(text: String) {}
}

class ChatViewModel(
    private val treesRepository: TreesRepository,
    private val messagesDataSource: MessagesDataSource
) : ViewModel() {
    private val _state = MutableStateFlow<ChatState>(ChatState())
    val state = _state.asStateFlow()
    val actions = object : ChatActions {
        override fun openTreeChat(cardId: String) {
            viewModelScope.launch {
                val tree = treesRepository.getTree(cardId)
                val treeWithChatMessages = treesRepository.getChatMessagesForTree(cardId)
                val messages = treeWithChatMessages.chatMessages.let {
                    if (it.isEmpty()) {
                        listOf(
                            ChatMessage(
                                treeId = cardId,
                                role = "system",
                                content = tree?.generateAIPrompt() ?: error("Trying to speak to a tree which does not exist"),
                                createdAt = Date()
                            )
                        )
                    } else {
                        it
                    }
                }
                _state.update {
                    it.copy(
                        tree = tree,
                        chatMessages = messages
                    )
                }
            }
        }

        override fun sendMessage() {
            val message = ChatMessage(
                treeId = state.value.tree?.cardId ?: error("No tree selected"),
                role = "user",
                content = state.value.fieldText,
                createdAt = Date()
            )
            // Update local copy
            _state.update { it.copy(chatMessages = it.chatMessages + message, fieldText = "") }
            // Update database
            viewModelScope.launch {
                treesRepository.insertChatMessage(message)
            }
            // Send message to the server and wait for response
            viewModelScope.launch {
                val responseMessage = messagesDataSource.sendMessage(_state.value.chatMessages)
                _state.update { it.copy(chatMessages = it.chatMessages + responseMessage) }
                treesRepository.insertChatMessage(responseMessage)
            }
        }

        override fun updateFieldText(text: String) {
            _state.update { it.copy(fieldText = text) }
        }
    }
}