package me.baldo.rootlink.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TreesDAO {
    @Transaction
    @Query("SELECT * FROM Tree WHERE cardId = :cardId")
    suspend fun getChatMessagesOfTree(cardId: String): TreeWithChatMessages

    @Upsert
    suspend fun upsertTree(tree: Tree)

    @Upsert
    suspend fun upsertTrees(trees: List<Tree>)

    @Query("SELECT * FROM Tree")
    fun getAllTrees(): Flow<List<Tree>>

    @Query("SELECT * FROM Tree WHERE cardId = :cardId")
    suspend fun getTree(cardId: String): Tree?

    @Insert
    suspend fun insertChatMessageToTreeChat(chatMessage: ChatMessage)
}