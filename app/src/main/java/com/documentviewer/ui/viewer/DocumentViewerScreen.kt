package com.documentviewer.ui.viewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.positionChanged
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.math.abs
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.documentviewer.data.model.FileType
import com.documentviewer.core.utils.DocumentConverter
import com.documentviewer.core.utils.DocumentCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import java.io.File
import java.io.FileOutputStream

// Zoomable content composable with pinch-to-zoom and double-tap
@Composable
fun ZoomableContent(
    modifier: Modifier = Modifier,
    content: @Composable (scale: Float, offsetX: Float, offsetY: Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scale = remember { mutableStateOf(1f) }
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    Box(
        modifier = modifier.globalZoom(scale, offsetX, offsetY, scope)
    ) {
        content(scale.value, offsetX.value, offsetY.value)
    }
}

// Global zoom gesture modifier - apply to any container
fun Modifier.globalZoom(
    scale: MutableState<Float>,
    offsetX: MutableState<Float>,
    offsetY: MutableState<Float>,
    scope: CoroutineScope
): Modifier = this
    .pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = { tapOffset ->
                scope.launch {
                    if (scale.value > 1f) {
                        // Zoom out to 1x
                        val scaleAnim = Animatable(scale.value)
                        val offsetXAnim = Animatable(offsetX.value)
                        val offsetYAnim = Animatable(offsetY.value)

                        launch {
                            scaleAnim.animateTo(1f, animationSpec = tween(200))
                            scale.value = scaleAnim.value
                        }
                        launch {
                            offsetXAnim.animateTo(0f, animationSpec = tween(200))
                            offsetX.value = offsetXAnim.value
                        }
                        launch {
                            offsetYAnim.animateTo(0f, animationSpec = tween(200))
                            offsetY.value = offsetYAnim.value
                        }
                    } else {
                        // Zoom in to 2.5x at tap location
                        val targetScale = 2.5f
                        val targetOffsetX = (size.width / 2f - tapOffset.x) * (targetScale - 1f)
                        val targetOffsetY = (size.height / 2f - tapOffset.y) * (targetScale - 1f)

                        val scaleAnim = Animatable(scale.value)
                        val offsetXAnim = Animatable(offsetX.value)
                        val offsetYAnim = Animatable(offsetY.value)

                        launch {
                            scaleAnim.animateTo(targetScale, animationSpec = tween(200))
                            scale.value = scaleAnim.value
                        }
                        launch {
                            offsetXAnim.animateTo(targetOffsetX, animationSpec = tween(200))
                            offsetX.value = offsetXAnim.value
                        }
                        launch {
                            offsetYAnim.animateTo(targetOffsetY, animationSpec = tween(200))
                            offsetY.value = offsetYAnim.value
                        }
                    }
                }
            }
        )
    }
    .pointerInput(Unit) {
        detectTransformGestures { centroid, pan, zoom, rotation ->
            val newScale = (scale.value * zoom).coerceIn(1f, 5f)

            // Calculate the scale change
            val scaleChange = newScale / scale.value

            if (newScale > 1f) {
                // Update scale
                scale.value = newScale

                // Adjust pan based on current scale for smooth panning at any angle
                // The pan gesture already accounts for rotation, so we just scale it
                offsetX.value += pan.x
                offsetY.value += pan.y

                // Adjust offset for zoom center point (centroid)
                if (scaleChange != 1f) {
                    // Calculate offset adjustment to zoom towards the centroid
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val focusX = centroid.x - centerX
                    val focusY = centroid.y - centerY

                    offsetX.value += focusX * (scaleChange - 1f)
                    offsetY.value += focusY * (scaleChange - 1f)
                }
            } else {
                // Reset to no zoom
                scale.value = 1f
                offsetX.value = 0f
                offsetY.value = 0f
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentViewerScreen(
    navController: NavController,
    uri: String,
    fileType: String,
    fileName: String
) {
    val context = LocalContext.current
    val decodedUri = Uri.parse(java.net.URLDecoder.decode(uri, "UTF-8"))
    val type = try { FileType.valueOf(fileType) } catch (e: Exception) { FileType.UNKNOWN }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileName, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Share button
                    IconButton(onClick = {
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            setType(context.contentResolver.getType(decodedUri))
                            putExtra(android.content.Intent.EXTRA_STREAM, decodedUri)
                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share file"))
                    }) {
                        Icon(Icons.Default.Share, "Share")
                    }
                    
                    // Delete button
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Delete")
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
            when (type) {
                FileType.PDF -> PdfViewer(context, decodedUri)
                FileType.WORD -> WordDocumentViewer(context, decodedUri)
                FileType.EXCEL -> OfficeDocumentViewer(context, decodedUri, FileType.EXCEL)
                FileType.POWERPOINT -> OfficeDocumentViewer(context, decodedUri, FileType.POWERPOINT)
                FileType.IMAGE -> ImageViewer(decodedUri)
                else -> UnsupportedFileViewer(fileName, type)
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Delete, null) },
            title = { Text("Delete File?") },
            text = { Text("Are you sure you want to delete \"$fileName\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            context.contentResolver.delete(decodedUri, null, null)
                            android.widget.Toast.makeText(context, "File deleted", android.widget.Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "Failed to delete: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PdfViewer(context: Context, uri: Uri) {
    var pdfPages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var totalPages by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var loadingProgress by remember { mutableStateOf(0f) }
    var error by remember { mutableStateOf<String?>(null) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    
    // Get PDF position preferences
    val pdfPositionPreferences: com.documentviewer.core.preferences.PdfPositionPreferences = 
        androidx.hilt.navigation.compose.hiltViewModel<PdfViewerViewModel>().pdfPositionPreferences
    val lastPage by pdfPositionPreferences.getLastPage(uri.toString()).collectAsState(initial = 0)
    val scope = rememberCoroutineScope()
    var hasRestoredPosition by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Calculate current page from scroll position
    val currentPage = remember {
        derivedStateOf {
            if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
                listState.layoutInfo.visibleItemsInfo.first().index + 1
            } else {
                1
            }
        }
    }
    
    // Restore last page position after PDF is loaded
    LaunchedEffect(pdfPages.isNotEmpty(), lastPage) {
        if (pdfPages.isNotEmpty() && !hasRestoredPosition && lastPage > 0) {
            listState.scrollToItem(lastPage)
            hasRestoredPosition = true
        }
    }
    
    // Save current page when it changes
    LaunchedEffect(currentPage.value) {
        if (pdfPages.isNotEmpty() && hasRestoredPosition) {
            scope.launch {
                pdfPositionPreferences.saveLastPage(uri.toString(), listState.firstVisibleItemIndex)
            }
        }
    }

    fun loadPdf(pdfPassword: String? = null) {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            isLoading = true
            error = null
            loadingProgress = 0f
            try {
                val result = loadPdfPages(context, uri, pdfPassword) { progress ->
                    loadingProgress = progress
                }
                pdfPages = result.pages
                totalPages = result.totalPages
                isLoading = false
                showPasswordDialog = false
                password = ""
                passwordError = false
            } catch (e: SecurityException) {
                if (e.message?.contains("password", ignoreCase = true) == true ||
                    e.message?.contains("Invalid password", ignoreCase = true) == true) {
                    isLoading = false
                    showPasswordDialog = true
                    passwordError = pdfPassword != null  // Only show error if user already tried
                } else {
                    error = "Error loading PDF: ${e.message}"
                    isLoading = false
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: ""
                if (errorMsg.contains("password", ignoreCase = true) ||
                    errorMsg.contains("encrypted", ignoreCase = true) ||
                    errorMsg.contains("BadPasswordException", ignoreCase = true)) {
                    isLoading = false
                    showPasswordDialog = true
                    passwordError = pdfPassword != null  // Only show error if user already tried
                } else {
                    error = "Error loading PDF: $errorMsg"
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(uri) {
        loadPdf()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading PDF...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { loadingProgress },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(loadingProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            pdfPages.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No pages found in PDF")
                }
            }
            else -> {
                ZoomableContent(modifier = Modifier.fillMaxSize()) { scale, offsetX, offsetY ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offsetX,
                                    translationY = offsetY
                                ),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            userScrollEnabled = scale <= 1f  // Disable scroll when zoomed
                        ) {
                            items(pdfPages.size) { index ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = androidx.compose.ui.graphics.Color.White
                                    )
                                ) {
                                    Image(
                                        bitmap = pdfPages[index].asImageBitmap(),
                                        contentDescription = "PDF Page ${index + 1}",
                                        modifier = Modifier.fillMaxWidth(),
                                        contentScale = ContentScale.FillWidth
                                    )
                                }
                            }
                        }

                        // Page indicator overlay
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "${currentPage.value} / $totalPages",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }

    // Password dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordDialog = false
                password = ""
                passwordError = false
            },
            icon = {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                    contentDescription = null
                )
            },
            title = { Text("Password Protected PDF") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This PDF is password protected. Please enter the password to view it.")

                    if (passwordError) {
                        Text(
                            "Incorrect password. Please try again.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    var showPasswordText by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = false  // Clear error when user types
                        },
                        label = { Text("Password") },
                        placeholder = { Text("Enter password") },
                        visualTransformation = if (showPasswordText)
                            androidx.compose.ui.text.input.VisualTransformation.None
                        else
                            androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                        ),
                        trailingIcon = {
                            IconButton(onClick = { showPasswordText = !showPasswordText }) {
                                Icon(
                                    if (showPasswordText)
                                        androidx.compose.material.icons.Icons.Default.VisibilityOff
                                    else
                                        androidx.compose.material.icons.Icons.Default.Visibility,
                                    contentDescription = if (showPasswordText) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = passwordError
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (password.isNotEmpty()) {
                            loadPdf(password)
                        }
                    },
                    enabled = password.isNotEmpty()
                ) {
                    Text("Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasswordDialog = false
                    password = ""
                    passwordError = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WordDocumentViewer(context: Context, uri: Uri) {
    var htmlContent by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uri) {
        isLoading = true
        error = null
        try {
            withContext(Dispatchers.IO) {
                // Use URI hash for caching
                val cacheKey = uri.toString().hashCode().toLong()
                
                // Try to get from cache first
                htmlContent = DocumentCache.getCachedHtml(context, uri, cacheKey)
                
                if (htmlContent == null) {
                    // Not in cache, convert and cache it
                    htmlContent = DocumentConverter.convertDocxToHtml(context, uri)
                    htmlContent?.let { DocumentCache.cacheHtml(context, uri, cacheKey, it) }
                }
            }
            isLoading = false
        } catch (e: Exception) {
            error = "Error loading document: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, context.contentResolver.getType(uri))
                        flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "No app available to open this file", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                icon = { Icon(Icons.Default.OpenInNew, "Open Externally") },
                text = { Text("Open in App") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading document...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                htmlContent != null -> {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                webViewClient = WebViewClient()
                                settings.apply {
                                    javaScriptEnabled = false
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    setSupportZoom(true)
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                }
                                isVerticalScrollBarEnabled = true
                                isHorizontalScrollBarEnabled = true
                            }
                        },
                        update = { webView ->
                            htmlContent?.let { html ->
                                webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleDocsViewer(uri: Uri) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var publicUrl by remember { mutableStateOf<String?>(null) }

    // Copy file to public cache and get shareable URL
    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            try {
                // Copy file to cache directory with a public name
                val fileName = uri.lastPathSegment ?: "document"
                val cacheFile = File(context.cacheDir, "viewer_$fileName")
                
                context.contentResolver.openInputStream(uri)?.use { input ->
                    cacheFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Use Microsoft Office Online Viewer with file content
                // For now, we'll use a simpler WebView approach with the file directly
                publicUrl = uri.toString()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Error preparing document: ${e.message}"
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (publicUrl != null) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                errorCode: Int,
                                description: String?,
                                failingUrl: String?
                            ) {
                                super.onReceivedError(view, errorCode, description, failingUrl)
                                error = "Cannot display document. Please use an external app to view this file."
                                isLoading = false
                            }
                        }
                        settings.apply {
                            javaScriptEnabled = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            setSupportZoom(true)
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            allowFileAccess = true
                            allowContentAccess = true
                        }
                    }
                },
                update = { webView ->
                    // Try to load the file directly
                    publicUrl?.let { webView.loadUrl(it) }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading document...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (error != null) {
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
                        error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        // Open in external app
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, context.contentResolver.getType(uri))
                            flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // No app available
                        }
                    }) {
                        Text("Open in External App")
                    }
                }
            }
        }
    }
}

@Composable
fun ImageViewer(uri: Uri) {
    ZoomableContent(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black)
    ) { scale, offsetX, offsetY ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun UnsupportedFileViewer(fileName: String, fileType: FileType) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Preview not available",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "File type: ${fileType.name}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = fileName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "This file type cannot be previewed in the app yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OfficeDocumentViewer(context: Context, uri: Uri, fileType: FileType) {
    var documentPages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var totalPages by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var loadingProgress by remember { mutableStateOf(0f) }
    var error by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    val currentPage = remember {
        derivedStateOf {
            if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
                listState.layoutInfo.visibleItemsInfo.first().index + 1
            } else {
                1
            }
        }
    }

    LaunchedEffect(uri) {
        isLoading = true
        error = null
        loadingProgress = 0f
        try {
            withContext(Dispatchers.IO) {
                // Use URI hash for caching
                val cacheKey = uri.toString().hashCode().toLong()
                
                // Try to get from cache first
                val cachedPages = DocumentCache.getCachedBitmaps(context, uri, cacheKey)
                
                if (cachedPages != null) {
                    documentPages = cachedPages
                    totalPages = cachedPages.size
                } else {
                    // Not in cache, render and cache it
                    val result = loadOfficeDocument(context, uri, fileType) { progress ->
                        loadingProgress = progress
                    }
                    documentPages = result.pages
                    totalPages = result.totalPages
                    
                    // Cache the rendered pages
                    DocumentCache.cacheBitmaps(context, uri, cacheKey, result.pages)
                }
            }
            isLoading = false
        } catch (e: Exception) {
            error = "Error loading document: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, context.contentResolver.getType(uri))
                        flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "No app available to open this file", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                icon = { Icon(Icons.Default.OpenInNew, "Open Externally") },
                text = { Text("Open in App") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading ${fileType.name}...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { loadingProgress },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${(loadingProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                documentPages.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No pages found in document")
                    }
                }
                else -> {
                    ZoomableContent(modifier = Modifier.fillMaxSize()) { scale, offsetX, offsetY ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(
                                        scaleX = scale,
                                        scaleY = scale,
                                        translationX = offsetX,
                                        translationY = offsetY
                                    ),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                userScrollEnabled = scale <= 1f
                            ) {
                                items(documentPages.size) { index ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = androidx.compose.ui.graphics.Color.White
                                        )
                                    ) {
                                        Image(
                                            bitmap = documentPages[index].asImageBitmap(),
                                            contentDescription = "${fileType.name} Page ${index + 1}",
                                            modifier = Modifier.fillMaxWidth(),
                                            contentScale = ContentScale.FillWidth
                                        )
                                    }
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp),
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "${currentPage.value} / $totalPages",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class PdfLoadResult(val pages: List<Bitmap>, val totalPages: Int)

private suspend fun loadPdfPages(
    context: Context,
    uri: Uri,
    password: String? = null,
    onProgress: (Float) -> Unit
): PdfLoadResult = withContext(Dispatchers.IO) {
    val pages = mutableListOf<Bitmap>()
    var totalPages = 0

    try {
        // If password is provided, decrypt the PDF first using iText
        val fileDescriptorToUse = if (password != null) {
            // Create a temporary file for the decrypted PDF
            val tempFile = File(context.cacheDir, "temp_decrypted_${System.currentTimeMillis()}.pdf")

            try {
                // Read the encrypted PDF with iText
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val pdfReader = com.itextpdf.kernel.pdf.PdfReader(inputStream)
                        .setUnethicalReading(true)

                    // Set password
                    val readerProperties = com.itextpdf.kernel.pdf.ReaderProperties()
                        .setPassword(password.toByteArray())

                    // Re-create reader with password
                    context.contentResolver.openInputStream(uri)?.use { passwordInputStream ->
                        val passwordReader = com.itextpdf.kernel.pdf.PdfReader(passwordInputStream, readerProperties)
                        val pdfDoc = com.itextpdf.kernel.pdf.PdfDocument(passwordReader)

                        // Create output PDF without password
                        val pdfWriter = com.itextpdf.kernel.pdf.PdfWriter(tempFile)
                        val outputDoc = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)

                        // Copy all pages
                        pdfDoc.copyPagesTo(1, pdfDoc.numberOfPages, outputDoc)

                        outputDoc.close()
                        pdfDoc.close()
                    }
                }

                // Open the decrypted file
                android.os.ParcelFileDescriptor.open(tempFile, android.os.ParcelFileDescriptor.MODE_READ_ONLY)
            } catch (e: com.itextpdf.kernel.exceptions.BadPasswordException) {
                tempFile.delete()
                throw SecurityException("Invalid password")
            } catch (e: Exception) {
                tempFile.delete()
                throw e
            }
        } else {
            // No password, open file directly
            context.contentResolver.openFileDescriptor(uri, "r")
        }

        fileDescriptorToUse?.use { fileDescriptor ->
            PdfRenderer(fileDescriptor).use { pdfRenderer ->
                totalPages = pdfRenderer.pageCount
                // Limit to first 100 pages to avoid memory issues
                val maxPages = minOf(totalPages, 100)

                for (i in 0 until maxPages) {
                    pdfRenderer.openPage(i).use { page ->
                        // Better scale for quality
                        val scale = 2.5f
                        val width = (page.width * scale).toInt()
                        val height = (page.height * scale).toInt()

                        // Create bitmap with white background
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        // Fill with white background to fix black overlay issue
                        bitmap.eraseColor(Color.WHITE)

                        page.render(
                            bitmap,
                            null,
                            null,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                        )
                        pages.add(bitmap)

                        // Update progress
                        onProgress((i + 1).toFloat() / maxPages)
                    }
                }
            }
        }

        // Clean up temporary file if it was created
        if (password != null) {
            val tempFile = File(context.cacheDir, "temp_decrypted_${System.currentTimeMillis()}.pdf")
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    } catch (e: Exception) {
        throw e
    }

    PdfLoadResult(pages, totalPages)
}

private suspend fun loadOfficeDocument(
    context: Context,
    uri: Uri,
    fileType: FileType,
    onProgress: (Float) -> Unit
): PdfLoadResult = withContext(Dispatchers.IO) {
    val pages = mutableListOf<Bitmap>()
    var totalPages = 0

    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            when (fileType) {
                FileType.WORD -> {
                    pages.addAll(renderWordDocument(inputStream, onProgress))
                }
                FileType.EXCEL -> {
                    pages.addAll(renderExcelDocument(inputStream, onProgress))
                }
                FileType.POWERPOINT -> {
                    pages.addAll(renderPowerPointDocument(inputStream, onProgress))
                }
                else -> throw IllegalArgumentException("Unsupported file type: $fileType")
            }
            totalPages = pages.size
        }
    } catch (e: Exception) {
        throw e
    }

    PdfLoadResult(pages, totalPages)
}

private fun renderWordDocument(inputStream: java.io.InputStream, onProgress: (Float) -> Unit): List<Bitmap> {
    val pages = mutableListOf<Bitmap>()

    try {
        val document = org.apache.poi.xwpf.usermodel.XWPFDocument(inputStream)

        // A4 dimensions at 300 DPI for high quality
        val width = 2100
        val height = 2970
        val topMargin = 200f
        val bottomMargin = 200f
        val leftMargin = 180f
        val rightMargin = 180f
        val contentWidth = width - leftMargin - rightMargin

        var currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var canvas = android.graphics.Canvas(currentBitmap)
        canvas.drawColor(Color.WHITE)

        var yOffset = topMargin

        // Process each paragraph with full formatting
        document.paragraphs.forEachIndexed { paraIndex, paragraph ->
            val alignment = when (paragraph.alignment?.name) {
                "CENTER" -> android.graphics.Paint.Align.CENTER
                "RIGHT" -> android.graphics.Paint.Align.RIGHT
                "BOTH" -> android.graphics.Paint.Align.LEFT  // Justified -> Left for simplicity
                else -> android.graphics.Paint.Align.LEFT
            }

            // Handle empty paragraphs (blank lines)
            if (paragraph.runs.isEmpty() || paragraph.text.isEmpty()) {
                yOffset += 44f  // Default line height for empty paragraph

                // Check if we need a new page
                if (yOffset > height - bottomMargin) {
                    pages.add(currentBitmap.copy(Bitmap.Config.ARGB_8888, false))
                    currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    canvas = android.graphics.Canvas(currentBitmap)
                    canvas.drawColor(Color.WHITE)
                    yOffset = topMargin
                }
            } else {
                var paragraphMaxFontSize = 44f
                var paragraphHasContent = false

                // Process each run (formatted text segment) in the paragraph
                paragraph.runs.forEach { run ->
                    val text = run.text() ?: ""

                    // Handle explicit line breaks in text
                    val lines = text.split("\n")

                    lines.forEachIndexed { lineIndex, lineText ->
                        if (lineText.isNotEmpty()) {
                            paragraphHasContent = true

                            val paint = android.graphics.Paint().apply {
                                isAntiAlias = true
                                isSubpixelText = true

                                // Get font size (default 11pt = ~44px at 300dpi)
                                @Suppress("DEPRECATION")
                                val fontSizeInPoints = run.fontSize
                                val fontSize = if (fontSizeInPoints > 0) fontSizeInPoints.toFloat() * 4f else 44f
                                textSize = fontSize
                                paragraphMaxFontSize = maxOf(paragraphMaxFontSize, fontSize)

                                // Apply text color
                                val colorHex = run.color
                                color = if (colorHex != null && colorHex != "auto") {
                                    try {
                                        android.graphics.Color.parseColor("#$colorHex")
                                    } catch (e: Exception) {
                                        Color.BLACK
                                    }
                                } else {
                                    Color.BLACK
                                }

                                // Apply bold
                                isFakeBoldText = run.isBold

                                // Apply italic
                                textSkewX = if (run.isItalic) -0.25f else 0f

                                // Apply underline
                                if (run.underline?.name != "NONE") {
                                    flags = flags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                                }

                                // Apply strikethrough
                                if (run.isStrikeThrough) {
                                    flags = flags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                                }

                                textAlign = alignment
                            }

                            // Calculate line height (1.15 * font size is standard)
                            val lineHeight = paint.textSize * 1.15f

                            // Word wrap with formatting preserved
                            val words = lineText.split(" ")
                            var line = ""

                            words.forEach { word ->
                                val testLine = if (line.isEmpty()) word else "$line $word"
                                val textWidth = paint.measureText(testLine)

                                if (textWidth > contentWidth && line.isNotEmpty()) {
                                    // Draw the current line
                                    val xPos = when (alignment) {
                                        android.graphics.Paint.Align.CENTER -> width / 2f
                                        android.graphics.Paint.Align.RIGHT -> width - rightMargin
                                        else -> leftMargin
                                    }
                                    canvas.drawText(line, xPos, yOffset, paint)
                                    line = word
                                    yOffset += lineHeight

                                    // Create new page if needed
                                    if (yOffset > height - bottomMargin) {
                                        pages.add(currentBitmap.copy(Bitmap.Config.ARGB_8888, false))
                                        currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                                        canvas = android.graphics.Canvas(currentBitmap)
                                        canvas.drawColor(Color.WHITE)
                                        yOffset = topMargin
                                    }
                                } else {
                                    line = testLine
                                }
                            }

                            // Draw remaining line
                            if (line.isNotEmpty()) {
                                val xPos = when (alignment) {
                                    android.graphics.Paint.Align.CENTER -> width / 2f
                                    android.graphics.Paint.Align.RIGHT -> width - rightMargin
                                    else -> leftMargin
                                }
                                canvas.drawText(line, xPos, yOffset, paint)
                                yOffset += lineHeight

                                // Create new page if needed
                                if (yOffset > height - bottomMargin) {
                                    pages.add(currentBitmap.copy(Bitmap.Config.ARGB_8888, false))
                                    currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                                    canvas = android.graphics.Canvas(currentBitmap)
                                    canvas.drawColor(Color.WHITE)
                                    yOffset = topMargin
                                }
                            }
                        }

                        // Add line break spacing for explicit newlines (except last line)
                        if (lineIndex < lines.size - 1) {
                            yOffset += paragraphMaxFontSize * 0.3f
                        }
                    }
                }

                // Add paragraph spacing (slightly larger than line spacing)
                if (paragraphHasContent) {
                    yOffset += paragraphMaxFontSize * 0.5f
                }

                // Check if we need a new page after paragraph
                if (yOffset > height - bottomMargin) {
                    pages.add(currentBitmap.copy(Bitmap.Config.ARGB_8888, false))
                    currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    canvas = android.graphics.Canvas(currentBitmap)
                    canvas.drawColor(Color.WHITE)
                    yOffset = topMargin
                }
            }

            onProgress((paraIndex + 1).toFloat() / document.paragraphs.size)
        }

        // Add the last page if it has content
        if (yOffset > topMargin) {
            pages.add(currentBitmap)
        }
        document.close()
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback: create error page
        val errorBitmap = Bitmap.createBitmap(2100, 2970, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(errorBitmap)
        canvas.drawColor(Color.WHITE)
        val paint = android.graphics.Paint().apply {
            color = Color.RED
            textSize = 50f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Error rendering Word document", 1050f, 1400f, paint)
        paint.textSize = 35f
        canvas.drawText(e.message ?: "Unknown error", 1050f, 1500f, paint)
        pages.add(errorBitmap)
    }

    return pages
}

private fun renderExcelDocument(inputStream: java.io.InputStream, onProgress: (Float) -> Unit): List<Bitmap> {
    val pages = mutableListOf<Bitmap>()

    try {
        val workbook = org.apache.poi.xssf.usermodel.XSSFWorkbook(inputStream)

        // Render each sheet as a separate page (or multiple pages if needed)
        workbook.forEachIndexed { sheetIndex, sheet ->
            val width = 2970  // Landscape A4 width at 300 DPI
            val height = 2100 // Landscape A4 height at 300 DPI

            val margin = 60f
            val titleHeight = 90f
            val minCellWidth = 120f
            val maxCellWidth = 400f
            val cellHeight = 65f
            val cellPadding = 12f

            // Calculate column widths based on content
            val numColumns = minOf(sheet.getRow(0)?.lastCellNum?.toInt() ?: 0, 12)
            val columnWidths = FloatArray(numColumns) { minCellWidth }

            // Sample first 20 rows to determine optimal column widths
            val sampleRows = minOf(sheet.lastRowNum + 1, 20)
            for (rowIndex in 0 until sampleRows) {
                val row = sheet.getRow(rowIndex) ?: continue
                for (colIndex in 0 until numColumns) {
                    val cell = row.getCell(colIndex)
                    val cellValue = getCellValueAsString(cell)
                    if (cellValue.isNotEmpty()) {
                        val paint = android.graphics.Paint().apply {
                            textSize = 30f
                            isAntiAlias = true
                        }
                        val textWidth = paint.measureText(cellValue) + (2 * cellPadding)
                        columnWidths[colIndex] = maxOf(columnWidths[colIndex], minOf(textWidth, maxCellWidth))
                    }
                }
            }

            // Adjust column widths to fit page width
            val totalWidth = columnWidths.sum()
            val availableWidth = width - (2 * margin)
            if (totalWidth > availableWidth) {
                val scale = availableWidth / totalWidth
                for (i in columnWidths.indices) {
                    columnWidths[i] *= scale
                }
            }

            var currentPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            var currentCanvas = android.graphics.Canvas(currentPageBitmap)
            currentCanvas.drawColor(Color.WHITE)

            // Draw sheet title
            val titlePaint = android.graphics.Paint().apply {
                color = Color.BLACK
                textSize = 48f
                isFakeBoldText = true
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            currentCanvas.drawText(sheet.sheetName ?: "Sheet ${sheetIndex + 1}", width / 2f, margin + 45f, titlePaint)

            var yOffset = margin + titleHeight

            // Render rows with proper formatting
            val maxRows = minOf(sheet.lastRowNum + 1, 100)
            for (rowIndex in 0 until maxRows) {
                val row = sheet.getRow(rowIndex)

                // Check if we need a new page
                if (yOffset + cellHeight > height - margin) {
                    pages.add(currentPageBitmap.copy(Bitmap.Config.ARGB_8888, false))
                    currentPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    currentCanvas = android.graphics.Canvas(currentPageBitmap)
                    currentCanvas.drawColor(Color.WHITE)
                    yOffset = margin
                }

                var xOffset = margin

                for (cellIndex in 0 until numColumns) {
                    val cellWidth = columnWidths[cellIndex]
                    val cell = row?.getCell(cellIndex)

                    // Get cell style for background color
                    val cellStyle = cell?.cellStyle
                    val bgColor = cellStyle?.fillForegroundColorColor?.let {
                        if (it is org.apache.poi.xssf.usermodel.XSSFColor) {
                            val rgb = it.rgb
                            if (rgb != null && rgb.size >= 3) {
                                android.graphics.Color.rgb(
                                    rgb[0].toInt() and 0xFF,
                                    rgb[1].toInt() and 0xFF,
                                    rgb[2].toInt() and 0xFF
                                )
                            } else Color.WHITE
                        } else Color.WHITE
                    } ?: Color.WHITE

                    // Draw cell background
                    val bgPaint = android.graphics.Paint().apply {
                        color = bgColor
                        style = android.graphics.Paint.Style.FILL
                    }
                    currentCanvas.drawRect(xOffset, yOffset, xOffset + cellWidth, yOffset + cellHeight, bgPaint)

                    // Draw cell border
                    val borderPaint = android.graphics.Paint().apply {
                        color = Color.GRAY
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = 2f
                        isAntiAlias = true
                    }
                    currentCanvas.drawRect(xOffset, yOffset, xOffset + cellWidth, yOffset + cellHeight, borderPaint)

                    // Get cell value
                    val cellValue = getCellValueAsString(cell)

                    // Draw cell text with formatting
                    if (cellValue.isNotEmpty()) {
                        val font = cellStyle?.let { workbook.getFontAt(it.fontIndex) }

                        val textPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            isSubpixelText = true
                            textSize = (font?.fontHeightInPoints?.toFloat() ?: 10f) * 2.8f

                            // Apply font color
                            color = if (font is org.apache.poi.xssf.usermodel.XSSFFont) {
                                font.xssfColor?.rgb?.let { rgb ->
                                    if (rgb.size >= 3) {
                                        android.graphics.Color.rgb(
                                            rgb[0].toInt() and 0xFF,
                                            rgb[1].toInt() and 0xFF,
                                            rgb[2].toInt() and 0xFF
                                        )
                                    } else Color.BLACK
                                } ?: Color.BLACK
                            } else Color.BLACK

                            // Apply bold
                            isFakeBoldText = font?.bold ?: false

                            // Apply italic
                            textSkewX = if (font?.italic == true) -0.25f else 0f

                            // Apply alignment
                            textAlign = when (cellStyle?.alignment?.name) {
                                "CENTER" -> android.graphics.Paint.Align.CENTER
                                "RIGHT" -> android.graphics.Paint.Align.RIGHT
                                else -> android.graphics.Paint.Align.LEFT
                            }
                        }

                        // Word wrap text within cell
                        val contentWidth = cellWidth - (2 * cellPadding)
                        val words = cellValue.split(" ")
                        var line = ""
                        var lineY = yOffset + (cellHeight / 2f) + (textPaint.textSize / 3f)

                        for (word in words) {
                            val testLine = if (line.isEmpty()) word else "$line $word"
                            val textWidth = textPaint.measureText(testLine)

                            if (textWidth > contentWidth && line.isNotEmpty()) {
                                // Draw current line
                                val textX = when (textPaint.textAlign) {
                                    android.graphics.Paint.Align.CENTER -> xOffset + cellWidth / 2f
                                    android.graphics.Paint.Align.RIGHT -> xOffset + cellWidth - cellPadding
                                    else -> xOffset + cellPadding
                                }
                                currentCanvas.drawText(line, textX, lineY, textPaint)
                                line = word
                                lineY += textPaint.textSize * 0.9f
                            } else {
                                line = testLine
                            }
                        }

                        // Draw remaining text
                        if (line.isNotEmpty() && lineY <= yOffset + cellHeight - cellPadding) {
                            val textX = when (textPaint.textAlign) {
                                android.graphics.Paint.Align.CENTER -> xOffset + cellWidth / 2f
                                android.graphics.Paint.Align.RIGHT -> xOffset + cellWidth - cellPadding
                                else -> xOffset + cellPadding
                            }
                            currentCanvas.drawText(line, textX, lineY, textPaint)
                        }
                    }

                    xOffset += cellWidth
                }

                yOffset += cellHeight
            }

            pages.add(currentPageBitmap)
            onProgress((sheetIndex + 1).toFloat() / workbook.numberOfSheets)
        }

        workbook.close()
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback: create error page
        val errorBitmap = Bitmap.createBitmap(2970, 2100, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(errorBitmap)
        canvas.drawColor(Color.WHITE)
        val paint = android.graphics.Paint().apply {
            color = Color.RED
            textSize = 50f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Error rendering Excel document", 1485f, 1000f, paint)
        paint.textSize = 35f
        canvas.drawText(e.message ?: "Unknown error", 1485f, 1100f, paint)
        pages.add(errorBitmap)
    }

    return pages
}

private fun getCellValueAsString(cell: org.apache.poi.ss.usermodel.Cell?): String {
    return when {
        cell == null -> ""
        cell.cellType == org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
            if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(cell.dateCellValue)
            } else {
                val value = cell.numericCellValue
                if (value % 1.0 == 0.0) value.toLong().toString()
                else String.format(java.util.Locale.US, "%.2f", value)
            }
        }
        cell.cellType == org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
        cell.cellType == org.apache.poi.ss.usermodel.CellType.FORMULA -> {
            try {
                when (cell.cachedFormulaResultType) {
                    org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                        val value = cell.numericCellValue
                        if (value % 1.0 == 0.0) value.toLong().toString()
                        else String.format(java.util.Locale.US, "%.2f", value)
                    }
                    org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                    else -> cell.toString()
                }
            } catch (e: Exception) {
                cell.toString()
            }
        }
        cell.cellType == org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
        else -> ""
    }
}

private fun renderPowerPointDocument(inputStream: java.io.InputStream, onProgress: (Float) -> Unit): List<Bitmap> {
    val pages = mutableListOf<Bitmap>()

    try {
        val presentation = org.apache.poi.xslf.usermodel.XMLSlideShow(inputStream)
        val slides = presentation.slides

        // Get slide dimensions (standard PowerPoint dimensions)
        val slideWidth = 720.0 // 10 inches at 72 DPI
        val slideHeight = 540.0 // 7.5 inches at 72 DPI (4:3 ratio)

        // Scale factor for rendering (higher = better quality)
        val scale = 4f
        val width = (slideWidth * scale).toInt()
        val height = (slideHeight * scale).toInt()

        // Render each slide as a page
        slides.forEachIndexed { index, slide ->
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)

            // Draw white background as default
            canvas.drawColor(Color.WHITE)

            // Render all shapes in the slide with sequential positioning
            var yOffsetAuto = 120f  // Automatic vertical positioning
            val marginLeft = 120f
            val marginRight = 120f
            val contentWidthAuto = width - marginLeft - marginRight

            slide.shapes.forEach { shape ->
                if (shape is org.apache.poi.xslf.usermodel.XSLFTextShape) {
                    // Use simple sequential layout - avoid AWT anchor completely
                    val shapeX = marginLeft
                    val shapeY = yOffsetAuto
                    val shapeWidth = contentWidthAuto

                    val insetMargin = 20f
                    val contentWidth = shapeWidth - (2 * insetMargin)
                    var yPos = shapeY

                    // Render text paragraphs with proper formatting
                    shape.textParagraphs.forEach { paragraph ->
                        val alignment = when (paragraph.textAlign?.name) {
                            "CENTER" -> android.graphics.Paint.Align.CENTER
                            "RIGHT" -> android.graphics.Paint.Align.RIGHT
                            else -> android.graphics.Paint.Align.LEFT
                        }

                        // Process each text run in the paragraph
                        paragraph.textRuns.forEach { run ->
                            val text = run.rawText ?: ""
                            if (text.isNotEmpty()) {
                                val paint = android.graphics.Paint().apply {
                                    isAntiAlias = true
                                    isSubpixelText = true

                                    // Get font size
                                    val fontSize = run.fontSize?.toFloat() ?: 18f
                                    textSize = fontSize * 4f

                                    // Get text color - simplified to avoid AWT
                                    color = Color.BLACK

                                    // Apply bold
                                    isFakeBoldText = run.isBold

                                    // Apply italic
                                    textSkewX = if (run.isItalic) -0.25f else 0f

                                    // Apply underline
                                    if (run.isUnderlined) {
                                        flags = flags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                                    }

                                    // Apply strikethrough
                                    if (run.isStrikethrough) {
                                        flags = flags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                                    }

                                    textAlign = alignment
                                }

                                // Word wrap text within shape bounds
                                val words = text.split(" ")
                                var line = ""

                                words.forEach { word ->
                                    val testLine = if (line.isEmpty()) word else "$line $word"
                                    val textWidth = paint.measureText(testLine)

                                    if (textWidth > contentWidth && line.isNotEmpty()) {
                                        // Draw the current line
                                        val xPos = when (alignment) {
                                            android.graphics.Paint.Align.CENTER -> shapeX + shapeWidth / 2f
                                            android.graphics.Paint.Align.RIGHT -> shapeX + shapeWidth - insetMargin
                                            else -> shapeX + insetMargin
                                        }

                                        // Check if still within reasonable bounds
                                        if (yPos < height - 100f) {
                                            canvas.drawText(line, xPos, yPos, paint)
                                            yPos += paint.textSize * 1.2f
                                        }
                                        line = word
                                    } else {
                                        line = testLine
                                    }
                                }

                                // Draw remaining line
                                if (line.isNotEmpty() && yPos < height - 100f) {
                                    val xPos = when (alignment) {
                                        android.graphics.Paint.Align.CENTER -> shapeX + shapeWidth / 2f
                                        android.graphics.Paint.Align.RIGHT -> shapeX + shapeWidth - insetMargin
                                        else -> shapeX + insetMargin
                                    }
                                    canvas.drawText(line, xPos, yPos, paint)
                                    yPos += paint.textSize * 1.2f
                                }
                            }
                        }

                        // Add paragraph spacing
                        yPos += 30f
                    }

                    yOffsetAuto = yPos + 50f
                }
            }

            pages.add(bitmap)
            onProgress((index + 1).toFloat() / slides.size)
        }

        presentation.close()
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback: create error page
        val errorBitmap = Bitmap.createBitmap(2970, 1670, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(errorBitmap)
        canvas.drawColor(Color.WHITE)
        val paint = android.graphics.Paint().apply {
            color = Color.RED
            textSize = 50f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Error rendering PowerPoint document", 1485f, 835f, paint)
        paint.textSize = 35f
        canvas.drawText(e.message ?: "Unknown error", 1485f, 935f, paint)
        pages.add(errorBitmap)
    }

    return pages
}
