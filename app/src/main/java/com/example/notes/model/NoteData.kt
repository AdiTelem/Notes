package com.example.notes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteData (
    val title: String,
    val content: String,
    @PrimaryKey var id: Int,
    val createTime: Long = System.currentTimeMillis()
)
