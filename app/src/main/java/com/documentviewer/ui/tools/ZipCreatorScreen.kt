package com.documentviewer.ui.tools

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZipCreatorScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var zipFileName by remember { mutableStateOf("") }
    var createdZipFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var showNameDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        selectedFiles = uris
    }

    // Load existing ZIP files
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val zipFiles = downloadsDir.listFiles { file ->
                file.extension == "zip" && file.name.startsWith("DocViewer_")
            }?.sortedByDescending { it.lastModified() } ?: emptyList()
            withContext(Dispatchers.Main) {
                createdZipFiles = zipFiles
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ZIP Creator") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
            // Created ZIP files section
            if (createdZipFiles.isNotEmpty()) {
                Text(
                    "Created ZIP Files",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(createdZipFiles) { file ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FolderZip, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(file.name, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "${file.length() / 1024} KB • ${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(file.lastModified())}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // File selection section
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedFiles.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.FolderZip,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Select files to compress")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Select Files")
                            }
                        }
                    }
                } else {
                    Text(
                        "Selected Files (${selectedFiles.size})",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedFiles) { uri ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.InsertDriveFile, null)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = uri.lastPathSegment?.substringAfterLast('/') ?: "Unknown",
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )
                                    IconButton(onClick = { selectedFiles = selectedFiles - uri }) {
                                        Icon(Icons.Default.Close, "Remove")
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showNameDialog = true },
                        enabled = !isProcessing,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Create ZIP Archive")
                        }
                    }
                }
            }
        }
    }

    // Name input dialog
    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Name your ZIP file") },
            text = {
                OutlinedTextField(
                    value = zipFileName,
                    onValueChange = { zipFileName = it },
                    label = { Text("File name") },
                    placeholder = { Text("my_archive") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showNameDialog = false
                    if (zipFileName.isBlank()) {
                        zipFileName = "archive_${System.currentTimeMillis()}"
                    }
                    isProcessing = true
                    scope.launch(Dispatchers.IO) {
                        try {
                            val fileName = if (zipFileName.endsWith(".zip")) zipFileName else "$zipFileName.zip"
                            val finalFileName = "DocViewer_$fileName"
                            
                            // Create temp directory
                            val tempDir = File(context.cacheDir, "zip_temp_${System.currentTimeMillis()}")
                            tempDir.mkdirs()
                            
                            // Copy files
                            selectedFiles.forEach { uri ->
                                context.contentResolver.openInputStream(uri)?.use { input ->
                                    val name = uri.lastPathSegment?.substringAfterLast('/') ?: "file_${System.currentTimeMillis()}"
                                    File(tempDir, name).outputStream().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                            }
                            
                            // Create ZIP in Downloads
                            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            val zipFile = File(downloadsDir, finalFileName)
                            
                            val zipFileObj = ZipFile(zipFile)
                            tempDir.listFiles()?.forEach { file ->
                                zipFileObj.addFile(file)
                            }
                            
                            // Clean up
                            tempDir.deleteRecursively()
                            
                            // Scan file to make it visible
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val values = ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
                                    put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
                                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                                }
                                context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                            }
                            
                            // Reload ZIP list
                            val zipFiles = downloadsDir.listFiles { file ->
                                file.extension == "zip" && file.name.startsWith("DocViewer_")
                            }?.sortedByDescending { it.lastModified() } ?: emptyList()
                            
                            withContext(Dispatchers.Main) {
                                createdZipFiles = zipFiles
                                showSuccessDialog = true
                                selectedFiles = emptyList()
                                zipFileName = ""
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                errorMessage = "Error: ${e.message}"
                            }
                        } finally {
                            withContext(Dispatchers.Main) {
                                isProcessing = false
                            }
                        }
                    }
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showNameDialog = false
                    zipFileName = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Success") },
            text = { Text("ZIP file created in Downloads folder!") },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
    
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(errorMessage ?: "Unknown error") },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}
