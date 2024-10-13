package com.example.notes.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.notes.model.NoteData

@Composable
fun NotesList (notes: List<NoteData>,
               onClick: (NoteData) -> Unit,
               onLongClick: (NoteData) -> Unit,
               paddingValues: PaddingValues) {
    LazyVerticalGrid (columns = GridCells.Adaptive(150.dp),
                        modifier = Modifier.background(
                                        Brush.horizontalGradient(
                                            colorStops = arrayOf(
                                                0f to Color.LightGray,
                                                0.5f to Color.Gray,
                                                1f to Color.LightGray
                                            )
                                        )
                                    )
                                    .padding(paddingValues)
                                    .padding(15.dp)
                                    .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        items(notes, key = { it.id }) {
            note -> Note(note, onClick, onLongClick)
        }
    }
}