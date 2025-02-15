package com.example.noteapp.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val noteUseCases: NoteUseCases) :
    ViewModel() {

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private val image = mutableStateOf(NotesState())
    val imageState: State<NotesState> = image

    private var recentlyDeletedNote: Note? = null

    private var job: Job? = null

    init {
        getNotes()
    }

//    fun onEvent(event: NotesEvent) {
//        when (event) {
//            is NotesEvent.DeleteNote -> {
//                viewModelScope.launch {
//                    noteUseCases.deleteNote(event.note)
//                    recentlyDeletedNote = event.note
//                }
//            }
//        }
//    }

    private fun getNotes() {
        job?.cancel()
        job = noteUseCases.getNotes().onEach { notes ->
            _state.value = state.value.copy(notes = notes)
        }.launchIn(viewModelScope)
    }



}