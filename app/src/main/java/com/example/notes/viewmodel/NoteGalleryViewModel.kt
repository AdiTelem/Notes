package com.example.notes.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.notes.model.NoteData
import com.example.notes.model.NoteManager

class NoteGalleryViewModel(private var navController: NavController): ViewModel() {
    private var notes = mutableStateListOf<NoteData>()
    private var isInit = false

    private var showDeleteDialog = mutableStateOf(false)
    private var deleteRequestNoteID = 0

    private var sort = mutableStateOf("date_ascend")
    private var search = mutableStateOf("")

    //retrieves a filtered sorted list of notes
    fun getNotesList(context: Context): List<NoteData> {
        if (!isInit) {
            notes = NoteManager.readAllNotes(context).toMutableStateList()
            isInit = true
        }
        val updatedList: MutableList<NoteData> = notes.toMutableList()

        //remove counter
        updatedList.removeIf{ it.id == 0 }

        // search
        updatedList.removeIf{ !it.title.lowercase().startsWith(search.value.lowercase()) }

        //sort
        when (sort.value) {
            "date_ascend" -> updatedList.sortBy { it.createTime }
            "date_descend" -> updatedList.sortByDescending { it.createTime }
            "abc_ascend" -> updatedList.sortBy { it.title.lowercase() }
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

    fun setSort(newSort: String) {
        sort.value = newSort
    }

    fun setSearch(searchRequest: String) {
        search.value = searchRequest
    }
}
