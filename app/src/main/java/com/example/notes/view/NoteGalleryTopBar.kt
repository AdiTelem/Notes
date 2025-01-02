package com.example.notes.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notes.R
import com.example.notes.model.enums.SortOptions
import com.example.notes.ui.theme.NoteTitleOrange


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesGalleryTopBar(
    onSortOptions: (SortOptions) -> Unit,
    onSearchRequest: (String) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.height(60.dp)
        .background(
            Brush.verticalGradient(
                colorStops = arrayOf(
                    0f to NoteTitleOrange,
                    0.98f to NoteTitleOrange,
                    0.98f to Color.Black,
                    1f to Color.Black
                )
            )
        )
    ) {
        TopAppBar(
            title = { Text("Notes") },
            colors = TopAppBarColors(Color.Transparent, Color.Transparent, Color.Black, Color.Black, Color.Black),
            actions = {
                var searchBar by remember { mutableStateOf("") }
                BasicTextField(
                    value = searchBar,
                    onValueChange = { searchBar = it },
                    modifier = Modifier.width(100.dp),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .background(
                                    Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0f to NoteTitleOrange,
                                            0.98f to NoteTitleOrange,
                                            0.98f to Color.Black,
                                            1f to Color.Black
                                        )
                                    ))
                        ) {
                            innerTextField()
                        }
                    },
                )

                //search button
                IconButton(onClick = {
                    onSearchRequest(searchBar)
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search)
                    )
                }

                //sort expand
                IconButton(onClick = {
                    showSortMenu = true
                }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.sort_options)
                    )
                }

                //sort options
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("By Oldest") },
                        onClick = {
                            onSortOptions(SortOptions.DATE_ASCEND)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("By Newest") },
                        onClick = {
                            onSortOptions(SortOptions.DATE_DESCEND)
                            showSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("By ABC") },
                        onClick = {
                            onSortOptions(SortOptions.ABC)
                            showSortMenu = false
                        }
                    )
                }
            }
        )
    }
}