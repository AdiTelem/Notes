package com.example.notes

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.notes.model.NoteData
import com.example.notes.model.NotesRetrofitHelper
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
        NotesRetrofitHelper.readAllNote(
            onResponse = onResponse,
            onFailure = onFailure
        )
    }


    fun insertNote(
        noteData: NoteData,
        onResponse: (response: Response<Void>) -> Unit,
        onFailure: () -> Unit
    ) {
        NotesRetrofitHelper.insertNote(
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
        NotesRetrofitHelper.removeNote(
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
        NotesRetrofitHelper.updateNote(
            noteData = noteData,
            onResponse = onResponse,
            onFailure = onFailure
        )
    }
}