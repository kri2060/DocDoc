# Implementation Guide

This guide provides templates and instructions for completing all remaining screens and features.

## Quick Start Checklist

### Already Completed ✅
- [x] Project structure and build configuration
- [x] Database schema with Room
- [x] Repositories and data layer
- [x] Navigation graph
- [x] Home screen with team info
- [x] Theme and UI components
- [x] Dependency injection setup
- [x] File utilities

### To Be Implemented

#### High Priority Screens
1. [ ] FileListScreen - Browse files by category
2. [ ] PdfViewerScreen - View PDFs with reading position
3. [ ] ImagesToPdfScreen - Convert images to PDF
4. [ ] SettingsScreen - App configuration

#### Medium Priority
5. [ ] RecentFilesScreen
6. [ ] FavoritesScreen
7. [ ] MyCreationScreen
8. [ ] SearchScreen with OCR
9. [ ] NotepadScreen and NoteEditorScreen
10. [ ] OcrScannerScreen

#### PDF Operations
11. [ ] MergePdfScreen
12. [ ] PdfToImagesScreen
13. [ ] AddPasswordScreen
14. [ ] RemovePasswordScreen
15. [ ] TextToPdfScreen

#### Advanced Features
16. [ ] PdfMarkupScreen with annotations
17. [ ] TtsService for text-to-speech
18. [ ] WorkManager workers for background tasks

---

## Screen Implementation Templates

### 1. FileListScreen.kt

```kotlin
package com.documentviewer.ui.filelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.documentviewer.data.model.DocumentFile
import com.documentviewer.data.model.FileType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(
    navController: NavController,
    fileType: String,
    viewModel: FileListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(fileType) {
        viewModel.loadFiles(FileType.valueOf(fileType))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileType) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSort() }) {
                        Icon(Icons.Default.Sort, "Sort")
                    }
                    IconButton(onClick = { /* Show search */ }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                ErrorMessage(uiState.error!!)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.files) { file ->
                        FileListItem(
                            file = file,
                            onFileClick = { /* Open file */ },
                            onFavoriteClick = { viewModel.toggleFavorite(file) },
                            onShareClick = { /* Share file */ },
                            onDeleteClick = { viewModel.deleteFile(file) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FileListItem(
    file: DocumentFile,
    onFileClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onFileClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${FileUtils.formatFileSize(file.size)} • ${formatDate(file.lastModified)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row {
                IconButton(onClick = onFavoriteClick) {
                    Icon(Icons.Default.Star, "Favorite")
                }
                IconButton(onClick = onShareClick) {
                    Icon(Icons.Default.Share, "Share")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
    }
}

// ViewModel
@HiltViewModel
class FileListViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentFileRepository: RecentFileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FileListUiState())
    val uiState: StateFlow<FileListUiState> = _uiState.asStateFlow()

    fun loadFiles(fileType: FileType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                documentRepository.scanDocumentsByType(fileType).collect { files ->
                    _uiState.update {
                        it.copy(
                            files = files,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun toggleFavorite(file: DocumentFile) {
        viewModelScope.launch {
            // Implementation
        }
    }

    fun deleteFile(file: DocumentFile) {
        viewModelScope.launch {
            // Implementation
        }
    }

    fun toggleSort() {
        // Implementation
    }
}

data class FileListUiState(
    val files: List<DocumentFile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### 2. ImagesToPdfScreen.kt

```kotlin
package com.documentviewer.ui.pdf

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesToPdfScreen(
    navController: NavController,
    viewModel: ImagesToPdfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.addImages(uris)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Images to PDF") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.images.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.createPdf() },
                    icon = { Icon(Icons.Default.PictureAsPdf, "Create") },
                    text = { Text("Create PDF") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PDF Settings", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Page Size Dropdown
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = { }
                    ) {
                        TextField(
                            value = uiState.settings.pageSize.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Page Size") }
                        )
                    }

                    // Orientation Toggle
                    Row {
                        FilterChip(
                            selected = uiState.settings.orientation == Orientation.PORTRAIT,
                            onClick = { viewModel.setOrientation(Orientation.PORTRAIT) },
                            label = { Text("Portrait") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = uiState.settings.orientation == Orientation.LANDSCAPE,
                            onClick = { viewModel.setOrientation(Orientation.LANDSCAPE) },
                            label = { Text("Landscape") }
                        )
                    }

                    // Quality Slider
                    Text("Quality: ${uiState.settings.quality}%")
                    Slider(
                        value = uiState.settings.quality.toFloat(),
                        onValueChange = { viewModel.setQuality(it.toInt()) },
                        valueRange = 50f..100f
                    )
                }
            }

            // Image List
            if (uiState.images.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Default.AddPhotoAlternate, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Images")
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.images) { uri ->
                        ImageListItem(
                            uri = uri,
                            onRemove = { viewModel.removeImage(uri) }
                        )
                    }

                    item {
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add More Images")
                        }
                    }
                }
            }
        }

        // Loading Dialog
        if (uiState.isProcessing) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Creating PDF") },
                text = {
                    Column {
                        Text("Please wait...")
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {}
            )
        }

        // Success Dialog
        if (uiState.outputFile != null) {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("Success!") },
                text = { Text("PDF created successfully in My Creation folder") },
                confirmButton = {
                    TextButton(onClick = {
                        navController.navigate(Screen.MyCreation.route)
                    }) {
                        Text("View")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.resetState() }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun ImageListItem(uri: Uri, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(uri.lastPathSegment ?: "Image", modifier = Modifier.weight(1f))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, "Remove")
            }
        }
    }
}

