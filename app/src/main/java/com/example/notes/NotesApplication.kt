package com.example.notes

import android.app.Application
import android.util.Log
import com.example.notes.model.container.DefaultNotesAppContainer
import com.example.notes.model.container.NotesAppContainer
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJRoom
import com.example.notes.model.roomdb.NoteDB
import com.example.notes.model.SharedPref
import com.example.notes.model.enums.SystemUI

class NotesApplication : Application() {
    lateinit var container: NotesAppContainer
    lateinit var config: Configuration

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