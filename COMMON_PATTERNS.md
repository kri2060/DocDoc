# Common Patterns & Code Snippets

Quick reference for common code patterns used throughout the project.

## Import Statements

### For Composable Screens
```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
```

### For ViewModels
```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
```

### For Repositories
```kotlin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
```

### For Room DAOs
```kotlin
import androidx.room.*
import kotlinx.coroutines.flow.Flow
```

## Screen Template

### Basic Screen Structure
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(
    navController: NavController,
    viewModel: MyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Screen") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.error != null -> ErrorState(uiState.error!!)
                else -> ContentState(uiState.data)
            }
        }
    }
}
```

## ViewModel Template

### Basic ViewModel
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getData().collect { data ->
                    _uiState.update {
                        it.copy(
                            data = data,
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

    fun performAction() {
        viewModelScope.launch {
            try {
                repository.doSomething()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

data class MyUiState(
    val data: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

## Common UI Components

### Loading State
```kotlin
@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

### Error State
```kotlin
@Composable
fun ErrorState(
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}
```

### Empty State
```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onAction) {
                    Text(actionText)
                }
            }
        }
    }
}
```

### Confirmation Dialog
```kotlin
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = if (isDestructive) {
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                } else {
                    ButtonDefaults.textButtonColors()
                }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}
```

## Permission Handling

### Request Permission
```kotlin
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyScreen() {
    val permissionState = rememberPermissionState(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.isGranted) {
        // Show content
    } else {
        // Show permission rationale
        PermissionRationale(
            onRequestPermission = { permissionState.launchPermissionRequest() }
        )
    }
}
```

## File Picking

### Single File Picker
```kotlin
val filePicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let { viewModel.handleFile(it) }
}

Button(onClick = { filePicker.launch("application/pdf") }) {
    Text("Pick PDF")
}
```

### Multiple Files Picker
```kotlin
val filesPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetMultipleContents()
) { uris: List<Uri> ->
    viewModel.handleFiles(uris)
}

Button(onClick = { filesPicker.launch("image/*") }) {
    Text("Pick Images")
}
```

### Directory Picker
```kotlin
val directoryPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocumentTree()
) { uri: Uri? ->
    uri?.let { viewModel.handleDirectory(it) }
}

Button(onClick = { directoryPicker.launch(null) }) {
    Text("Pick Folder")
}
```

## LazyColumn with Items

### Basic List
```kotlin
LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(
        items = uiState.items,
        key = { it.id }
    ) { item ->
        ItemCard(
            item = item,
            onClick = { viewModel.onItemClick(item) }
        )
    }
}
```

### List with Header
```kotlin
LazyColumn {
    item {
        SectionHeader("My Header")
    }
    items(uiState.items) { item ->
        ItemCard(item)
    }
}
```

### Grid Layout
```kotlin
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(16.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(uiState.items) { item ->
        ItemCard(item)
    }
}
```

## Navigation Patterns

### Navigate to Screen
```kotlin
// Without arguments
navController.navigate(Screen.MyScreen.route)

// With arguments
navController.navigate(Screen.Detail.createRoute(itemId = "123"))

// Pop back
navController.navigateUp()

// Pop to specific destination
navController.popBackStack(Screen.Home.route, inclusive = false)
```

### Navigate with Result
```kotlin
// In destination screen
navController.previousBackStackEntry
    ?.savedStateHandle
    ?.set("result_key", result)
navController.navigateUp()

// In calling screen
val result = navController.currentBackStackEntry
    ?.savedStateHandle
    ?.getLiveData<String>("result_key")
    ?.observeAsState()
```

## Repository Patterns

### Basic Repository
```kotlin
class MyRepository @Inject constructor(
    private val dao: MyDao
) {
    fun getAll(): Flow<List<MyEntity>> = dao.getAll()

    suspend fun insert(item: MyEntity) {
        dao.insert(item)
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }
}
```

