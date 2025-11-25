package com.documentviewer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey
    val path: String,
    val name: String,
    val size: Long,
    val lastAccessed: Long,
    val mimeType: String?,
    val type: String
)
