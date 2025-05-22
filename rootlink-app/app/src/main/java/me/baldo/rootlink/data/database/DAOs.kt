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

    @Query("SELECT * FROM Tree")
    suspend fun getAllTreesOneShot(): List<Tree>

    @Query("SELECT * FROM Tree WHERE cardId = :cardId")
    suspend fun getTree(cardId: String): Tree?

    @Insert
    suspend fun insertChatMessageToTreeChat(chatMessage: ChatMessage)

    @Query("SELECT DISTINCT region FROM Tree")
    suspend fun getRegions(): List<String>

    // Count section

    @Query(
        """
    SELECT COUNT(*) FROM Tree 
    WHERE region = :region 
    AND cardId IN (
        SELECT DISTINCT treeId FROM ChatMessage
    )
    """
    )
    suspend fun getTreesInteractedWithInRegionCount(region: String): Int

    @Query("SELECT COUNT(*) FROM Tree WHERE region = :region")
    suspend fun getTreesInRegionCount(region: String): Int

    @Query("SELECT COUNT(DISTINCT treeId) FROM ChatMessage")
    suspend fun getTreesInteractedWithCount(): Int

    @Query("SELECT COUNT(*) FROM Tree")
    suspend fun getTreesCount(): Int

    @Query("SELECT COUNT(DISTINCT region) FROM Tree WHERE cardId IN (SELECT DISTINCT treeId FROM ChatMessage)")
    suspend fun getRegionsInteractedWithCount(): Int
}