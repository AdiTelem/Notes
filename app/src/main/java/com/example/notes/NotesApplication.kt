package com.example.notes

import android.app.Application
import android.util.Log
import com.example.notes.daggerdi.DaggerNotesComponent
import com.example.notes.daggerdi.NotesComponent
import com.example.notes.daggerdi.NotesModule
import com.example.notes.model.container.DefaultNotesAppContainer
import com.example.notes.model.container.NotesAppContainer
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJRoom
import com.example.notes.model.roomdb.NoteDB
import com.example.notes.model.SharedPref
import com.example.notes.model.enums.SystemUI

class NotesApplication : Application() {
    lateinit var container: NotesAppContainer
    lateinit var config: Configuration

    lateinit var notesComponent: NotesComponent
        private set

    override fun onCreate() {
        super.onCreate()

        notesComponent = DaggerNotesComponent.builder()
            .notesModule(NotesModule(this))
            .build()

        container = DefaultNotesAppContainer()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("AppCrash", "Unhandled exception", throwable)
        }

        val systemUI = SharedPref.getSystemUI(applicationContext)
        config = Configuration(
            systemUI = systemUI
        )
    }

    fun setSystemUI(systemUI: SystemUI) {
        SharedPref.setSystemUI(applicationContext, systemUI)
        config.systemUI = systemUI
    }
}

data class Configuration(
    var systemUI : SystemUI
)