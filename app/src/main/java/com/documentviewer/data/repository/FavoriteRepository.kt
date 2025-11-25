package com.documentviewer.data.repository

import com.documentviewer.data.local.dao.FavoriteDao
import com.documentviewer.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    fun getAllFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    fun isFavorite(path: String): Flow<Boolean> = favoriteDao.isFavorite(path)

    suspend fun addFavorite(favorite: FavoriteEntity) {
        favoriteDao.insert(favorite)
    }

    suspend fun removeFavorite(path: String) {
        favoriteDao.deleteByPath(path)
    }
}
