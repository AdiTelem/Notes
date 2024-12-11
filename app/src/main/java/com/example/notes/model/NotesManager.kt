package com.example.notes.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.example.notes.R

class NotesManager(var repository: NoteRepository) {

    var noteList = mutableStateListOf<NoteData>()

    companion object {
        private const val COUNTER_KEY = "counter"
    }

    fun readAllNotes() {
        repository.readAllNote { resultList -> noteList.addAll(resultList) }
    }

    fun createNote(noteData: NoteData, context: Context) {
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
        repository.removeNote(noteID) {
            status ->
            if (status) {
                noteList.removeIf { it.id == noteID }
            }
        }
    }

    fun updateNote(noteData: NoteData) {
        repository.updateNote(noteData) {
            status ->
            if (status) {
                noteList.removeIf { it.id == noteData.id }
                noteList.add(noteData)
            }
        }
    }
}