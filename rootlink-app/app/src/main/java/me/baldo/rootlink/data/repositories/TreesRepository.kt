package me.baldo.rootlink.data.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.baldo.rootlink.data.database.ChatMessage
import me.baldo.rootlink.data.database.Tree
import me.baldo.rootlink.data.database.TreesDAO

class TreesRepository(
    private val treesDAO: TreesDAO
) {
    private val _loadedTrees = MutableStateFlow<List<Tree>>(emptyList())
    val loadedTrees = _loadedTrees.asStateFlow()

    suspend fun getChatMessagesForTree(treeId: String) = treesDAO.getChatMessagesOfTree(treeId)

    suspend fun upsertTree(tree: Tree) = treesDAO.upsertTree(tree)

    suspend fun upsertTrees(trees: List<Tree>) = treesDAO.upsertTrees(trees)

    suspend fun loadNewTrees(trees: List<Tree>) {
        _loadedTrees.value = trees.toList()
        upsertTrees(trees)
    }

    fun getAllTrees() = treesDAO.getAllTrees()

    suspend fun getAllTreesOneShot() = treesDAO.getAllTreesOneShot()

    suspend fun getTree(treeId: String) = treesDAO.getTree(treeId)

    suspend fun insertChatMessage(chatMessage: ChatMessage) {
        treesDAO.insertChatMessageToTreeChat(chatMessage)
    }
}