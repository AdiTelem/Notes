package com.example.notes.daggerdi

import com.example.notes.MainActivity
import com.example.notes.view.fragments.DeleteDialogFragment
import com.example.notes.view.fragments.NoteEditFragment
import com.example.notes.view.fragments.NoteGalleryFragment
import com.example.notes.viewmodel.fragments.NotesEditViewModel
import com.example.notes.viewmodel.fragments.NotesGalleryViewModel
import com.example.notes.viewmodel.mvi.NoteEditViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NotesModule::class])
interface NotesComponent {
    fun inject(notesEditViewModel: NotesEditViewModel)
    fun inject(notesGalleryViewModel: NotesGalleryViewModel)

    fun inject(noteGalleryFragment: NoteGalleryFragment)
    fun inject(noteEditViewModel: NoteEditFragment)
    fun inject(deleteDialogFragment: DeleteDialogFragment)
}