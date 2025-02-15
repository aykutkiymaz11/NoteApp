package com.example.noteapp.feature_note.presentation.notes

import com.example.noteapp.feature_note.domain.model.Note

data class NotesState(
    val notes: List<Note> = emptyList(),
    val selectedImageUri: String? = null
)