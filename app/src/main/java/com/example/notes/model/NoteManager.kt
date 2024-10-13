package com.example.notes.model

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteManager {
    companion object {
        private fun syncDBS(context: Context) {
            if (NotesRemoteDBHelper.isInternetAvailable(context)) {
                val localCounter = NotesLocalDBHelper(context).getActionCount()
                var remoteCounter = 0L

                CoroutineScope(Dispatchers.IO).launch {
                    remoteCounter = NotesRemoteDBHelper.getActionCount()
                }

                if (localCounter > remoteCounter) {
                    NotesRemoteDBHelper.updateRemote(
                        NotesLocalDBHelper(context).noteListFromDb(context),
                        localCounter
                    )
                }
            }
        }

        private fun increaseCounters(context: Context) {
            val localCounter = NotesLocalDBHelper(context).getActionCount()

            NotesLocalDBHelper(context).increaseActionCount()
            if (NotesRemoteDBHelper.isInternetAvailable(context)) {
                NotesRemoteDBHelper.setActionCount(localCounter + 1)
            }
        }

        fun deleteNote(noteID: Int, context: Context) {
            syncDBS(context)

            NotesLocalDBHelper(context).removeNote(noteID)

            if (NotesRemoteDBHelper.isInternetAvailable(context)) {
                NotesRemoteDBHelper.removeNote(noteID)
            }

            increaseCounters(context)
        }

        fun createNote(noteData: NoteData, context: Context) {
            syncDBS(context)

            noteData.id = NotesLocalDBHelper(context).insertNote(
                NoteData(
                    noteData.title,
                    noteData.content,
                    noteData.id
                )
            )

            if (NotesRemoteDBHelper.isInternetAvailable(context)) {
                NotesRemoteDBHelper.insertNote(noteData)
            }

            increaseCounters(context)
        }

        fun updateNote(noteData: NoteData, context: Context) {
            syncDBS(context)

            NotesLocalDBHelper(context).updateNote(noteData.id, noteData.title, noteData.content)

            if (NotesRemoteDBHelper.isInternetAvailable(context)) {
                NotesRemoteDBHelper.updateNote(noteData)
            }
            increaseCounters(context)
        }

        fun readAllNotes(context: Context): MutableList<NoteData> {
            return NotesLocalDBHelper(context).noteListFromDb(context)
        }

        fun readOneNote(context: Context, noteID: Int): NoteData {
            return NotesLocalDBHelper(context).readOneNote(noteID)
        }
    }
}