package com.documentviewer.ui.home

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.documentviewer.ui.navigation.Screen
import com.documentviewer.ui.theme.*
import com.documentviewer.core.preferences.ThemePreferences
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val themePreferences: ThemePreferences = androidx.hilt.navigation.compose.hiltViewModel<HomeViewModel>().themePreferences
    val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    // Request all necessary permissions on startup
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val permissionState = rememberMultiplePermissionsState(permissions)
    var showAllFilesDialog by remember { mutableStateOf(false) }

    // Check if we have "All files access" permission for documents
    val hasAllFilesAccess = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            android.os.Environment.isExternalStorageManager()
        } else {
            true
        }
    }

    // Request permissions on first launch
    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    // Show dialog for "All files access" after media permissions granted
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted && !hasAllFilesAccess && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            showAllFilesDialog = true
        }
    }

    // Show permission dialog if not granted
    if (!permissionState.allPermissionsGranted) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Media Permissions Required") },
            text = {
                Text("This app needs permissions to access images, videos, and audio.")
            },
            confirmButton = {
                Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permissions")
                }
            }
        )
    }

    // Show dialog for "All files access" for documents
    if (showAllFilesDialog && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        AlertDialog(
            onDismissRequest = { showAllFilesDialog = false },
            title = { Text("Document Access Required") },
            text = {
                Text("To access PDF, Word, Excel and other documents, enable 'All files access' permission. Tap 'Open Settings' and select 'Allow access to manage all files'.")
            },
            confirmButton = {
                Button(onClick = {
                    showAllFilesDialog = false
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        android.net.Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAllFilesDialog = false }) {
                    Text("Skip")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Document Viewer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            themePreferences.setDarkTheme(!isDarkTheme)
                        }
                    }) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Team Info Card
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Document Management & PDF Toolkit",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Krishna (366) | Adarsh (166) | Mahesh (165)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Section Header: Categories
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionHeader("Categories", navController = navController, showSearch = true)
            }

            items(
                items = categoriesList,
                key = { it.type }
            ) { category ->
                CategoryCard(category) {
                    navController.navigate(Screen.FileList.createRoute(category.type))
                }
            }

            // Section Header: PDF Operations
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionHeader("PDF Operations")
            }

            items(
                items = pdfOperationsList,
                key = { it.title }
            ) { operation ->
                OperationCard(operation) {
                    navController.navigate(operation.route)
                }
            }

            // Section Header: Tools
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionHeader("Tools")
            }

            items(
                items = toolsList,
                key = { it.title }
            ) { tool ->
                ToolCard(tool) {
                    navController.navigate(tool.route)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, navController: NavController? = null, showSearch: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (showSearch && navController != null) {
            IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                Icon(
                    Icons.Default.Search,
                    "Search",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(category.color.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = category.color,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (category.count > 0) {
                Text(
                    text = "${category.count} files",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PlaceCard(place: PlaceItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = place.icon,
                contentDescription = place.title,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = place.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun OperationCard(operation: OperationItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = operation.icon,
                    contentDescription = operation.title,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = operation.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (tool.subtitle != null) {
                    Text(
                        text = tool.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Data classes for UI items
data class CategoryItem(
    val title: String,
    val type: String,
    val icon: ImageVector,
    val color: Color,
    val count: Int = 0
)

data class PlaceItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

data class OperationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

data class ToolItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val subtitle: String? = null
)

// Lists
val categoriesList = listOf(
    CategoryItem("All Documents", "ALL_DOCUMENTS", Icons.Default.Folder, Color(0xFF607D8B)),
    CategoryItem("PDF", "PDF", Icons.Default.PictureAsPdf, PdfColor),
    CategoryItem("Word", "WORD", Icons.Default.Description, WordColor),
    CategoryItem("Excel", "EXCEL", Icons.Default.TableChart, ExcelColor),
    CategoryItem("PowerPoint", "POWERPOINT", Icons.Default.Slideshow, PowerPointColor),
    CategoryItem("Images", "IMAGE", Icons.Default.Image, ImageColor)
)

val placesList = listOf(
    PlaceItem("Folders", Icons.Default.Folder, Screen.Folders.route),
    PlaceItem("Recent Files", Icons.Default.History, Screen.Recent.route),
    PlaceItem("Favorites", Icons.Default.Star, Screen.Favorites.route),
    PlaceItem("My Creation", Icons.Default.CreateNewFolder, Screen.MyCreation.route)
)

val pdfOperationsList = listOf(
    OperationItem("Merge PDF", Icons.Default.MergeType, Screen.MergePdf.route),
    OperationItem("Remove Password", Icons.Default.LockOpen, Screen.RemovePassword.route),
    OperationItem("Add Password", Icons.Default.Lock, Screen.AddPassword.route),
    OperationItem("Word to PDF", Icons.Default.Description, Screen.WordToPdf.route),
    OperationItem("Slide to PDF", Icons.Default.Slideshow, Screen.SlideToPdf.route)
)

val toolsList = listOf(
    ToolItem("ZIP Creator", Icons.Default.FolderZip, Screen.ZipCreator.route, "Compress files"),
    ToolItem("Notepad", Icons.Default.Note, Screen.Notepad.route, "Create and edit notes")
)
