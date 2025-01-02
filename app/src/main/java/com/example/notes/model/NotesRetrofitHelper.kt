package com.example.notes.model

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotesRetrofitHelper {
    companion object {
        fun insertNote(noteData: NoteData, onResponse: (response: Response<Void>) -> Unit, onFailure: () -> Unit) {
            RetrofitInterface.instance.createNote(noteData).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    onResponse(response)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onFailure()
                }
            })
        }

        fun readAllNote(onResponse: (response: Response<List<NoteData> >) -> Unit, onFailure: () -> Unit) {
            RetrofitInterface.instance.getAllNote().enqueue(object : Callback<List<NoteData> > {
                override fun onResponse(call: Call<List<NoteData> >, response: Response<List<NoteData> >) {
                    onResponse(response)
                }

                override fun onFailure(call: Call<List<NoteData> >, t: Throwable) {
                    onFailure()
                }
            })
        }

        fun readNote(noteID: Int, onResponse: (response: Response<NoteData>) -> Unit, onFailure: () -> Unit) {
            RetrofitInterface.instance.getNoteByID(noteID).enqueue(object : Callback<NoteData>  {
                override fun onResponse(call: Call<NoteData>, response: Response<NoteData>) {
                    onResponse(response)
                }

                override fun onFailure(call: Call<NoteData>, t: Throwable) {
                    onFailure()
                }
            })
        }

        fun removeNote(noteID: Int, onResponse: (response: Response<Void>) -> Unit, onFailure: () -> Unit) {
            RetrofitInterface.instance.deleteNote(noteID).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    onResponse(response)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onFailure()
                }
            })
        }

        fun updateNote(noteData: NoteData, onResponse: (response: Response<Void>) -> Unit, onFailure: () -> Unit) {
            RetrofitInterface.instance.updateNote(noteData.id, noteData).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    onResponse(response)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onFailure()
                }
            })
        }
    }
}