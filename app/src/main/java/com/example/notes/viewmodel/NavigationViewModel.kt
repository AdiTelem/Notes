package com.example.notes.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notes.NotesApplication
import com.example.notes.R
import com.example.notes.model.NoteData
import com.example.notes.model.NoteRepository
import com.example.notes.model.NotesManager

class NavigationViewModel(var repository: NoteRepository) :
    ViewModel() {

    var noteList = mutableStateListOf<NoteData>()
    var isInit = false

    companion object {
        private const val COUNTER_KEY = "counter"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NotesApplication)
                val noteRepository = application.container.noteRepository
                NavigationViewModel(repository = noteRepository)
            }
        }
    }

    init {
        readAllNotes()
    }

    private fun initList() {
        if (!isInit) {
            var success = readAllNotes()
            isInit = success
        }
    }

    private fun readAllNotes(): Boolean {
        return repository.readAllNote { resultList -> noteList.addAll(resultList) }
    }

    fun createNote(noteData: NoteData, context: Context) {
        initList()
        var sharedPref = context.getSharedPreferences(
            context.getString(R.string.shared_pref_gallery),
            Context.MODE_PRIVATE
        )

        val newCounter: Int = (sharedPref.getInt(COUNTER_KEY, 0)) + 1

        noteData.id = newCounter
        repository.insertNote(noteData) { status ->
            if (status) {
                sharedPref.edit().putInt(COUNTER_KEY, newCounter).apply()
                noteList.add(noteData)
            }
        }
    }

    fun deleteNote(noteID: Int, context: Context) {
        initList()
        repository.removeNote(noteID) {
                status ->
            if (status) {
                noteList.removeIf { it.id == noteID }
            }
        }
    }

    fun updateNote(noteData: NoteData) {
        initList()
        repository.updateNote(noteData) {
                status ->
            if (status) {
                noteList.removeIf { it.id == noteData.id }
                noteList.add(noteData)
            }
        }
    }
}
