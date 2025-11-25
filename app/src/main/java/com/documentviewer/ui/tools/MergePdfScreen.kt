package com.documentviewer.ui.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.documentviewer.core.utils.PdfUtils
import com.documentviewer.data.model.DocumentFile
import com.documentviewer.data.model.FileType
import com.documentviewer.data.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergePdfScreen(
    navController: NavController,
    viewModel: MergePdfViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    var showSuccess by remember { mutableStateOf(false) }
    var outputFileName by remember { mutableStateOf("") }
    var savedFilePath by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadPdfFiles()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Merge PDF") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState.selectedFiles.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearSelection() }) {
                            Text("Clear (${uiState.selectedFiles.size})")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.MergeType,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Select PDFs to Merge",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Tap to select multiple PDF files. They will be merged in the order you select them.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.pdfFiles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                "No PDF files found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.pdfFiles) { pdf ->
                            val isSelected = uiState.selectedFiles.contains(pdf)
                            val selectionIndex = uiState.selectedFiles.indexOf(pdf).takeIf { it >= 0 }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleSelection(pdf) },
                                colors = if (isSelected) {
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                } else {
                                    CardDefaults.cardColors()
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected && selectionIndex != null) {
                                        Surface(
                                            shape = MaterialTheme.shapes.small,
                                            color = MaterialTheme.colorScheme.primary
                                        ) {
                                            Box(
                                                modifier = Modifier.size(40.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "${selectionIndex + 1}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    } else {
                                        Checkbox(
                                            checked = false,
                                            onCheckedChange = null
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            pdf.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            formatFileSize(pdf.size),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.selectedFiles.size >= 2) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = outputFileName,
                                onValueChange = { outputFileName = it },
                                label = { Text("Output filename (optional)") },
                                placeholder = { Text("Merged_PDF") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    val fileName = outputFileName.ifEmpty {
                                        "Merged_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}"
                                    }
                                    viewModel.mergePdfs(context, fileName)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isProcessing
                            ) {
                                if (uiState.isProcessing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Merging...")
                                } else {
                                    Icon(Icons.Filled.MergeType, "Merge")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Merge ${uiState.selectedFiles.size} PDFs")
                                }
                            }
                        }
                    }
                }
            }

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(uiState.mergeSuccess, uiState.savedFilePath) {
        if (uiState.mergeSuccess) {
            showSuccess = true
            savedFilePath = uiState.savedFilePath ?: ""
        }
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {
                showSuccess = false
                viewModel.resetState()
                navController.navigateUp()
            },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            title = { Text("Success") },
            text = {
                Column {
                    Text("PDFs merged successfully!")
                    if (savedFilePath.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Saved to Downloads folder",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    viewModel.resetState()
                    navController.navigateUp()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}

@HiltViewModel
class MergePdfViewModel @Inject constructor(
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MergePdfUiState())
    val uiState: StateFlow<MergePdfUiState> = _uiState

    fun loadPdfFiles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                documentRepository.scanDocumentsByType(FileType.PDF).collect { files ->
                    _uiState.value = _uiState.value.copy(
                        pdfFiles = files,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun toggleSelection(pdf: DocumentFile) {
        val currentSelected = _uiState.value.selectedFiles.toMutableList()
        if (currentSelected.contains(pdf)) {
            currentSelected.remove(pdf)
        } else {
            currentSelected.add(pdf)
        }
        _uiState.value = _uiState.value.copy(selectedFiles = currentSelected)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedFiles = emptyList())
    }

    fun mergePdfs(context: android.content.Context, outputFileName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            try {
                val result = withContext(Dispatchers.IO) {
                    // Save to Downloads folder using MediaStore
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "$outputFileName.pdf")
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                        }
                    }

                    val uri = context.contentResolver.insert(
                        android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        contentValues
                    )

                    if (uri != null) {
                        val success = PdfUtils.mergePdfs(
                            context,
                            _uiState.value.selectedFiles.map { android.net.Uri.parse(it.uri.toString()) },
                            uri
                        )

                        if (success) {
                            Pair(true, android.os.Environment.DIRECTORY_DOWNLOADS + "/$outputFileName.pdf")
                        } else {
                            Pair(false, "")
                        }
                    } else {
                        Pair(false, "")
                    }
                }

                if (result.first) {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        mergeSuccess = true,
                        savedFilePath = result.second,
                        selectedFiles = emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = "Failed to merge PDFs"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = MergePdfUiState()
    }
}

data class MergePdfUiState(
    val pdfFiles: List<DocumentFile> = emptyList(),
    val selectedFiles: List<DocumentFile> = emptyList(),
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val mergeSuccess: Boolean = false,
    val savedFilePath: String? = null
)
