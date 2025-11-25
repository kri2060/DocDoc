package com.documentviewer.data.local.dao

import androidx.room.*
import com.documentviewer.data.local.entity.SearchIndexEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchIndexDao {
    @Query("""
        SELECT * FROM search_index
        WHERE content LIKE '%' || :query || '%' OR fileName LIKE '%' || :query || '%'
        ORDER BY indexedAt DESC
    """)
    fun search(query: String): Flow<List<SearchIndexEntity>>

    @Query("SELECT * FROM search_index WHERE filePath = :filePath")
    fun getIndexForFile(filePath: String): Flow<List<SearchIndexEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(index: SearchIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(indices: List<SearchIndexEntity>)

    @Query("DELETE FROM search_index WHERE filePath = :filePath")
    suspend fun deleteByFilePath(filePath: String)

    @Query("DELETE FROM search_index")
    suspend fun deleteAll()
}
