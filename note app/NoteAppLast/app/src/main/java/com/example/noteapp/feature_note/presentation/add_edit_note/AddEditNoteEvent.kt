package com.example.noteapp.feature_note.presentation.add_edit_note

import android.net.Uri
import androidx.compose.ui.focus.FocusState
import com.example.noteapp.feature_note.domain.model.Note

sealed class AddEditNoteEvent {
    data class EnteredTitle(val value: String) : AddEditNoteEvent()
    data class ChangeTitleFocus(val focusState: FocusState) : AddEditNoteEvent()
    data class EnteredSubTitle(val value: String) : AddEditNoteEvent()
    data class ChangeSubTitleFocus(val focusState: FocusState) : AddEditNoteEvent()
    data class EnteredContent(val value: String) : AddEditNoteEvent()
    data class ChangeContentFocus(val focusState: FocusState) : AddEditNoteEvent()
    data class ChangeColor(val color: Int) : AddEditNoteEvent()
    data class PickImage(val imageUri: String?) : AddEditNoteEvent()
    data class DeleteNote(val noteId: Int) : AddEditNoteEvent()

    // TODO:   data class OnClickTask(val clickAction: OnClickAction) : AddTaskEvent()
    object SaveNote : AddEditNoteEvent()
}