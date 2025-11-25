package com.documentviewer.di

import android.content.Context
import androidx.room.Room
import com.documentviewer.data.local.AppDatabase
import com.documentviewer.data.local.dao.FavoriteDao
import com.documentviewer.data.local.dao.NoteDao
import com.documentviewer.data.local.dao.ReadingPositionDao
import com.documentviewer.data.local.dao.RecentFileDao
import com.documentviewer.data.local.dao.SearchIndexDao
import com.documentviewer.data.repository.DocumentRepository
import com.documentviewer.data.repository.FavoriteRepository
import com.documentviewer.data.repository.NoteRepository
import com.documentviewer.data.repository.ReadingPositionRepository
import com.documentviewer.data.repository.RecentFileRepository
import com.documentviewer.data.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "document_viewer_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRecentFileDao(database: AppDatabase): RecentFileDao {
        return database.recentFileDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideReadingPositionDao(database: AppDatabase): ReadingPositionDao {
        return database.readingPositionDao()
    }

    @Provides
    @Singleton
    fun provideSearchIndexDao(database: AppDatabase): SearchIndexDao {
        return database.searchIndexDao()
    }

    @Provides
    @Singleton
    fun provideDocumentRepository(@ApplicationContext context: Context): DocumentRepository {
        return DocumentRepository(context)
    }

    @Provides
    @Singleton
    fun provideRecentFileRepository(recentFileDao: RecentFileDao): RecentFileRepository {
        return RecentFileRepository(recentFileDao)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(favoriteDao: FavoriteDao): FavoriteRepository {
        return FavoriteRepository(favoriteDao)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepository(noteDao)
    }

    @Provides
    @Singleton
    fun provideReadingPositionRepository(
        readingPositionDao: ReadingPositionDao
    ): ReadingPositionRepository {
        return ReadingPositionRepository(readingPositionDao)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        searchIndexDao: SearchIndexDao,
        @ApplicationContext context: Context
    ): SearchRepository {
        return SearchRepository(searchIndexDao, context)
    }
}
