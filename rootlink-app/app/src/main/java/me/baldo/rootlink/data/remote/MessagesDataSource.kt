package me.baldo.rootlink.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.baldo.rootlink.BuildConfig
import me.baldo.rootlink.data.database.ChatMessage
import java.time.Instant
import java.util.Date
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
private data class Message(
    @SerialName("id")
    val id: String,
    @SerialName("role")
    val role: String,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String
) {
    fun toChatMessage(treeId: String): ChatMessage = ChatMessage(
        treeId = treeId,
        id = this.id,
        role = this.role,
        content = this.content,
        createdAt = runCatching { Date.from(Instant.parse(createdAt)) }.getOrElse { Date() }
    )
}

@Serializable
private data class ChatRequestMessage(val messages: List<Message>)

@Serializable
private data class ChatResponseMessage(val responseMessage: Message)

class MessagesDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val TAG = "MessagesDataSource"
        private const val BASE_URL = BuildConfig.SERVER_ADDRESS
    }

    @OptIn(ExperimentalTime::class)
    suspend fun sendMessage(messages: List<ChatMessage>): ChatMessage {
        // val url = "$BASE_URL/api/chat"
        val url = "https://192.168.1.180:3000/api/chat"
        val answer = try {
            httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(ChatRequestMessage(messages.toMessages()))
            }.body<ChatResponseMessage>().responseMessage
        } catch (e: Exception) {
            Log.e(TAG, "Error while sending message: ${e.message}")
            Message(
                UUID.randomUUID().toString(),
                role = "assistant",
                content = "NETWORK ERROR!",
                createdAt = Clock.System.now().toString()
            )
        }
        return answer.toChatMessage(messages.first().treeId)
    }
}

@OptIn(ExperimentalTime::class)
private fun List<ChatMessage>.toMessages(): List<Message> {
    return this.map { message ->
        Message(
            id = message.id,
            role = message.role,
            content = message.content,
            createdAt = Clock.System.now().toString()
        )
    }
}