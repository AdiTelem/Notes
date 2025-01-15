package com.example.notes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteData (
    val title: String,
    val content: String,
    @PrimaryKey var id: Int,
    var createTime: Long = System.currentTimeMillis()
) {
    fun updateTime() {
        createTime = System.currentTimeMillis()
    }

    companion object {
        fun EmptyNote() : NoteData {
            return emptyNote.copy()
        }

        private val emptyNote = NoteData(
            title = "",
            content = "",
            id = 0,
            createTime = 0
        )
    }
}
