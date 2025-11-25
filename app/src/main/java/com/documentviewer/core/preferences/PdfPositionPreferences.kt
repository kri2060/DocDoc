package com.documentviewer.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.pdfPositionDataStore: DataStore<Preferences> by preferencesDataStore(name = "pdf_positions")

@Singleton
class PdfPositionPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun getLastPage(pdfUri: String): Flow<Int> {
        val key = intPreferencesKey(pdfUri.hashCode().toString())
        return context.pdfPositionDataStore.data.map { preferences ->
            preferences[key] ?: 0
        }
    }

    suspend fun saveLastPage(pdfUri: String, page: Int) {
        val key = intPreferencesKey(pdfUri.hashCode().toString())
        context.pdfPositionDataStore.edit { preferences ->
            preferences[key] = page
        }
    }
}
