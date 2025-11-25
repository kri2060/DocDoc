package com.documentviewer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_positions")
data class ReadingPositionEntity(
    @PrimaryKey
    val filePath: String,
    val pageNumber: Int,
    val scrollOffset: Float = 0f,
    val lastReadAt: Long
)
