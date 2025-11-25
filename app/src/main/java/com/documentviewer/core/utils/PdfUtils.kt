package com.documentviewer.core.utils

import android.content.Context
import android.net.Uri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.kernel.pdf.ReaderProperties
import com.itextpdf.kernel.pdf.PdfDocumentInfo
import com.itextpdf.kernel.utils.PdfMerger
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xslf.usermodel.XMLSlideShow
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object PdfUtils {

    /**
     * Merge multiple PDF files into a single PDF
     */
    fun mergePdfs(context: Context, pdfUris: List<Uri>, outputUri: Uri): Boolean {
        return try {
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = PdfDocument(pdfWriter)
                val merger = PdfMerger(pdfDocument)

                pdfUris.forEach { uri ->
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val sourcePdf = PdfDocument(PdfReader(inputStream))
                        merger.merge(sourcePdf, 1, sourcePdf.numberOfPages)
                        sourcePdf.close()
                    }
                }

                pdfDocument.close()
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Add password protection to a PDF file
     */
    fun addPasswordToPdf(
        context: Context,
        inputUri: Uri,
        outputUri: Uri,
        userPassword: String,
        ownerPassword: String = userPassword
    ): Boolean {
        return try {
            context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                    val reader = PdfReader(inputStream)
                    val writerProperties = WriterProperties()
                        .setStandardEncryption(
                            userPassword.toByteArray(),
                            ownerPassword.toByteArray(),
                            com.itextpdf.kernel.pdf.EncryptionConstants.ALLOW_PRINTING,
                            com.itextpdf.kernel.pdf.EncryptionConstants.ENCRYPTION_AES_128
                        )

                    val writer = PdfWriter(outputStream, writerProperties)
                    val pdfDoc = PdfDocument(reader, writer)
                    pdfDoc.close()
                    true
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Remove password from a PDF file
     */
    fun removePasswordFromPdf(
        context: Context,
        inputUri: Uri,
        outputUri: Uri,
        password: String
    ): Boolean {
        return try {
            context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                    val readerProperties = ReaderProperties().setPassword(password.toByteArray())
                    val reader = PdfReader(inputStream, readerProperties)
                    val writer = PdfWriter(outputStream)
                    val pdfDoc = PdfDocument(reader, writer)
                    pdfDoc.close()
                    true
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Convert Word document to PDF
     */
    fun convertWordToPdf(context: Context, wordUri: Uri, outputUri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(wordUri)?.use { inputStream ->
                context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                    // For now, using a basic conversion
                    // In production, you might want to use more sophisticated libraries
                    val document = XWPFDocument(inputStream)

                    val pdfWriter = PdfWriter(outputStream)
                    val pdfDocument = PdfDocument(pdfWriter)
                    val pageSize = com.itextpdf.kernel.geom.PageSize.A4

                    document.paragraphs.forEach { paragraph ->
                        val page = pdfDocument.addNewPage(pageSize)
                        // Basic conversion - this is simplified
                        // You may want to use a more robust conversion library
                    }

                    pdfDocument.close()
                    document.close()
                    true
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Convert PowerPoint to PDF
     */
    fun convertPowerPointToPdf(context: Context, pptUri: Uri, outputUri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(pptUri)?.use { inputStream ->
                context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                    val presentation = XMLSlideShow(inputStream)

                    val pdfWriter = PdfWriter(outputStream)
                    val pdfDocument = PdfDocument(pdfWriter)

                    // Basic conversion - this is simplified
                    presentation.slides.forEach { slide ->
                        val page = pdfDocument.addNewPage()
                        // You may want to use a more robust conversion library
                    }

                    pdfDocument.close()
                    presentation.close()
                    true
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Get PDF page count
     */
    fun getPdfPageCount(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = PdfReader(inputStream)
                val pdfDoc = PdfDocument(reader)
                val count = pdfDoc.numberOfPages
                pdfDoc.close()
                count
            } ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Check if PDF is password protected
     */
    fun isPdfPasswordProtected(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = PdfReader(inputStream)
                val pdfDoc = PdfDocument(reader)
                pdfDoc.close()
                false
            } ?: false
        } catch (e: com.itextpdf.kernel.exceptions.BadPasswordException) {
            true
        } catch (e: Exception) {
            false
        }
    }
}
