package com.example.notes.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.notes.model.NoteData
import com.example.notes.model.NoteRepository
import com.example.notes.model.NotesManager

class NavigationViewModel(context: Context, repository: NoteRepository) :
    ViewModel() {
        var notes = NotesManager(repository)

        init {
            notes.readAllNotes()
        }
}
