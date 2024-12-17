package com.example.notes.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.example.notes.WebService

class NoteRepositoryWithService: NoteRepository {
    private var service: WebService? = null

    fun setService(webService: WebService) {
        service = webService
    }

    override fun insertNote(noteData: NoteData, callback: (status: Boolean) -> Unit): Boolean {
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
        ) ?: return false
        return true
    }

    override fun readAllNote(callback: (notes: List<NoteData>) -> Unit): Boolean {
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
        }) ?: return false
        return true
    }

    override fun removeNote(noteID: Int, callback: (status: Boolean) -> Unit): Boolean {
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
        ) ?: return false
        return true
    }

    override fun updateNote(noteData: NoteData, callback: (status: Boolean) -> Unit): Boolean {
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
        ) ?: return false
        return true
    }
}