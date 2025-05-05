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
import me.baldo.rootlink.data.chat.ChatMessage
import me.baldo.rootlink.data.chat.ChatRole
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
    fun toChatMessage(): ChatMessage = ChatMessage(
        id = this.id,
        role = if (this.role == "assistant") ChatRole.ASSISTANT else ChatRole.USER,
        content = this.content,
        createdAt = runCatching { Date.from(Instant.parse(createdAt)) }.getOrElse { Date() }
    )
}

@Serializable
private data class RequestMessage(val messages: List<Message>)

@Serializable
private data class ResponseMessage(val responseMessage: Message)

class MessagesDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val TAG = "MessagesDataSource"
        private const val BASE_URL = "http://192.168.1.180:3000"
    }

    private val messages = mutableListOf<Message>()

    @OptIn(ExperimentalTime::class)
    suspend fun sendMessage(message: String): ChatMessage {
        val url = "$BASE_URL/api/chat"
        val question = Message(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = message,
            createdAt = Clock.System.now().toString()
        )
        messages.add(question)
        val answer = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(RequestMessage(messages))
        }.body<ResponseMessage>().responseMessage
        messages.add(answer)
        Log.i(TAG, "Response: $answer")
        return answer.toChatMessage()
    }
}