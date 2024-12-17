package com.example.notes

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.example.notes.model.NoteData
import com.example.notes.model.NotesRemoteDBHelper
import com.example.notes.model.RetrofitInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WebService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): WebService = this@WebService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("service_debug", "bind")

        return binder
    }

    /* the callback should receive a response object given in the Retrofit API */
    fun readAllNote(onResponse: (response: Response<List<NoteData>>) -> Unit, onFailure: () -> Unit) {
        NotesRemoteDBHelper.readAllNote(
            onResponse = onResponse,
            onFailure = onFailure
        )
    }


    fun insertNote(
        noteData: NoteData,
        onResponse: (response: Response<Void>) -> Unit,
        onFailure: () -> Unit
    ) {
        NotesRemoteDBHelper.insertNote(
            noteData = noteData,
            onResponse = onResponse,
            onFailure = onFailure
        )
    }

    fun removeNote(
        noteID: Int,
        onResponse: (response: Response<Void>) -> Unit,
        onFailure: () -> Unit
    ) {
        NotesRemoteDBHelper.removeNote(
            noteID = noteID,
            onResponse = onResponse,
            onFailure = onFailure
        )
    }

    fun updateNote(
        noteData: NoteData,
        onResponse: (response: Response<Void>) -> Unit,
        onFailure: () -> Unit
    ) {
        NotesRemoteDBHelper.updateNote(
            noteData = noteData,
            onResponse = onResponse,
            onFailure = onFailure
        )
    }
}