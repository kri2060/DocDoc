package com.documentviewer.data.repository

import android.content.Context
import com.documentviewer.data.local.dao.SearchIndexDao
import com.documentviewer.data.local.entity.SearchIndexEntity
import kotlinx.coroutines.flow.Flow

class SearchRepository(
    private val searchIndexDao: SearchIndexDao,
    private val context: Context
) {

    fun search(query: String): Flow<List<SearchIndexEntity>> =
        searchIndexDao.search(query)

    fun getIndexForFile(filePath: String): Flow<List<SearchIndexEntity>> =
        searchIndexDao.getIndexForFile(filePath)

    suspend fun indexFile(filePath: String, content: String, fileType: String, pageNumber: Int? = null) {
        val index = SearchIndexEntity(
            filePath = filePath,
            fileName = filePath.substringAfterLast('/'),
            content = content,
            pageNumber = pageNumber,
            indexedAt = System.currentTimeMillis(),
            fileType = fileType
        )
        searchIndexDao.insert(index)
    }

    suspend fun indexMultiple(indices: List<SearchIndexEntity>) {
        searchIndexDao.insertAll(indices)
    }

    suspend fun removeIndex(filePath: String) {
        searchIndexDao.deleteByFilePath(filePath)
    }

    suspend fun clearAllIndices() {
        searchIndexDao.deleteAll()
    }
}
