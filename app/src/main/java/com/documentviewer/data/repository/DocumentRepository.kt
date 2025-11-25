package com.documentviewer.data.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.documentviewer.core.utils.FileUtils
import com.documentviewer.data.model.DocumentFile
import com.documentviewer.data.model.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class DocumentRepository(private val context: Context) {

    private val TAG = "DocumentRepository"

    suspend fun scanDocumentsByType(fileType: FileType): Flow<List<DocumentFile>> = flow {
        val documents = mutableListOf<DocumentFile>()

        withContext(Dispatchers.IO) {
            try {
                // Choose the right MediaStore collection based on file type
                val collection = when (fileType) {
                    FileType.IMAGE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    FileType.VIDEO -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    FileType.AUDIO -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Files.getContentUri("external")
                    }
                }

                val projection = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DATE_MODIFIED,
                    MediaStore.Files.FileColumns.MIME_TYPE
                )

                // Build selection based on file type
                val (selection, selectionArgs) = when (fileType) {
                    FileType.PDF -> Pair(
                        "${MediaStore.Files.FileColumns.MIME_TYPE} = ?",
                        arrayOf("application/pdf")
                    )
                    FileType.WORD -> Pair(
                        "(${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ?)",
                        arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    )
                    FileType.EXCEL -> Pair(
                        "(${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ?)",
                        arrayOf("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    )
                    FileType.POWERPOINT -> Pair(
                        "(${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ?)",
                        arrayOf("application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
                    )
                    FileType.ALL_DOCUMENTS -> Pair(
                        "(${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ?)",
                        arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/vnd.ms-excel",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            "application/vnd.ms-powerpoint",
                            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                            "application/zip"
                        )
                    )
                    FileType.IMAGE -> Pair(
                        "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ?",
                        arrayOf("image/%")
                    )
                    FileType.VIDEO -> Pair(
                        "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ?",
                        arrayOf("video/%")
                    )
                    FileType.AUDIO -> Pair(
                        "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ?",
                        arrayOf("audio/%")
                    )
                    FileType.TEXT -> Pair(
                        "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ?",
                        arrayOf("text/%")
                    )
                    FileType.ZIP -> Pair(
                        "${MediaStore.Files.FileColumns.MIME_TYPE} = ?",
                        arrayOf("application/zip")
                    )
                    else -> Pair(null, null)
                }

                val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

                Log.d(TAG, "Scanning for $fileType files...")
                Log.d(TAG, "Collection: $collection")
                Log.d(TAG, "Selection: $selection")

                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )?.use { cursor ->
                    Log.d(TAG, "Query returned ${cursor.count} results")

                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                    val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
                    val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                    while (cursor.moveToNext()) {
                        try {
                            val id = cursor.getLong(idColumn)
                            val name = cursor.getString(nameColumn) ?: "Unknown"
                            val size = cursor.getLong(sizeColumn)
                            val dateModified = cursor.getLong(dateColumn)
                            val mimeType = cursor.getString(mimeColumn)

                            // Build proper URI based on file type
                            val uri = when (fileType) {
                                FileType.IMAGE -> Uri.withAppendedPath(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id.toString()
                                )
                                FileType.VIDEO -> Uri.withAppendedPath(
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    id.toString()
                                )
                                FileType.AUDIO -> Uri.withAppendedPath(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    id.toString()
                                )
                                else -> Uri.withAppendedPath(
                                    MediaStore.Files.getContentUri("external"),
                                    id.toString()
                                )
                            }

                            val extension = FileUtils.getExtension(name)
                            val type = FileType.fromMimeType(mimeType) ?: FileType.fromExtension(extension)

                            val doc = DocumentFile(
                                uri = uri,
                                name = name,
                                path = uri.toString(),
                                size = size,
                                lastModified = dateModified * 1000,
                                mimeType = mimeType,
                                extension = extension,
                                type = type
                            )

                            documents.add(doc)
                            Log.d(TAG, "Added: $name (${FileUtils.formatFileSize(size)})")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing file: ${e.message}")
                        }
                    }
                } ?: run {
                    Log.e(TAG, "Query returned null cursor")
                }

                Log.d(TAG, "Scan complete. Found ${documents.size} $fileType files")
            } catch (e: SecurityException) {
                Log.e(TAG, "Permission denied: ${e.message}")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error scanning files: ${e.message}", e)
                throw e
            }
        }

        emit(documents)
    }

    suspend fun getAllDocuments(): Flow<List<DocumentFile>> = flow {
        val allDocs = mutableListOf<DocumentFile>()
        FileType.values().filter { it != FileType.UNKNOWN }.forEach { type ->
            scanDocumentsByType(type).collect { docs ->
                allDocs.addAll(docs)
            }
        }
        emit(allDocs.distinctBy { it.path })
    }
}
