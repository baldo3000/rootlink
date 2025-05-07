package me.baldo.rootlink.data.model

import java.util.Date
import java.util.UUID

enum class ChatRole { ASSISTANT, USER }

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val content: String,
    val createdAt: Date
)