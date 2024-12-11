package com.example.notes.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.example.notes.WebService

class NoteRepositoryWithService (
    private var service: WebService? = null
): NoteRepository {

    override fun insertNote(noteData: NoteData, callback: (status: Boolean) -> Unit) {
        service?.insertNote (
            noteData = noteData,
            onResponse = {
                response ->
                if (response.isSuccessful) {
                    callback(true)
                    Log.d("repository_callbacks", "server response success on insert\nmessage ${response.message()}")
                } else {
                    callback(false)
                    Log.d("repository_callbacks", "server response ${response.code()} on insert")
                }
            },
            onFailure = {
                callback(false)
                Log.d("repository_callbacks", "server failure on insert")
            }
        )
    }

    override fun readAllNote(callback: (notes: List<NoteData>) -> Unit) {
        service?.readAllNote ({ response ->
            if (response.isSuccessful) {
                val resultList = response.body()?.toMutableStateList() ?: mutableStateListOf<NoteData>()
                callback(resultList)
                Log.d("repository_callbacks", "server response success on read all")
            } else {
                Log.d("repository_callbacks", "server response ${response.code()} on read all")
            }

        }, {
            Log.d("repository_callbacks", "server failure on read all")
        })
    }

    override fun removeNote(noteID: Int, callback: (status: Boolean) -> Unit) {
        service?.removeNote (
            noteID = noteID,
            onResponse = {
                    response ->
                if (response.isSuccessful) {
                    callback(true)
                    Log.d("repository_callbacks", "server response success on remove\nmessage ${response.message()}")
                } else {
                    callback(false)
                    Log.d("repository_callbacks", "server response ${response.code()} on remove")
                }
            },
            onFailure = {
                callback(false)
                Log.d("repository_callbacks", "server failure on remove")
            }
        )
    }

    override fun updateNote(noteData: NoteData, callback: (status: Boolean) -> Unit) {
        service?.updateNote (
            noteData = noteData,
            onResponse = {
                    response ->
                if (response.isSuccessful) {
                    callback(true)
                    Log.d("repository_callbacks", "server response success on update\nmessage ${response.message()}")
                } else {
                    callback(false)
                    Log.d("repository_callbacks", "server response ${response.code()} on update")
                }
            },
            onFailure = {
                callback(false)
                Log.d("repository_callbacks", "server failure on update")
            }
        )
    }
}