package com.documentviewer.data.repository

import com.documentviewer.data.local.dao.RecentFileDao
import com.documentviewer.data.local.entity.RecentFileEntity
import kotlinx.coroutines.flow.Flow

class RecentFileRepository(private val recentFileDao: RecentFileDao) {

    fun getRecentFiles(limit: Int = 50): Flow<List<RecentFileEntity>> =
        recentFileDao.getRecentFiles(limit)

    suspend fun addRecentFile(file: RecentFileEntity) {
        recentFileDao.insert(file)
    }

    suspend fun removeRecentFile(path: String) {
        recentFileDao.deleteByPath(path)
    }

    suspend fun clearAll() {
        recentFileDao.deleteAll()
    }
}
