package me.baldo.rootlink.ui.screens.chat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.baldo.rootlink.data.model.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val fieldText: String = ""
)

interface ChatActions {
    fun addMessage(message: ChatMessage) {}
    fun updateFieldText(text: String) {}
}

class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow<ChatState>(ChatState())
    val state = _state.asStateFlow()

    val actions = object : ChatActions {
        override fun addMessage(message: ChatMessage) {
            _state.update { it.copy(messages = it.messages + message) }
        }

        override fun updateFieldText(text: String) {
            _state.update { it.copy(fieldText = text) }
        }
    }
}