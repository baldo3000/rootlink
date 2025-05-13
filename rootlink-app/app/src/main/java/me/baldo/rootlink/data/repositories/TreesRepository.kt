package me.baldo.rootlink.data.repositories

import me.baldo.rootlink.data.database.ChatMessage
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.database.TreesDAO

class TreesRepository(
    private val treesDAO: TreesDAO
) {
    suspend fun getChatMessagesForTree(treeId: String) = treesDAO.getChatMessagesOfTree(treeId)

    suspend fun upsertTree(tree: Tree) = treesDAO.upsertTree(tree)

    suspend fun getTrees() = treesDAO.getTrees()

    suspend fun getTree(treeId: String) = treesDAO.getTree(treeId)

    suspend fun insertChatMessage(chatMessage: ChatMessage) {
        treesDAO.insertChatMessageToTreeChat(chatMessage)
    }
}