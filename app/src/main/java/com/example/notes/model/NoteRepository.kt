package com.example.notes.model

interface NoteRepository {
        fun insertNote(noteData: NoteData, callback: (status: Boolean) -> Unit)
        fun readAllNote(callback: (notes: List<NoteData>) -> Unit)
        fun removeNote(noteID: Int, callback: (status: Boolean) -> Unit)
        fun updateNote(noteData: NoteData, callback: (status: Boolean) -> Unit)
}