package com.example.noteapp.feature_note.presentation.add_edit_note.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.noteapp.R
import com.example.noteapp.feature_note.presentation.add_edit_note.AddEditNoteEvent
import com.example.noteapp.feature_note.presentation.add_edit_note.AddEditNoteViewModel
import com.example.noteapp.ui.theme.background
import com.example.noteapp.ui.theme.searchBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest

private fun openGallery(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    launcher.launch(intent)
}

@ExperimentalMaterial3Api
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteId: String,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {


    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            selectedImageUri?.let { uri ->

                println("Seçilen Resim: $uri")
                viewModel.onPickImage(selectedImageUri)
            }
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery(galleryLauncher)
        } else {
            println("Galeriye erişim izni reddedildi.")
        }
    }


    val hasReadPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED


    var taskList by remember { mutableStateOf(mutableListOf<Pair<Boolean, String>>()) }
    val options = listOf("One", "Two", "Three", "Four", "Five")
    val colors = listOf(Color.Yellow, Color.Red, Color.Blue, Color.Black, Color.White)
    var selectedOption by remember { mutableStateOf("") }

    var text by remember { mutableStateOf("") }

    val scaffoldState = rememberScaffoldState()

    val sheetScaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()


    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.message)
                }

                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
            }
        }
    }


    BottomSheetScaffold(
        modifier = Modifier
            .background(searchBar),
        scaffoldState = sheetScaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(searchBar)
                    .padding(8.dp)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEachIndexed { index, option ->
                        CustomRadioButton(
                            selected = selectedOption == option,
                            color = colors[index],
                            onClick = { selectedOption = option }
                        )
                    }
                }

                Text(
                    text = "Pick Color",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.copy),
                                contentDescription = "Make Copy",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(start = 16.dp, end = 8.dp),
                                tint = Color.White
                            )
                            Text(
                                text = "Make Copy",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                contentDescription = "Add Image",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable {
                                        if (hasReadPermission) {
                                            openGallery(galleryLauncher)
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
                                    },
                                tint = Color.White
                            )
                            Text(
                                text = "Add Image",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    viewModel.onEvent(AddEditNoteEvent.DeleteNote(noteId = noteId.toInt()))
                                    navController.navigateUp()
                                },
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.delete),
                                contentDescription = "Delete Note",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(start = 16.dp, end = 8.dp),
                                tint = Color.Red
                            )
                            Text(
                                text = "Delete Note",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        },
        sheetContainerColor = searchBar
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(background)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = { navController.navigateUp() }) {
                    Icon(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(top = 24.dp),
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Back Button",
                        tint = Color.White
                    )
                }
                IconButton(
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = {
                        viewModel.onEvent(AddEditNoteEvent.SaveNote)
                        println("Save clicked")
                    }) {
                    Icon(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(top = 24.dp),
                        painter = painterResource(R.drawable.tick),
                        contentDescription = "Save Button",
                        tint = Color.White
                    )
                }

            }

            TextField(
                value = viewModel.noteTitle.value.text,
                onValueChange = { newText -> viewModel.onEvent(AddEditNoteEvent.EnteredTitle(newText)) },
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                modifier = Modifier
                    .padding(top = 32.dp)
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .onFocusChanged { viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it)) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Text(
                modifier = Modifier
                    .padding(start = 14.dp),
                text = "Date",
                color = Color.DarkGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Yellow)
                        .padding(6.dp)
                        .height(40.dp)
                        .width(0.15.dp)
                ) { }
                TextField(
                    value = viewModel.noteSubTitle.value.text,
                    onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredSubTitle(it)) },
                    maxLines = 2,
                    singleLine = false,
                    textStyle = TextStyle(
                        color = Color.LightGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .background(Color.Transparent)
                        .onFocusChanged { viewModel.onEvent(AddEditNoteEvent.ChangeSubTitleFocus(it)) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }



            TextField(
                value = viewModel.noteContent.value.text,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredContent(it)) },
                maxLines = 12,
                singleLine = false,
                textStyle = TextStyle(
                    color = Color.LightGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .onFocusChanged { viewModel.onEvent(AddEditNoteEvent.ChangeContentFocus(it)) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Text(
                modifier = Modifier.padding(14.dp),
                text = "Tasks",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            taskList.forEachIndexed { index, task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Yellow,
                            unselectedColor = Color.White
                        ),
                        selected = task.first,
                        onClick = {
                            taskList = taskList.toMutableList().apply {
                                this[index] = this[index].copy(first = !task.first)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = task.second,
                        onValueChange = { newText ->
                            taskList = taskList.toMutableList().apply {
                                this[index] = this[index].copy(second = newText)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(text = "Enter task...") },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.LightGray,
                            unfocusedIndicatorColor = Color.LightGray
                        )
                    )
                }
            }

            TextButton(
                modifier = Modifier.padding(top = 12.dp),
                onClick = {
                    taskList = taskList.toMutableList().apply {
                        add(Pair(false, ""))
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add Task",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp),
                    tint = Color.White
                )
                Text(text = "Add Task", color = Color.White)
            }
        }
    }

}


@Composable
fun CustomRadioButton(selected: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(25.dp)
            .background(color = color, shape = CircleShape)
            .border(
                width = if (selected) 4.dp else 2.dp,
                color = if (selected) color else Color.Gray,
                shape = CircleShape
            )
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}