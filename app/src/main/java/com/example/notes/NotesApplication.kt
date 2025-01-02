package com.example.notes

import android.app.Application
import com.example.notes.model.SharedPref
import com.example.notes.model.enums.SystemUI

class NotesApplication : Application() {
    lateinit var container: NotesAppContainer
    lateinit var config: Configuration

    override fun onCreate() {
        super.onCreate()
        container = DefaultNotesAppContainer()

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