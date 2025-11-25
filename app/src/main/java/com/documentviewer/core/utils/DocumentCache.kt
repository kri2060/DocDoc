package com.documentviewer.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

object DocumentCache {
    
    private fun getCacheDir(context: Context): File {
        val cacheDir = File(context.cacheDir, "document_cache")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        return cacheDir
    }
    
    private fun getFileHash(uri: Uri, lastModified: Long): String {
        val input = "${uri}_$lastModified"
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    // HTML Cache for Word documents
    fun getCachedHtml(context: Context, uri: Uri, lastModified: Long): String? {
        val hash = getFileHash(uri, lastModified)
        val cacheFile = File(getCacheDir(context), "$hash.html")
        return if (cacheFile.exists()) {
            cacheFile.readText()
        } else null
    }
    
    fun cacheHtml(context: Context, uri: Uri, lastModified: Long, html: String) {
        val hash = getFileHash(uri, lastModified)
        val cacheFile = File(getCacheDir(context), "$hash.html")
        cacheFile.writeText(html)
    }
    
    // Bitmap Cache for Excel/PowerPoint
    fun getCachedBitmaps(context: Context, uri: Uri, lastModified: Long): List<Bitmap>? {
        val hash = getFileHash(uri, lastModified)
        val cacheDir = File(getCacheDir(context), hash)
        
        if (!cacheDir.exists()) return null
        
        val bitmaps = mutableListOf<Bitmap>()
        val files = cacheDir.listFiles()?.sortedBy { it.name } ?: return null
        
        for (file in files) {
            if (file.extension == "png") {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    bitmaps.add(bitmap)
                }
            }
        }
        
        return if (bitmaps.isNotEmpty()) bitmaps else null
    }
    
    fun cacheBitmaps(context: Context, uri: Uri, lastModified: Long, bitmaps: List<Bitmap>) {
        val hash = getFileHash(uri, lastModified)
        val cacheDir = File(getCacheDir(context), hash)
        
        if (cacheDir.exists()) {
            cacheDir.deleteRecursively()
        }
        cacheDir.mkdirs()
        
        bitmaps.forEachIndexed { index, bitmap ->
            val file = File(cacheDir, "page_${index.toString().padStart(4, '0')}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }
    }
    
    // Clear cache
    fun clearCache(context: Context) {
        getCacheDir(context).deleteRecursively()
    }
    
    // Get cache size
    fun getCacheSize(context: Context): Long {
        return getCacheDir(context).walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }
}
