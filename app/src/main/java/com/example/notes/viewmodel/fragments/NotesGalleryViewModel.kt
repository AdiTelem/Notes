package com.example.notes.viewmodel.fragments

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notes.NotesApplication
import com.example.notes.model.NoteData
import com.example.notes.model.NoteRepository

class NotesGalleryViewModel(val repository: NoteRepository): ViewModel() {
    private val _notes = MutableLiveData<List<NoteData>>(mutableListOf())
    val notes: LiveData<List<NoteData>> = _notes

    private val _selectNote = MutableLiveData<NoteData>()
    private val _isDeleteDialogShown = MutableLiveData(false)
    val isDeleteDialogShown = _isDeleteDialogShown

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NotesApplication)
                val noteRepository = application.container.noteRepository
                NotesGalleryViewModel(repository = noteRepository)
            }
        }
    }

    fun readAllNotes(): Boolean {
        return repository.readAllNote { resultList -> _notes.value = resultList }
    }

    fun deleteNote(noteID: Int) {
        repository.removeNote(noteID) {
            status ->
            if (status) {
                val currentList = _notes.value.orEmpty().toMutableList()
                currentList.removeIf { it.id == noteID }
                _notes.value = currentList
            }
        }
    }

    fun deleteSelectedNote() {
        deleteNote(_selectNote.value!!.id)
    }

    fun onRefresh() {
        readAllNotes()
    }

    fun showDeleteDialog(selectedNote: NoteData) {
        _selectNote.value = selectedNote
        _isDeleteDialogShown.value = true
    }

    fun hideDeleteDialog() {
        _isDeleteDialogShown.value = false
    }
}