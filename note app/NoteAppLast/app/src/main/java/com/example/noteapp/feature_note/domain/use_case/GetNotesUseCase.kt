package com.example.noteapp.feature_note.domain.use_case

import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val repository: NoteRepository) {

    operator fun invoke(): Flow<List<Note>> {
        return repository.getNotes()
    }
}