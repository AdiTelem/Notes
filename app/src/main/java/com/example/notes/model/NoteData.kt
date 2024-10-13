package com.example.notes.model

class NoteData (val title: String,
                val content: String,
                var id: Int,
                val createTime: Long = System.currentTimeMillis()
) {

    override fun toString(): String {
        return "Note(title=$title, content=$content, id=$id, creationTime=$createTime)"
    }
}