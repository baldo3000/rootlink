package me.baldo.rootlink.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.baldo.rootlink.data.database.ChatMessage
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.remote.AirQualityDataSource
import me.baldo.rootlink.data.remote.MessagesDataSource
import me.baldo.rootlink.data.repositories.ProfileRepository
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
    private val profileRepository: ProfileRepository,
    private val messagesDataSource: MessagesDataSource,
    private val airQualityDataSource: AirQualityDataSource
) : ViewModel() {
    private val _state = MutableStateFlow<ChatState>(ChatState())
    val state = _state.asStateFlow()
    val actions = object : ChatActions {
        override fun openTreeChat(cardId: String) {
            viewModelScope.launch {
                val tree = treesRepository.getTree(cardId)
                val treeWithChatMessages = treesRepository.getChatMessagesForTree(cardId)
                _state.update {
                    it.copy(
                        tree = tree,
                        chatMessages = treeWithChatMessages.chatMessages
                    )
                }
            }
        }

        override fun sendMessage() {
            state.value.tree?.let { tree ->
                val userMessage = ChatMessage(
                    treeId = tree.cardId,
                    role = "user",
                    content = state.value.fieldText,
                    createdAt = Date()
                )
                // Update local copy
                _state.update {
                    it.copy(
                        chatMessages = it.chatMessages + userMessage,
                        fieldText = ""
                    )
                }
                // Update database
                viewModelScope.launch {
                    treesRepository.insertChatMessage(userMessage)
                }
                // Send message to the server and wait for response
                viewModelScope.launch {
                    val pos = tree.position
                    val sendMessages = state.value.chatMessages.toMutableList().apply {
                        airQualityDataSource.getAirQuality(pos.latitude, pos.longitude)?.let {
                            val airQualityMessage = ChatMessage(
                                treeId = get(0).treeId,
                                role = "system",
                                content = tree.generateAIPrompt() + "\nAir quality index: $it" + "\nName of the user: ${profileRepository.name.first()}",
                                createdAt = Date()
                            )
                            add(0, airQualityMessage)
                        }
                    }
                    val responseMessage = messagesDataSource.sendMessage(sendMessages)
                    _state.update { it.copy(chatMessages = it.chatMessages + responseMessage) }
                    treesRepository.insertChatMessage(responseMessage)
                }
            } ?: run { error("No tree selected") }
        }

        override fun updateFieldText(text: String) {
            _state.update { it.copy(fieldText = text) }
        }
    }
}