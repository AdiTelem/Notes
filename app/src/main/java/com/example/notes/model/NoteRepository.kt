package com.example.notes.model

interface NoteRepository {
        fun insertNote(noteData: NoteData, callback: (status: Boolean) -> Unit) : Boolean
        fun readAllNote(callback: (notes: List<NoteData>) -> Unit) : Boolean
        fun readOneNote(noteID: Int, callback: (note: NoteData) -> Unit) : Boolean
        fun removeNote(noteID: Int, callback: (status: Boolean) -> Unit) : Boolean
        fun updateNote(noteData: NoteData, callback: (status: Boolean) -> Unit) : Boolean
}