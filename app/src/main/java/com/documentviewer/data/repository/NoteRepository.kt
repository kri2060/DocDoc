package com.documentviewer.data.repository

import com.documentviewer.data.local.dao.NoteDao
import com.documentviewer.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    fun getNoteById(id: Long): Flow<NoteEntity?> = noteDao.getNoteById(id)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insert(note)

    suspend fun updateNote(note: NoteEntity) {
        noteDao.update(note)
    }

    suspend fun deleteNote(id: Long) {
        noteDao.deleteById(id)
    }
}
