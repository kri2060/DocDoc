package com.documentviewer.data.local.dao

import androidx.room.*
import com.documentviewer.data.local.entity.RecentFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY lastAccessed DESC LIMIT :limit")
    fun getRecentFiles(limit: Int = 50): Flow<List<RecentFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: RecentFileEntity)

    @Delete
    suspend fun delete(file: RecentFileEntity)

    @Query("DELETE FROM recent_files")
    suspend fun deleteAll()

    @Query("DELETE FROM recent_files WHERE path = :path")
    suspend fun deleteByPath(path: String)
}
