package com.example.notes.model.repository.rxjava

import com.example.notes.model.NoteData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

interface NoteRepositoryRXJ {
    fun readAllNote(): Flowable<List<NoteData>>
    fun readOneNote(noteId: Int): Maybe<NoteData>
    fun insertNote(note: NoteData): Completable
    fun updateNote(note: NoteData): Completable
    fun removeNote(note: NoteData): Completable
}