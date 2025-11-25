package com.documentviewer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val path: String,
    val name: String,
    val size: Long,
    val addedAt: Long,
    val mimeType: String?,
    val type: String
)
