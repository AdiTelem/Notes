package com.example.notes.model.container

import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJRoom
import com.example.notes.model.repository.service.NoteRepository
import com.example.notes.model.repository.service.NoteRepositoryWithService

// container is unused in fragments environment but is used in compose
interface NotesAppContainer {
    var noteRepository: NoteRepository
}

class DefaultNotesAppContainer: NotesAppContainer {
    override var noteRepository: NoteRepository = NoteRepositoryWithService()
}