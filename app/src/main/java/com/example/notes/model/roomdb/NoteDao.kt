package com.example.notes.model.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notes.model.NoteData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getAllNotesFlowable(): Flowable<List<NoteData>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    fun getNoteMaybe(noteId: Int): Maybe<NoteData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: NoteData): Completable

    @Update
    fun updateNote(note: NoteData): Completable

    @Delete
    fun deleteNote(note: NoteData): Completable
}