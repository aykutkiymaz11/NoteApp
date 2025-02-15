package com.example.noteapp.feature_note.presentation.notes.views

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.noteapp.R
import com.example.noteapp.feature_note.domain.model.Note
import com.example.noteapp.feature_note.presentation.notes.NotesViewModel
import com.example.noteapp.feature_note.presentation.util.Screen
import com.example.noteapp.ui.theme.background
import com.example.noteapp.ui.theme.searchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController, viewModel: NotesViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.shadow(20.dp, RoundedCornerShape(20.dp)),
                onClick = {
                    navController.navigate(Screen.AddEditNoteScreen.route)
                },
                backgroundColor = Color.Yellow
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(start = 6.dp, top = 12.dp),
                        text = "My Notes",
                        textAlign = TextAlign.Start,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )
                    SearchBar("Search Your Notes...")

                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 6.dp,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            println()
                            items(state.notes) { note ->
                                note.id?.let { NoteItem(note, navController) }
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun NoteItem(note: Note, navController: NavController) {

    //if content is longer than 40 char!!!
    val displayedContent =
        if (note.content.length >= 40) note.content.take(90) + "..." else note.content

    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .background(searchBar, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                navController.navigate(Screen.AddEditNoteScreen.route + "?noteId=${note.id ?: -1}")
            }
    ) {

        Image(
            painter = rememberAsyncImagePainter(model = note.imageUri?.let { Uri.parse(it) }),
            contentDescription = "Note Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Start
            )


            Text(
                modifier = Modifier.padding(4.dp),
                text = displayedContent,
                fontSize = 11.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .border(
                        border = BorderStroke(width = 1.dp, color = Color.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                text = "Notes",
                fontSize = 11.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Start
            )
        }
    }


}


