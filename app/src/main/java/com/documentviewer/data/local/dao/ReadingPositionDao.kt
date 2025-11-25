package com.documentviewer.data.local.dao

import androidx.room.*
import com.documentviewer.data.local.entity.ReadingPositionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingPositionDao {
    @Query("SELECT * FROM reading_positions WHERE filePath = :filePath")
    fun getPosition(filePath: String): Flow<ReadingPositionEntity?>

    @Query("SELECT * FROM reading_positions ORDER BY lastReadAt DESC LIMIT 5")
    fun getRecentPositions(): Flow<List<ReadingPositionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(position: ReadingPositionEntity)

    @Delete
    suspend fun delete(position: ReadingPositionEntity)

    @Query("DELETE FROM reading_positions WHERE filePath = :filePath")
    suspend fun deleteByPath(filePath: String)
}
