package com.example.noteapp.feature_note.presentation.add_edit_note

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.feature_note.domain.model.InvalidNoteException
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    application: Application,
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val appContext = application

    fun onPickImage(uri: Uri?) {
        val savedImagePath = saveImageToInternalStorage(appContext, uri!!)
        _selectedImageUri.value = savedImagePath?.let { Uri.parse(it) }
    }

    private val _noteTitle = mutableStateOf(NoteTextFieldState(hint = "Enter Title..."))
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(NoteTextFieldState(hint = "Enter Content"))
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteSubTitle = mutableStateOf(NoteTextFieldState(hint = "Enter Subtitle"))
    val noteSubTitle: State<NoteTextFieldState> = _noteSubTitle

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> get() = _selectedImageUri

    private val _noteColor = mutableStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if (noteId != -1) {
                viewModelScope.launch(Dispatchers.IO) {
                    noteUseCases.getNote(noteId)?.also { note ->
                        currentNoteId = note.id
                        _noteTitle.value = noteTitle.value.copy(text = note.title, isHintVisible = false)
                        _noteSubTitle.value = noteSubTitle.value.copy(text = note.subTitle, isHintVisible = false)
                        _noteContent.value = noteContent.value.copy(text = note.content, isHintVisible = false)
                    }
                }
            }
        }

    }


    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }

            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value =
                    noteContent.value.copy(isHintVisible = !event.focusState.isFocused && noteContent.value.text.isBlank())
            }

            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value =
                    noteTitle.value.copy(isHintVisible = !event.focusState.isFocused && noteTitle.value.text.isBlank())
            }

            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = noteContent.value.copy(text = event.value)
            }

            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(text = event.value)
            }

            is AddEditNoteEvent.ChangeSubTitleFocus -> {
                _noteSubTitle.value =
                    noteSubTitle.value.copy(isHintVisible = !event.focusState.isFocused && noteSubTitle.value.text.isBlank())
            }

            is AddEditNoteEvent.EnteredSubTitle -> {
                _noteSubTitle.value = noteSubTitle.value.copy(text = event.value)
            }

            AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                title = noteTitle.value.text,
                                content = noteContent.value.text,
                                subTitle = noteSubTitle.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                imageUri = _selectedImageUri.value?.toString(),
                                id = currentNoteId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Could not save the note"
                            )
                        )
                    }
                }
            }

            is AddEditNoteEvent.PickImage -> {
                //val savedImagePath = saveImageToInternalStorage(a, event.imageUri?.toUri()!!)
                //_selectedImageUri.value = savedImagePath?.let { Uri.parse(it) }
            }

            is AddEditNoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.noteId)
                }
            }
        }
    }


    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}