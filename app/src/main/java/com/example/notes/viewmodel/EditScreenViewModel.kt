package com.example.notes.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.notes.model.NoteData
import com.example.notes.model.NotesManager

class EditScreenViewModel(
    private var navController: NavController,
    private var nVM: NavigationViewModel,
    private val noteID: Int
) : ViewModel() {

    var title = mutableStateOf("")
    var content = mutableStateOf("")

    init {
        var editedNote = nVM.noteList.find {it.id == noteID} ?: NoteData("", "", 0)
        title.value = editedNote.title
        content.value = editedNote.content
    }

    fun submitNote(context: Context) {
        if (title.value.isNotBlank()) {
            if (noteID == 0) {
                nVM.createNote(NoteData(title.value, content.value, noteID), context)
            } else {
                nVM.updateNote(NoteData(title.value, content.value, noteID))
            }
        }

        navController.navigate("noteGallery")
    }

    fun dismiss() {
        navController.navigate("noteGallery")
    }
}