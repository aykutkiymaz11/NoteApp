package com.example.noteapp.feature_note.domain.use_case

import com.example.noteapp.feature_note.domain.model.InvalidNoteException
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.repository.NoteRepository

class AddNoteUseCase(private val repository: NoteRepository) {

    suspend operator fun invoke(note: Note) {
        if (note.title?.isBlank() == true) {
            throw InvalidNoteException("The title of the note cannot be empty!!!")
        }
        if (note.content?.isBlank() == true) {
            throw InvalidNoteException("The content of the note cannot be empty!!!")
        }
        repository.insertNote(note = note)
    }
}