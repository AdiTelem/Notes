package com.example.notes.daggerdi

import android.app.Application
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJRoom
import com.example.notes.model.roomdb.NoteDB
import com.example.notes.model.roomdb.NoteDao
import com.example.notes.viewmodel.mvi.NoteEditViewModel
import com.example.notes.viewmodel.mvi.NoteGalleryViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    @Singleton
    @Provides
    fun provideNoteGalleryVMFactory(
        repositoryRXJ: NoteRepositoryRXJ
    ) : NoteGalleryViewModel.Factory = NoteGalleryViewModel.Factory(
        uiScheduler = AndroidSchedulers.mainThread(),
        effectsScheduler = Schedulers.io(),
        processorFactory = NoteGalleryViewModel.NoteGalleryProcessor.Factory(),
        repositoryRXJ
    )

    @Singleton
    @Provides
    fun provideNoteEditVMFactory(
        repositoryRXJ: NoteRepositoryRXJ
    ) : NoteEditViewModel.Factory = NoteEditViewModel.Factory(
        uiScheduler = AndroidSchedulers.mainThread(),
        effectsScheduler = Schedulers.io(),
        processorFactory = NoteEditViewModel.NoteEditProcessor.Factory(),
        repositoryRXJ,
        application
    )
}