// ViewModel
@HiltViewModel
class ImagesToPdfViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImagesToPdfUiState())
    val uiState: StateFlow<ImagesToPdfUiState> = _uiState.asStateFlow()

    fun addImages(uris: List<Uri>) {
        _uiState.update {
            it.copy(images = it.images + uris)
        }
    }

    fun removeImage(uri: Uri) {
        _uiState.update {
            it.copy(images = it.images - uri)
        }
    }

    fun setOrientation(orientation: Orientation) {
        _uiState.update {
            it.copy(settings = it.settings.copy(orientation = orientation))
        }
    }

    fun setQuality(quality: Int) {
        _uiState.update {
            it.copy(settings = it.settings.copy(quality = quality))
        }
    }

    fun createPdf() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            try {
                // Use PDFBox or iText to create PDF
                val outputFile = createPdfFromImages(
                    context,
                    uiState.value.images,
                    uiState.value.settings
                )
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        outputFile = outputFile
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = ImagesToPdfUiState()
    }

    private suspend fun createPdfFromImages(
        context: Context,
        images: List<Uri>,
        settings: PdfOperationSettings
    ): File = withContext(Dispatchers.IO) {
        val outputDir = FileUtils.getMyCreationFolder(context)
        val outputFile = File(outputDir, "PDF_${System.currentTimeMillis()}.pdf")

        // Implementation using PDFBox
        val document = PDDocument()
        try {
            images.forEach { uri ->
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val page = PDPage(PDRectangle.A4)
                    document.addPage(page)

                    // Draw bitmap on page
                    val contentStream = PDPageContentStream(document, page)
                    // ... bitmap conversion and drawing logic
                    contentStream.close()
                }
            }
            document.save(outputFile)
        } finally {
            document.close()
        }

        outputFile
    }
}

