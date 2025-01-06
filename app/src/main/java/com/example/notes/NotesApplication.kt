package com.example.notes

import android.app.Application
import android.util.Log
import com.example.notes.model.container.DefaultNotesAppContainer
import com.example.notes.model.container.NotesAppContainer
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJRoom
import com.example.notes.model.roomdb.NoteDB

class NotesApplication : Application() {
    lateinit var container: NotesAppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultNotesAppContainer()

        val instance = NoteDB.getInstance(applicationContext)
        val dao = instance.noteDao()

        val noteRepositoryRXJRoom = container.noteRepositoryRXJ as NoteRepositoryRXJRoom
        noteRepositoryRXJRoom.setDao(dao)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("AppCrash", "Unhandled exception", throwable)
        }
    }
}