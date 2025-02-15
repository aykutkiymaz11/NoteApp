package com.example.noteapp.feature_note.domain.use_case

data class NoteUseCases(
    val getNotes: GetNotesUseCase,
    val getNote: GetNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val addNote: AddNoteUseCase
)