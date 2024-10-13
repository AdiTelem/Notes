package com.example.notes.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
    not implemented GET requests due to lack of time designing local DB syncing mechanism
    with remote DB and controlling more than a single source of truth.
*/

class NotesRemoteDBHelper {
    companion object {
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        fun removeNote(noteID: Int) {
            RetrofitInterface.instance.deleteNote(noteID).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    // no handler for response
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // no handler for failure
                }
            })
        }

        fun insertNote(noteData: NoteData) {
            RetrofitInterface.instance.createNote(noteData).enqueue(object : Callback<NoteData> {
                override fun onResponse(call: Call<NoteData>, response: Response<NoteData>) {
                    // no handler for response
                }

                override fun onFailure(call: Call<NoteData>, t: Throwable) {
                    // no handler for failure
                }
            })
        }

        fun updateNote(noteData: NoteData) {
            RetrofitInterface.instance.updateNote(noteData.id, noteData).enqueue(object : Callback<NoteData> {
                override fun onResponse(call: Call<NoteData>, response: Response<NoteData>) {
                    // no handler for response
                }

                override fun onFailure(call: Call<NoteData>, t: Throwable) {
                    // no handler for failure
                }
            })
        }

        //if -1 returned, value doesnt exist
        suspend fun getActionCount(): Long {
            try {
            val response = RetrofitInterface.instance.getNoteByID(0).executeSuspend()

            if (!response.isSuccessful) {
                return -1
            }

            return response.body()!!.createTime

//            if (response.isSuccessful) {
//                val user = response.body()
//                println("success")
//            } else {
//                println("Failed request")
//            }
        } catch (e: Exception) {
            println("Error occurred: ${e.localizedMessage}")
                return -1
        }
        }

        fun setActionCount(actionCount: Long) {
            val actionCounterRecord = NoteData(
                    id = 0,
                    title = "",
                    content = "",
                    createTime = actionCount
            )

            RetrofitInterface.instance.createNote(actionCounterRecord).enqueue(object : Callback<NoteData> {
                override fun onResponse(call: Call<NoteData>, response: Response<NoteData>) {
                    // no handler for response
                }

                override fun onFailure(call: Call<NoteData>, t: Throwable) {
                    // no handler for failure
                }
            })
        }

        fun updateRemote(notes: MutableList<NoteData>, actionCounter: Long) {
            setActionCount(actionCounter)

            notes.forEach {
                insertNote(it)
            }
        }

        private suspend fun <T> Call<T>.executeSuspend(): Response<T> {
            return withContext(Dispatchers.IO) {
                execute()
            }
        }
    }
}