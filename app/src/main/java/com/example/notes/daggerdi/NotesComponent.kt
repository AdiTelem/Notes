package com.example.notes.daggerdi

import com.example.notes.MainActivity
import com.example.notes.viewmodel.fragments.NotesEditViewModel
import com.example.notes.viewmodel.fragments.NotesGalleryViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NotesModule::class])
interface NotesComponent {
    fun inject(notesEditViewModel: NotesEditViewModel)
    fun inject(notesGalleryViewModel: NotesGalleryViewModel)
}