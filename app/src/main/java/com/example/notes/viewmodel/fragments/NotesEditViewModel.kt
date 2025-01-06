package com.example.notes.viewmodel.fragments

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notes.NotesApplication
import com.example.notes.R
import com.example.notes.model.NoteData
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import com.example.notes.model.repository.service.NoteRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class NotesEditViewModel (val repository: NoteRepositoryRXJ, private val application: NotesApplication) :
    AndroidViewModel(application = application) {

    val title = MutableLiveData("")
    val content = MutableLiveData("")
    var id = 0

    private val compositeDisposable = CompositeDisposable()

    companion object {
        private const val COUNTER_KEY = "counter"

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

    fun getNote() {
        val notesDisposable = repository.readOneNote(id) .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { note ->
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
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.shared_pref_gallery),
            Context.MODE_PRIVATE
        )

        val Counter: Int = (sharedPref.getInt(COUNTER_KEY, 0))
        Log.d("EditVM", "$Counter")
        val newCounter = Counter + 1
        Log.d("EditVM", "$newCounter")

        noteData.id = newCounter

        val notesDisposable = repository.insertNote(noteData)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    Log.d("EditVM", "hdfhdffg $newCounter")
                    sharedPref.edit().putInt(COUNTER_KEY, newCounter).apply()
                },
                { error ->
                    Log.e("EditVM", error.message ?: "no message")
                    sharedPref.edit().putInt(COUNTER_KEY, newCounter).apply()
                }
            )
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