data class ImagesToPdfUiState(
    val images: List<Uri> = emptyList(),
    val settings: PdfOperationSettings = PdfOperationSettings(),
    val isProcessing: Boolean = false,
    val outputFile: File? = null,
    val error: String? = null
)
```

### 3. PdfViewerScreen.kt with Last Page Recall

```kotlin
package com.documentviewer.ui.viewer

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.barteksc.pdfviewer.PDFView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    navController: NavController,
    fileUri: String,
    viewModel: PdfViewerViewModel = hiltViewModel()
) {
    val uri = Uri.parse(fileUri)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uri) {
        viewModel.loadPdf(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.fileName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.startTts() }) {
                        Icon(Icons.Default.RecordVoiceOver, "Text to Speech")
                    }
                    IconButton(onClick = { /* Navigate to markup */ }) {
                        Icon(Icons.Default.Edit, "Annotate")
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, "Share")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Page ${uiState.currentPage} / ${uiState.totalPages}")

                    IconButton(onClick = { viewModel.previousPage() }) {
                        Icon(Icons.Default.NavigateBefore, "Previous")
                    }

                    IconButton(onClick = { viewModel.nextPage() }) {
                        Icon(Icons.Default.NavigateNext, "Next")
                    }
                }
            }
        }
    ) { padding ->
        AndroidView(
            factory = { context ->
                PDFView(context, null).apply {
                    fromUri(uri)
                        .defaultPage(uiState.currentPage)
                        .onPageChange { page, pageCount ->
                            viewModel.onPageChanged(page, pageCount)
                        }
                        .onLoad { numPages ->
                            viewModel.onPdfLoaded(numPages)
                        }
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .load()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

// ViewModel
@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    private val readingPositionRepository: ReadingPositionRepository,
    private val recentFileRepository: RecentFileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PdfViewerUiState())
    val uiState: StateFlow<PdfViewerUiState> = _uiState.asStateFlow()

    fun loadPdf(uri: Uri) {
        viewModelScope.launch {
            val filePath = uri.toString()

            // Load last reading position
            readingPositionRepository.getPosition(filePath).collect { position ->
                position?.let {
                    _uiState.update { state ->
                        state.copy(currentPage = it.pageNumber)
                    }
                }
            }

            // Add to recent files
            // ... implementation
        }
    }

    fun onPageChanged(page: Int, pageCount: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentPage = page,
                    totalPages = pageCount
                )
            }

            // Save reading position
            readingPositionRepository.savePosition(
                ReadingPositionEntity(
                    filePath = uiState.value.filePath,
                    pageNumber = page,
                    lastReadAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun onPdfLoaded(numPages: Int) {
        _uiState.update { it.copy(totalPages = numPages) }
    }

    fun previousPage() {
        if (uiState.value.currentPage > 0) {
            _uiState.update { it.copy(currentPage = it.currentPage - 1) }
        }
    }

    fun nextPage() {
        if (uiState.value.currentPage < uiState.value.totalPages - 1) {
            _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        }
    }

    fun startTts() {
        // Start TTS service
    }
}

data class PdfViewerUiState(
    val filePath: String = "",
    val fileName: String = "",
    val currentPage: Int = 0,
    val totalPages: Int = 0
)
```

## WorkManager Implementation

### PdfMergeWorker.kt

```kotlin
package com.documentviewer.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import java.io.File

class PdfMergeWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val inputPaths = inputData.getStringArray("input_files") ?: return Result.failure()
            val outputPath = inputData.getString("output_path") ?: return Result.failure()

            // Show notification
            showProgressNotification()

            // Merge PDFs
            val merger = PDFMergerUtility()
            inputPaths.forEach { path ->
                merger.addSource(File(path))
            }
            merger.destinationFileName = outputPath
            merger.mergeDocuments(null)

            // Update notification
            showSuccessNotification(outputPath)

            Result.success(workDataOf("output_path" to outputPath))
        } catch (e: Exception) {
            showErrorNotification(e.message)
            Result.failure(workDataOf("error" to e.message))
        }
    }

    private fun showProgressNotification() {
        // Implementation
    }

    private fun showSuccessNotification(path: String) {
        // Implementation with Open action
    }

    private fun showErrorNotification(message: String?) {
        // Implementation
    }
}
```

## TTS Service Implementation

```kotlin
package com.documentviewer.ui.viewer

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class TtsService : Service(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private val binder = TtsBinder()

    private val _state = MutableStateFlow(TtsState())
    val state: StateFlow<TtsState> = _state

    inner class TtsBinder : Binder() {
        fun getService(): TtsService = this@TtsService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _state.value = _state.value.copy(isPlaying = true)
                }

                override fun onDone(utteranceId: String?) {
                    _state.value = _state.value.copy(isPlaying = false)
                }

                override fun onError(utteranceId: String?) {
                    _state.value = _state.value.copy(isPlaying = false, error = "Error")
                }
            })
        }
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "utterance")
    }

    fun pause() {
        tts.stop()
        _state.value = _state.value.copy(isPlaying = false)
    }

    fun setSpeed(speed: Float) {
        tts.setSpeechRate(speed)
        _state.value = _state.value.copy(speed = speed)
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}

data class TtsState(
    val isPlaying: Boolean = false,
    val speed: Float = 1.0f,
    val error: String? = null
)
```

## Next Steps

1. **Implement FileListScreen** - Start here as it's used by many features
2. **Add PDF Viewer** - Critical for document viewing
3. **Implement PDF Operations** - Core functionality
4. **Add Search** - Advanced feature
5. **Complete TTS and Markup** - Advanced features

## Testing Each Screen

Test each implementation:
1. Navigation works
2. Data loads correctly
3. Error states display properly
4. Actions perform as expected
5. State persists correctly

## Common Patterns

### Error Handling
```kotlin
try {
    // Operation
} catch (e: SecurityException) {
    // Handle permission errors
} catch (e: IOException) {
    // Handle file errors
} catch (e: Exception) {
    // Handle general errors
}
```

### Permission Requests
```kotlin
val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { granted ->
    if (granted) {
        // Proceed
    }
}
```

### Loading States
```kotlin
data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

---

Good luck with implementation! The foundation is solid and well-structured.
