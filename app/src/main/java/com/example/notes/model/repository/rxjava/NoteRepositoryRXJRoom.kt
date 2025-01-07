package com.example.notes.model.repository.rxjava

import android.util.Log
import com.example.notes.model.NoteData
import com.example.notes.model.roomdb.NoteDao
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


class NoteRepositoryRXJRoom @Inject constructor(
    private var _noteDao: NoteDao
) : NoteRepositoryRXJ {


    override fun readAllNote(): Flowable<List<NoteData>> {
        Log.d("NotesDB", "read")
        return _noteDao.getAllNotesFlowable()
            .subscribeOn(Schedulers.io())
    }

    override fun readOneNote(noteId: Int): Maybe<NoteData> {
        Log.d("NotesDB", "read $noteId")
        return _noteDao.getNoteMaybe(noteId)
            .subscribeOn(Schedulers.io())
    }

    override fun insertNote(note: NoteData): Completable {
        Log.d("NotesDB", "insertNote $note")
        return _noteDao.insertNote(note)
            .subscribeOn(Schedulers.io())
    }

    override fun updateNote(note: NoteData): Completable {
        Log.d("NotesDB", "updateNote $note")
        return _noteDao.updateNote(note)
            .subscribeOn(Schedulers.io())
    }

    override fun removeNote(note: NoteData): Completable {
        Log.d("NotesDB", "removeNote $note")
        return _noteDao.deleteNote(note)
            .subscribeOn(Schedulers.io())
    }
}