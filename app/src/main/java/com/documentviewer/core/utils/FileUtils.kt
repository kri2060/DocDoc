package com.documentviewer.core.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.documentviewer.data.model.DocumentFile
import com.documentviewer.data.model.FileType
import java.io.File

object FileUtils {

    fun getFileFromUri(context: Context, uri: Uri): DocumentFile? {
        return try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

                    val name = if (nameIndex >= 0) it.getString(nameIndex) else "Unknown"
                    val size = if (sizeIndex >= 0) it.getLong(sizeIndex) else 0L
                    val mimeType = contentResolver.getType(uri)
                    val extension = name.substringAfterLast('.', "")
                    val type = FileType.fromMimeType(mimeType)

                    DocumentFile(
                        uri = uri,
                        name = name,
                        path = uri.toString(),
                        size = size,
                        lastModified = System.currentTimeMillis(),
                        mimeType = mimeType,
                        extension = extension,
                        type = type
                    )
                } else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getMyCreationFolder(context: Context): File {
        val folder = File(context.getExternalFilesDir(null), "MyCreation")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    fun getMimeType(file: File): String? {
        val extension = file.extension
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.2f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    fun isFileExists(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }
}
