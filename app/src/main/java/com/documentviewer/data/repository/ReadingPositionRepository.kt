package com.documentviewer.data.repository

import com.documentviewer.data.local.dao.ReadingPositionDao
import com.documentviewer.data.local.entity.ReadingPositionEntity
import kotlinx.coroutines.flow.Flow

class ReadingPositionRepository(private val readingPositionDao: ReadingPositionDao) {

    fun getPosition(filePath: String): Flow<ReadingPositionEntity?> =
        readingPositionDao.getPosition(filePath)

    fun getRecentPositions(): Flow<List<ReadingPositionEntity>> =
        readingPositionDao.getRecentPositions()

    suspend fun savePosition(position: ReadingPositionEntity) {
        readingPositionDao.insert(position)
    }

    suspend fun deletePosition(filePath: String) {
        readingPositionDao.deleteByPath(filePath)
    }
}
