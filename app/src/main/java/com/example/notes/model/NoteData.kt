package com.example.notes.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notes.view.Note

@Entity(tableName = "notes")
data class NoteData (
    val title: String,
    val content: String,
    @PrimaryKey var id: Int,
    val createTime: Long = System.currentTimeMillis()
) {
    companion object {
        val emptyNote = NoteData(
            title = "",
            content = "",
            id = 0,
            createTime = 0
        )
    }
}
