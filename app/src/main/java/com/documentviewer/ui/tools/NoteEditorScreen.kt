package com.documentviewer.ui.tools

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.documentviewer.data.local.entity.NoteEntity
import com.documentviewer.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    navController: NavController,
    noteId: Long,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isLoaded by remember { mutableStateOf(false) }

    // Load note if editing existing note
    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            viewModel.loadNote(noteId)
        }
        isLoaded = true
    }

    // Observe note state
    val noteState by viewModel.noteState.collectAsState()
    
    LaunchedEffect(noteState) {
        noteState?.let { note ->
            title = note.title
            content = note.content
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == 0L) "New Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            if (noteId == 0L) {
                                // Create new note
                                viewModel.createNote(title, content)
                            } else {
                                // Update existing note
                                viewModel.updateNote(noteId, title, content)
                            }
                            navController.navigateUp()
                        }
                    }) {
                        Icon(Icons.Default.Check, "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _noteState = MutableStateFlow<NoteEntity?>(null)
    val noteState: StateFlow<NoteEntity?> = _noteState.asStateFlow()

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            try {
                noteRepository.getNoteById(noteId).collect { note ->
                    _noteState.value = note
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun createNote(title: String, content: String) {
        viewModelScope.launch {
            try {
                val note = NoteEntity(
                    title = title,
                    content = content,
                    createdAt = System.currentTimeMillis(),
                    modifiedAt = System.currentTimeMillis()
                )
                noteRepository.insertNote(note)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateNote(noteId: Long, title: String, content: String) {
        viewModelScope.launch {
            try {
                _noteState.value?.let { note ->
                    val updatedNote = note.copy(
                        title = title,
                        content = content,
                        modifiedAt = System.currentTimeMillis()
                    )
                    noteRepository.updateNote(updatedNote)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
