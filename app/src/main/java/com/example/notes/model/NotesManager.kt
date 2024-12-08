package com.example.notes.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.notes.R

class NotesManager() {

    var noteList = mutableStateListOf<NoteData>()

    companion object {
        private const val COUNTER_KEY = "counter"
    }

    fun readAllNotes(context: Context) {
        NotesRemoteDBHelper.readAllNote( { response ->
            if (response.isSuccessful) {
                noteList.addAll(response.body()?.toMutableStateList() ?: mutableStateListOf<NoteData>())
                Log.d("remote_db", "server response success on read all\nmessage ${response.message()}")
            } else {
                Log.d("remote_db", "server response ${response.code()} on read all")
            }

        }, {
            Log.d("remote_db", "server failure on read all")
        })
    }

    fun createNote(noteData: NoteData, context: Context) {
        var sharedPref = context.getSharedPreferences(
            context.getString(R.string.shared_pref_gallery),
            Context.MODE_PRIVATE
        )

        val newCounter: Int = (sharedPref.getInt(COUNTER_KEY, 0)) + 1

        noteData.id = newCounter
        NotesRemoteDBHelper.insertNote(noteData,
            { code ->
                if (code == 200) {
                    sharedPref.edit().putInt(COUNTER_KEY, newCounter).apply()
                    noteList.add(noteData)
                    Log.d("remote_db", "server response success on insert")
                } else {
                    Log.d("remote_db", "server response $code on insert")
                }

            }, {
                Log.d("remote_db", "server failure on insert")
            })
    }

        fun deleteNote(noteID: Int, context: Context) {
            NotesRemoteDBHelper.removeNote(
                noteID,
                { code ->
                    if (code == 200) {
                        noteList.removeIf { it.id == noteID }
                        Log.d("remote_db", "server response success on delete")
                    } else {
                        Log.d("remote_db", "server response $code on delete")
                    }

                }, {
                    Log.d("remote_db", "server failure on delete")
                })
        }


        fun updateNote(noteData: NoteData, context: Context) {
                NotesRemoteDBHelper.updateNote(
                    noteData,
                    { code ->
                        if (code == 200) {
                            noteList.removeIf { it.id == noteData.id }
                            noteList.add(noteData)
                            Log.d("remote_db", "server response success on update")
                        } else {
                            Log.d("remote_db", "server response $code on update")
                        }

                    }, {
                        Log.d("remote_db", "server failure on update")
                    })
        }
//
//
//        fun readOneNote(context: Context, noteID: Int): NoteData {
////            return NotesLocalDBHelper(context).readOneNote(noteID)
//        }

}