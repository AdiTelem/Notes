package com.example.notes

import com.example.notes.model.NoteRepository
import com.example.notes.model.NoteRepositoryWithService

interface NotesAppContainer {
    var noteRepository: NoteRepository
}

class DefaultNotesAppContainer: NotesAppContainer {
    override var noteRepository: NoteRepository = NoteRepositoryWithService()
}