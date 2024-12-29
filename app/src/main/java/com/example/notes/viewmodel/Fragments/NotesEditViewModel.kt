package com.example.notes.viewmodel.Fragments

import android.app.Application
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notes.NotesApplication
import com.example.notes.R
import com.example.notes.model.NoteData
import com.example.notes.model.NoteRepository

class NotesEditViewModel (val repository: NoteRepository, private val application: NotesApplication) :
    AndroidViewModel(application = application) {

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    var id = 0

    companion object {
        private const val COUNTER_KEY = "counter"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NotesApplication)
                val noteRepository = application.container.noteRepository
                NotesEditViewModel(repository = noteRepository, application = application)
            }
        }
    }

    init {
        title.value = ""
    }

    fun getNote() {
        repository.readOneNote(id) {
            noteData ->
            title.value = noteData.title
            content.value = noteData.content
        }
    }

    fun onSubmit() {
        title.value?.let {
            if (it.isNotEmpty()) {
                if (id == 0) {
                    createNote(NoteData(it, content.value ?: "", 0))
                } else {
                    updateNote(NoteData(it, content.value ?: "", id))
                }
            }
        }
    }

    private fun createNote(noteData: NoteData) {
        val context = getApplication<Application>().applicationContext
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.shared_pref_gallery),
            Context.MODE_PRIVATE
        )

        val newCounter: Int = (sharedPref.getInt(COUNTER_KEY, 0)) + 1

        noteData.id = newCounter
        repository.insertNote(noteData) { status ->
            if (status) {
                sharedPref.edit().putInt(COUNTER_KEY, newCounter).apply()
            }
        }
    }

    private fun updateNote(noteData: NoteData) {
        repository.updateNote(noteData) {
            Log.d("debug update note", "called")
        }
    }
}