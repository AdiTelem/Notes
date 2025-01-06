package com.example.notes.viewmodel.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notes.NotesApplication
import com.example.notes.model.NoteData
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJRoom
import com.example.notes.model.repository.service.NoteRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class NotesGalleryViewModel(val repository: NoteRepositoryRXJ): ViewModel() {
    private val _notes = MutableLiveData<List<NoteData>>(mutableListOf())
    val notes: LiveData<List<NoteData>> = _notes

    private val _selectNote = MutableLiveData<NoteData>()
    private val _isDeleteDialogShown = MutableLiveData(false)
    val isDeleteDialogShown = _isDeleteDialogShown

    private val compositeDisposable = CompositeDisposable()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NotesApplication)
                val noteRepository = application.container.noteRepositoryRXJ
                NotesGalleryViewModel(repository = noteRepository)
            }
        }
    }

    fun readAllNotes() {
        val notesDisposable = repository.readAllNote()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                resultList -> _notes.value = resultList
            }

        compositeDisposable.add(notesDisposable)
    }

    private fun deleteNote(note: NoteData) {
        val notesDisposable = repository.removeNote(note)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val currentList = _notes.value.orEmpty().toMutableList()
                    currentList.removeIf { it.id == note.id }
                    _notes.value = currentList
                },
                { error ->
                    Log.e("GalleryVM", error.message ?: "no message")
                }
            )

        compositeDisposable.add(notesDisposable)
    }

    fun deleteSelectedNote() {
        _selectNote.value?.let {
            deleteNote(it)
        }
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

    // for rxjava disposables
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}