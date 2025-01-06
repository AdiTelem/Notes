package com.example.notes.model.repository.service

import com.example.notes.model.NoteData


/*
the return values stands for success or failure launching the task, not its end status
each callback is called once the operation is done
 */

interface NoteRepository {
        fun insertNote(noteData: NoteData, callback: (status: Boolean) -> Unit) : Boolean
        fun readAllNote(callback: (notes: List<NoteData>) -> Unit) : Boolean
        fun readOneNote(noteID: Int, callback: (note: NoteData) -> Unit) : Boolean
        fun removeNote(noteID: Int, callback: (status: Boolean) -> Unit) : Boolean
        fun updateNote(noteData: NoteData, callback: (status: Boolean) -> Unit) : Boolean
}