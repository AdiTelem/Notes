package com.example.notes.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.notes.ui.theme.NoteContentYellow
import com.example.notes.ui.theme.NoteEditCancel
import com.example.notes.ui.theme.NoteEditSubmit
import com.example.notes.ui.theme.NoteTitleOrange
import com.example.notes.ui.theme.typography
import com.example.notes.viewmodel.EditScreenViewModel

@Composable
fun EditScreen(eSVM: EditScreenViewModel) {
    val context = LocalContext.current

    Box (modifier = Modifier.statusBarsPadding()
                            .background(NoteTitleOrange)
                            .navigationBarsPadding()
                            .fillMaxWidth()
                            .fillMaxHeight()) {
        Column (modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = eSVM.title.value,
                onValueChange = { eSVM.title.value = it },
                placeholder = { Text("Title", color = Color.Black) },
                modifier = Modifier.fillMaxWidth()
                                    .height(60.dp),
                textStyle = typography.titleLarge,
                colors = TextFieldDefaults.colors(focusedContainerColor = NoteTitleOrange,
                                                    unfocusedContainerColor = NoteTitleOrange,
                                                    cursorColor = Color.Black,
                                                    focusedTextColor = Color.Black,
                                                    unfocusedTextColor = Color.Black,
                                                    unfocusedIndicatorColor = Color.Black,
                                                    focusedIndicatorColor = Color.Black
                )
            )
            TextField(
                value = eSVM.content.value,
                onValueChange = { eSVM.content.value = it },
                placeholder = { Text("Content", color = Color.Black) },
                modifier = Modifier.fillMaxWidth().weight(0.7f),
                textStyle = typography.bodyMedium,
                colors = TextFieldDefaults.colors(focusedContainerColor = NoteContentYellow,
                                                    unfocusedContainerColor = NoteContentYellow,
                                                    cursorColor = Color.Black,
                                                    focusedTextColor = Color.Black,
                                                    unfocusedTextColor = Color.Black,
                                                    disabledTextColor = Color.Black,
                                                    unfocusedIndicatorColor = Color.Black,
                                                    focusedIndicatorColor = Color.Black
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                                    .height(40.dp),
            ) {
                Button(
                    onClick = { eSVM.dismiss() },
                    modifier = Modifier.fillMaxHeight().weight(0.5f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors().copy(containerColor = NoteEditCancel,
                                                                contentColor = Color.Black
                    )
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { eSVM.submitNote(context) },
                    modifier = Modifier.fillMaxHeight().weight(0.5f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors().copy(containerColor = NoteEditSubmit,
                                                                contentColor = Color.Black
                    )
                ) {
                    Text("Ok")
                }
            }
        }
    }
}