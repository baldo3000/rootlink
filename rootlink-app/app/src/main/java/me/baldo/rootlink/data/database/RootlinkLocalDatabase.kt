package me.baldo.rootlink.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Tree::class, ChatMessage::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class RootlinkLocalDatabase : RoomDatabase() {
    abstract fun treesDAO(): TreesDAO
}