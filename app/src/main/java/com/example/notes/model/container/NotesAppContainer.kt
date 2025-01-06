package com.example.notes.model.container

import com.example.notes.model.repository.rxjava.NoteRepositoryRXJ
import com.example.notes.model.repository.rxjava.NoteRepositoryRXJRoom
import com.example.notes.model.repository.service.NoteRepository
import com.example.notes.model.repository.service.NoteRepositoryWithService

interface NotesAppContainer {
    //service
    var noteRepository: NoteRepository

    //rxjava
    var noteRepositoryRXJ: NoteRepositoryRXJ
}

class DefaultNotesAppContainer: NotesAppContainer {
    //service
    override var noteRepository: NoteRepository = NoteRepositoryWithService()

    //rxjava
    override var noteRepositoryRXJ: NoteRepositoryRXJ = NoteRepositoryRXJRoom()
}