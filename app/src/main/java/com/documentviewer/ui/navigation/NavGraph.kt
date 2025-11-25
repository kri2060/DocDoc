package com.documentviewer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.documentviewer.ui.home.HomeScreen
import com.documentviewer.ui.filelist.FileListScreen
import com.documentviewer.ui.viewer.DocumentViewerScreen
import com.documentviewer.ui.tools.MergePdfScreen
import com.documentviewer.ui.tools.AddPasswordScreen
import com.documentviewer.ui.tools.RemovePasswordScreen
import com.documentviewer.ui.tools.WordToPdfScreen
import com.documentviewer.ui.tools.SlideToPdfScreen
import com.documentviewer.ui.tools.ZipCreatorScreen
import com.documentviewer.ui.tools.OcrScannerScreen
import com.documentviewer.ui.tools.NotepadScreen
import com.documentviewer.ui.tools.NoteEditorScreen
import com.documentviewer.ui.search.SearchScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FileList : Screen("file_list/{type}") {
        fun createRoute(type: String) = "file_list/$type"
    }
    object Folders : Screen("folders")
    object Recent : Screen("recent")
    object Favorites : Screen("favorites")
    object MyCreation : Screen("my_creation")
    object ImagesToPdf : Screen("images_to_pdf")
    object PdfToImages : Screen("pdf_to_images")
    object MergePdf : Screen("merge_pdf")
    object RemovePassword : Screen("remove_password")
    object AddPassword : Screen("add_password")
    object TextToPdf : Screen("text_to_pdf")
    object WordToPdf : Screen("word_to_pdf")
    object SlideToPdf : Screen("slide_to_pdf")
    object ZipCreator : Screen("zip_creator")
    object OcrScanner : Screen("ocr_scanner")
    object Notepad : Screen("notepad")
    object NoteEditor : Screen("note_editor/{noteId}") {
        fun createRoute(noteId: Long) = "note_editor/$noteId"
    }
    object Settings : Screen("settings")
    object Search : Screen("search")
    object DocumentViewer : Screen("document_viewer/{uri}/{fileType}/{fileName}") {
        fun createRoute(uri: String, fileType: String, fileName: String): String {
            val encodedUri = java.net.URLEncoder.encode(uri, "UTF-8")
            val encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8")
            return "document_viewer/$encodedUri/$fileType/$encodedFileName"
        }
    }
    object PdfViewer : Screen("pdf_viewer/{uri}") {
        fun createRoute(uri: String) = "pdf_viewer/$uri"
    }
    object PdfMarkup : Screen("pdf_markup/{uri}") {
        fun createRoute(uri: String) = "pdf_markup/$uri"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(
            route = Screen.FileList.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val fileType = backStackEntry.arguments?.getString("type") ?: "PDF"
            FileListScreen(navController, fileType)
        }

        composable(Screen.Folders.route) {
            // FoldersScreen(navController)
        }

        composable(Screen.Recent.route) {
            // RecentFilesScreen(navController)
        }

        composable(Screen.Favorites.route) {
            // FavoritesScreen(navController)
        }

        composable(Screen.MyCreation.route) {
            // MyCreationScreen(navController)
        }

        composable(Screen.ImagesToPdf.route) {
            // ImagesToPdfScreen(navController)
        }

        composable(Screen.PdfToImages.route) {
            // PdfToImagesScreen(navController)
        }

        composable(Screen.MergePdf.route) {
            MergePdfScreen(navController)
        }

        composable(Screen.AddPassword.route) {
            AddPasswordScreen(navController)
        }

        composable(Screen.RemovePassword.route) {
            RemovePasswordScreen(navController)
        }

        composable(Screen.WordToPdf.route) {
            WordToPdfScreen(navController)
        }

        composable(Screen.SlideToPdf.route) {
            SlideToPdfScreen(navController)
        }

        composable(Screen.ZipCreator.route) {
            ZipCreatorScreen(navController)
        }

        composable(Screen.OcrScanner.route) {
            OcrScannerScreen(navController)
        }

        composable(Screen.Notepad.route) {
            NotepadScreen(navController)
        }

        composable(
            route = Screen.NoteEditor.route,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            NoteEditorScreen(navController, noteId)
        }

        composable(Screen.Settings.route) {
            // SettingsScreen(navController)
        }

        composable(Screen.Search.route) {
            SearchScreen(navController)
        }

        composable(
            route = Screen.DocumentViewer.route,
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("fileType") { type = NavType.StringType },
                navArgument("fileName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri") ?: ""
            val fileType = backStackEntry.arguments?.getString("fileType") ?: "UNKNOWN"
            val fileName = backStackEntry.arguments?.getString("fileName") ?: "Unknown"
            DocumentViewerScreen(navController, uri, fileType, fileName)
        }
    }
}
