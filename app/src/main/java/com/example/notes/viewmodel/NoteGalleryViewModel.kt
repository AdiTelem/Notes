package com.example.notes.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.notes.model.NoteData
import com.example.notes.model.NoteManager
import com.example.notes.model.SortOptions

class NoteGalleryViewModel(
    private var navController: NavController,
    context: Context
): ViewModel() {

    companion object {
        private const val SORT_KEY = "sort"
    }

    private var notes = mutableStateListOf<NoteData>()
    private var isInit = false

    private var showDeleteDialog = mutableStateOf(false)
    private var deleteRequestNoteID = 0

    private var sharedPref: SharedPreferences
    private var sort = mutableStateOf(SortOptions.DATE_ASCEND)
    private var search = mutableStateOf("")

    init {
        sharedPref =  context.getSharedPreferences("GalleryPref", Context.MODE_PRIVATE)

        sort.value = SortOptions.valueOf(
                    sharedPref.getString(SORT_KEY, SortOptions.DATE_ASCEND.name) ?:
                        SortOptions.DATE_ASCEND.name
        )
    }

    //retrieves a filtered sorted list of notes
    fun getNotesList(context: Context): List<NoteData> {
        Log.v("SharedPref", "${sort.value} used")
        if (!isInit) {
            notes = NoteManager.readAllNotes(context).toMutableStateList()
            isInit = true
        }
        val updatedList: MutableList<NoteData> = notes.toMutableList()

        //remove counter
        updatedList.removeIf { it.id == 0 }

        // search
        updatedList.removeIf {
            !it.title
                .lowercase()
                .startsWith(search.value.lowercase())
        }

        //sort
        when (sort.value) {
            SortOptions.DATE_ASCEND -> updatedList.sortBy { it.createTime }
            SortOptions.DATE_DESCEND -> updatedList.sortByDescending { it.createTime }
            SortOptions.ABC -> updatedList.sortBy { it.title.lowercase() }
        }

        return updatedList
    }

    fun deleteRequest(id: Int) {
        showDeleteDialog.value = true
        deleteRequestNoteID = id
    }

    fun deleteConfirmed(context: Context) {
        notes.removeIf { it.id == deleteRequestNoteID }
        NoteManager.deleteNote(deleteRequestNoteID, context)
        showDeleteDialog.value = false
    }

    fun deleteDismissed() {
        showDeleteDialog.value = false
    }

    fun isDeleteRequested(): Boolean {
        return showDeleteDialog.value
    }

    fun createNewNote() {
        navController.navigate("editNote/0")
    }

    fun editNote(noteID: Int) {
        navController.navigate("editNote/$noteID")
    }

    fun setSort(newSort: SortOptions) {
        sort.value = newSort
        sharedPref.edit().putString(SORT_KEY, newSort.name).apply()

        Log.v("SharedPref", "${newSort.name} applied")

    }

    fun setSearch(searchRequest: String) {
        search.value = searchRequest
    }
}
