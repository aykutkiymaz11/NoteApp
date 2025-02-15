package com.example.noteapp.feature_note.domain.repository

import com.example.noteapp.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    fun getNoteById(id: Int): Note?
    suspend fun insertNote(note: Note)
    suspend fun delete(noteId: Int)
}