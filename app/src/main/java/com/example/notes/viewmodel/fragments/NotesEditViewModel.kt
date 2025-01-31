package com.example.notes.viewmodel.fragments

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notes.NotesApplication
import com.example.notes.model.NoteData
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import com.example.notes.model.SharedPref

class NotesEditViewModel (val repository: NoteRepositoryRXJ, private val application: NotesApplication) :
    AndroidViewModel(application = application) {

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    var id = 0

    private val compositeDisposable = CompositeDisposable()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NotesApplication)
                val noteRepository = application.container.noteRepositoryRXJ
                NotesEditViewModel(repository = noteRepository, application = application)
            }
        }
    }

    init {
        title.value = ""
    }

    fun getNote(noteID: Int) {
        val notesDisposable = repository.readOneNote(noteID) .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { note ->
                    id = noteID
                    title.value = note.title
                    content.value = note.content
                },
                { error ->
                    Log.e("EditVM", error.message ?: "no message")
                }
            )

        compositeDisposable.add(notesDisposable)
    }

    fun clearNote() {
        title.value = ""
        content.value = ""
        id = 0
    }

    fun onSubmit() {
        title.value?.let {
            if (it.isNotEmpty()) {
                if (id == 0) {
                    createNote(NoteData(it, content.value ?: "", id))
                } else {
                    updateNote(NoteData(it, content.value ?: "", id))
                }
            }
        }
    }

    private fun createNote(noteData: NoteData) {
        val context = getApplication<Application>().applicationContext

        val newCounter = SharedPref.getNoteCounter(context) + 1

        noteData.id = newCounter

        val notesDisposable = repository.insertNote(noteData)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("EditVM", "$newCounter")
                SharedPref.setNoteCounter(context, newCounter)
            }

        Thread.sleep(100)

        compositeDisposable.add(notesDisposable)
    }

    private fun updateNote(noteData: NoteData) {
        val notesDisposable = repository.updateNote(noteData)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("EditVM", "update note called")
            },
                { error ->
                    Log.e("EditVM", error.message ?: "no message")
                }
            )

        compositeDisposable.add(notesDisposable)
    }

    // for rxjava disposables
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}