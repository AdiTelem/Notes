package com.example.notes.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.notes.model.NoteData
import com.example.notes.ui.theme.NoteTitleOrange
import com.example.notes.ui.theme.typography


@Composable
fun Note( noteData: NoteData,
          onClick: (NoteData) -> Unit,
          onLongClick: (NoteData) -> Unit) {
    Box(modifier = Modifier.height(300.dp)
                            .width(100.dp)
                            .clip(RoundedCornerShape(percent = 10))
                            .border(width = 2.dp,
                                brush = Brush.radialGradient(colorStops = arrayOf(1f to Color.Black, 1f to Color.Black)),
                                shape = RoundedCornerShape(percent = 10))
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to NoteTitleOrange,
                                        0.14f to NoteTitleOrange,
                                        0.14f to Color.DarkGray,
                                        0.145f to Color.DarkGray,
                                        0.145f to Color(0xFFD7C504),
                                        1f to Color(0xFFD7C504)
                                    )
                                )
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { onClick(noteData) },
                                    onLongPress = { onLongClick(noteData) }
                                )
                            }
    ) {
        Column {
                Text(
                    text = noteData.title,
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(0.13f)
                        .padding(5.dp)
                        .offset(y = 10.dp),
                    style = typography.titleLarge,
                    color = Color.Black
                )
                Text(
                    text = noteData.content,
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(0.87f)
                        .padding(5.dp),
                    style = typography.bodyMedium,
                    color = Color.Black
                )
        }
    }
}