### Repository with External Data Source
```kotlin
class MyRepository @Inject constructor(
    private val dao: MyDao,
    private val context: Context
) {
    suspend fun scanFiles(): Flow<List<DocumentFile>> = flow {
        val files = withContext(Dispatchers.IO) {
            // Scan files using ContentResolver
            scanFileSystem()
        }
        emit(files)
    }

    private fun scanFileSystem(): List<DocumentFile> {
        // Implementation
        return emptyList()
    }
}
```

## WorkManager Patterns

### Enqueue Worker
```kotlin
val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
    .setInputData(workDataOf(
        "key1" to "value1",
        "key2" to 123
    ))
    .build()

WorkManager.getInstance(context).enqueue(workRequest)
```

### Monitor Work Progress
```kotlin
WorkManager.getInstance(context)
    .getWorkInfoByIdLiveData(workRequest.id)
    .observe(lifecycleOwner) { workInfo ->
        when (workInfo.state) {
            WorkInfo.State.RUNNING -> { /* Update UI */ }
            WorkInfo.State.SUCCEEDED -> { /* Show success */ }
            WorkInfo.State.FAILED -> { /* Show error */ }
            else -> {}
        }
    }
```

### Worker Implementation
```kotlin
class MyWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val input = inputData.getString("key1")

            setProgress(workDataOf("progress" to 0))

            // Do work
            performTask(input)

            setProgress(workDataOf("progress" to 100))

            Result.success(workDataOf("output" to "result"))
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to e.message))
        }
    }

    private suspend fun performTask(input: String?) {
        // Implementation
    }
}
```

## Utility Functions

### Format File Size
```kotlin
fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
    return "%.2f %s".format(
        bytes / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}
```

### Format Date
```kotlin
fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            .format(Date(timestamp))
    }
}
```

### Get MIME Type
```kotlin
fun getMimeType(context: Context, uri: Uri): String? {
    return context.contentResolver.getType(uri)
}

fun getMimeTypeFromExtension(filename: String): String? {
    val extension = filename.substringAfterLast('.', "")
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}
```

## Testing Patterns

### ViewModel Test
```kotlin
@Test
fun `test loadData updates state`() = runTest {
    val repository = FakeRepository()
    val viewModel = MyViewModel(repository)

    viewModel.loadData()

    assertEquals(false, viewModel.uiState.value.isLoading)
    assertEquals(3, viewModel.uiState.value.data.size)
}
```

### Compose UI Test
```kotlin
@Test
fun testButtonClick() {
    composeTestRule.setContent {
        MyScreen(rememberNavController())
    }

    composeTestRule
        .onNodeWithText("Click Me")
        .performClick()

    composeTestRule
        .onNodeWithText("Clicked!")
        .assertExists()
}
```

## Common Extensions

### Context Extensions
```kotlin
fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.getColorCompat(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}
```

### Modifier Extensions
```kotlin
fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}
```

## Compose Previews

### Basic Preview
```kotlin
@Preview(showBackground = true)
@Composable
fun PreviewMyScreen() {
    DocumentViewerTheme {
        MyScreen(rememberNavController())
    }
}
```

### Multiple Previews
```kotlin
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewMyCard() {
    DocumentViewerTheme {
        MyCard()
    }
}
```

### Preview with Data
```kotlin
@Preview
@Composable
fun PreviewFileListItem() {
    DocumentViewerTheme {
        FileListItem(
            file = DocumentFile(
                uri = Uri.EMPTY,
                name = "Sample.pdf",
                path = "/storage/Sample.pdf",
                size = 1024000,
                lastModified = System.currentTimeMillis(),
                mimeType = "application/pdf",
                extension = "pdf",
                type = FileType.PDF
            ),
            onFileClick = {},
            onFavoriteClick = {},
            onShareClick = {},
            onDeleteClick = {}
        )
    }
}
```

---

Use these patterns as reference when implementing new features!
