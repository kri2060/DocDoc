package com.documentviewer.data.model

import android.net.Uri

data class DocumentFile(
    val uri: Uri,
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long,
    val mimeType: String?,
    val extension: String,
    val type: FileType
)

enum class FileType {
    PDF,
    WORD,
    EXCEL,
    POWERPOINT,
    IMAGE,
    VIDEO,
    AUDIO,
    TEXT,
    ZIP,
    ALL_DOCUMENTS,
    UNKNOWN;

    companion object {
        fun fromMimeType(mimeType: String?): FileType {
            return when {
                mimeType == null -> UNKNOWN
                mimeType.startsWith("application/pdf") -> PDF
                mimeType.startsWith("application/msword") ||
                mimeType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml") -> WORD
                mimeType.startsWith("application/vnd.ms-excel") ||
                mimeType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml") -> EXCEL
                mimeType.startsWith("application/vnd.ms-powerpoint") ||
                mimeType.startsWith("application/vnd.openxmlformats-officedocument.presentationml") -> POWERPOINT
                mimeType.startsWith("image/") -> IMAGE
                mimeType.startsWith("video/") -> VIDEO
                mimeType.startsWith("audio/") -> AUDIO
                mimeType.startsWith("text/") -> TEXT
                mimeType == "application/zip" -> ZIP
                else -> UNKNOWN
            }
        }

        fun fromExtension(extension: String): FileType {
            return when (extension.lowercase()) {
                "pdf" -> PDF
                "doc", "docx" -> WORD
                "xls", "xlsx" -> EXCEL
                "ppt", "pptx" -> POWERPOINT
                "jpg", "jpeg", "png", "gif", "bmp", "webp" -> IMAGE
                "mp4", "avi", "mkv", "mov" -> VIDEO
                "mp3", "wav", "ogg", "m4a" -> AUDIO
                "txt", "md", "log" -> TEXT
                "zip", "rar", "7z" -> ZIP
                else -> UNKNOWN
            }
        }
    }
}

data class PdfOperationSettings(
    val pageSize: PageSize = PageSize.A4,
    val orientation: Orientation = Orientation.PORTRAIT,
    val margin: Int = 20,
    val quality: Int = 90
)

enum class PageSize {
    A4, LETTER, LEGAL
}

enum class Orientation {
    PORTRAIT, LANDSCAPE
}
