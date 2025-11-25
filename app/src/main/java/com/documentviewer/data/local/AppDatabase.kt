package com.documentviewer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.documentviewer.data.local.dao.*
import com.documentviewer.data.local.entity.*

@Database(
    entities = [
        RecentFileEntity::class,
        FavoriteEntity::class,
        NoteEntity::class,
        ReadingPositionEntity::class,
        SearchIndexEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun noteDao(): NoteDao
    abstract fun readingPositionDao(): ReadingPositionDao
    abstract fun searchIndexDao(): SearchIndexDao
}
