package com.example.notes.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.viewmodel.EditScreenViewModel
import com.example.notes.viewmodel.NavigationViewModel
import com.example.notes.viewmodel.NoteGalleryViewModel

@Composable
fun NotesAppNavigation() {
    val navController = rememberNavController()
    var nVM = NavigationViewModel(LocalContext.current)

    NavHost(navController = navController, startDestination = "noteGallery") {
        composable("noteGallery") {
            val notesListViewModel = NoteGalleryViewModel(navController, nVM.notes, LocalContext.current) // Retrieve ViewModel
            NoteGallery(notesListViewModel)
        }

        composable("editNote/{noteID}") { backStackEntry ->
            val noteID = backStackEntry.arguments?.getString("noteID")?.toIntOrNull()

            if (noteID != null) {
                val editScreenViewModel = EditScreenViewModel(navController, nVM.notes, noteID)
                EditScreen(editScreenViewModel)
            } else {
                throw IllegalStateException("note to edit not found")
            }
        }
    }
}
