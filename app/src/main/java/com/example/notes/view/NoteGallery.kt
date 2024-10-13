package com.example.notes.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notes.R
import com.example.notes.ui.theme.NoteTitleOrange
import com.example.notes.viewmodel.NoteGalleryViewModel

@Composable
fun NoteGallery(nGVM: NoteGalleryViewModel) {
    val context = LocalContext.current
    val presentedList = nGVM.getNotesList(context)

    Scaffold (
            modifier = Modifier.statusBarsPadding(),
            topBar = { NotesGalleryTopBar ( {
                            sort -> nGVM.setSort(sort)
                        }, {
                            search -> nGVM.setSearch(search)
                        })
               },
              floatingActionButton = { AddNoteButton {
                  nGVM.createNewNote()
              } }) {
        paddingValues ->
        NotesList(presentedList, { noteData ->
            nGVM.editNote(noteData.id)
        }, {
            noteData -> nGVM.deleteRequest(noteData.id)
        }, paddingValues)
    }

    if (nGVM.isDeleteRequested())
    {
        DeletionConfirmationDialog(
            onConfirm = {
                nGVM.deleteConfirmed(context)
            }
        ) {
            nGVM.deleteDismissed()
        }
    }

}

@Composable
fun DeletionConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.8f),
        icon = {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        },
        title = {
            Text(text = "Confirmation")
        },
        text = {
            Text(text = "Are you sure you would like to delete that?")
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NoteTitleOrange)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NoteTitleOrange)
            ) {
                Text("Dismiss")
            }
        },
        textContentColor = Color.Gray,
        iconContentColor = NoteTitleOrange,
        containerColor = Color.White,
        titleContentColor = Color.Black
    )
}

@Composable
fun AddNoteButton(onClick: () -> Unit)
{
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.offset(5.dp, 5.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pencil),
            contentDescription = stringResource(id = R.string.pencil),
            modifier = Modifier.size(64.dp)
        )
    }
}