package com.example.notes.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.notes.model.NoteData
import com.example.notes.model.NoteManager

class EditScreenViewModel(private var navController: NavController, private val noteID: Int, context: Context) :
    ViewModel() {

    var title = mutableStateOf("")
    var content = mutableStateOf("")

    init {
        //retrieve notes data if given
        if (noteID != 0) {
            val noteData = NoteManager.readOneNote(context, noteID)
            title.value = noteData.title
            content.value = noteData.content
        }
    }

    fun submitNote(context: Context) {
        if (title.value.isNotBlank()) {
            if (noteID == 0) {
                NoteManager.createNote(NoteData(title.value, content.value, noteID), context)
            } else {
                NoteManager.updateNote(NoteData(title.value, content.value, noteID), context)
            }
        }

        navController.navigate("noteGallery")
    }

    fun dismiss() {
        navController.navigate("noteGallery")
    }
}