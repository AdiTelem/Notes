package com.example.notes.daggerdi

import android.app.Application
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJRoom
import com.example.notes.model.roomdb.NoteDB
import com.example.notes.model.roomdb.NoteDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NotesModule(private val application: Application) {

    @Provides
    fun provideApplication(): Application = application

    @Singleton
    @Provides
    fun provideNoteDao(): NoteDao {
        val instance = NoteDB.getInstance(application.applicationContext)
        return instance.noteDao()
    }

    @Singleton
    @Provides
    fun provideNoteRepositoryRXJ(noteDao: NoteDao): NoteRepositoryRXJ = NoteRepositoryRXJRoom(noteDao)

}
