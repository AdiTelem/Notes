package com.example.notes

import android.app.Application

class NotesApplication : Application() {
    lateinit var container: NotesAppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultNotesAppContainer()
    }
}