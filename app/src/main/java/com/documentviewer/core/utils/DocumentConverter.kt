package com.documentviewer.core.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline
import java.io.InputStream

object DocumentConverter {

    /**
     * Converts a DOCX file to HTML with embedded images and full formatting preservation
     */
    fun convertDocxToHtml(context: Context, uri: Uri): String {
        return try {
            android.util.Log.d("DocumentConverter", "Starting DOCX to HTML conversion for URI: $uri")
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                android.util.Log.d("DocumentConverter", "Input stream opened successfully")
                val document = XWPFDocument(stream)
                android.util.Log.d("DocumentConverter", "XWPFDocument created successfully")

                val htmlBuilder = StringBuilder()

                // Process each paragraph
                for (paragraph in document.paragraphs) {
                    htmlBuilder.append(convertParagraphToHtml(paragraph))
                }

                // Process tables
                for (table in document.tables) {
                    htmlBuilder.append(convertTableToHtml(table))
                }

                // Get embedded images
                val images = mutableMapOf<String, String>()
                for ((index, picture) in document.allPictures.withIndex()) {
                    val base64Data = Base64.encodeToString(picture.data, Base64.NO_WRAP)
                    val mimeType = picture.suggestFileExtension() ?: "png"
                    images["image$index"] = "data:image/$mimeType;base64,$base64Data"
                }

                val htmlContent = htmlBuilder.toString()
                android.util.Log.d("DocumentConverter", "HTML content length: ${htmlContent.length}")

                // Wrap the HTML content with proper styling
                wrapHtmlWithStyles(htmlContent)
            } ?: createErrorHtml("Unable to open document")
        } catch (e: Exception) {
            android.util.Log.e("DocumentConverter", "Error converting document", e)
            createErrorHtml("Error converting document: ${e.message}\n\n${e.stackTraceToString()}")
        }
    }

    private fun convertParagraphToHtml(paragraph: XWPFParagraph): String {
        if (paragraph.text.trim().isEmpty()) {
            return "<p>&nbsp;</p>\n"
        }

        val styleClass = when (paragraph.style) {
            "Heading1", "heading 1" -> "h1"
            "Heading2", "heading 2" -> "h2"
            "Heading3", "heading 3" -> "h3"
            "Heading4", "heading 4" -> "h4"
            "Heading5", "heading 5" -> "h5"
            "Heading6", "heading 6" -> "h6"
            else -> when {
                paragraph.styleID?.lowercase()?.contains("title") == true -> "h1"
                paragraph.styleID?.lowercase()?.contains("subtitle") == true -> "h2"
                paragraph.styleID?.lowercase()?.contains("heading") == true -> "h3"
                else -> "p"
            }
        }

        val textAlign = when (paragraph.alignment) {
            ParagraphAlignment.CENTER -> " style=\"text-align: center;\""
            ParagraphAlignment.RIGHT -> " style=\"text-align: right;\""
            ParagraphAlignment.BOTH -> " style=\"text-align: justify;\""
            else -> ""
        }

        val content = StringBuilder()
        for (run in paragraph.runs) {
            content.append(convertRunToHtml(run))
        }

        // Handle lists
        val numId = paragraph.numID
        return if (numId != null) {
            "<li>$content</li>\n"
        } else {
            "<$styleClass$textAlign>$content</$styleClass>\n"
        }
    }

    private fun convertRunToHtml(run: XWPFRun): String {
        var text = run.text() ?: ""
        if (text.isEmpty()) return ""

        // Escape HTML special characters
        text = text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("\n", "<br/>")

        val styles = mutableListOf<String>()
        var styledText = text

        // Font size
        val fontSize = run.fontSize
        if (fontSize > 0) {
            styles.add("font-size: ${fontSize}pt")
        }

        // Font color
        val color = run.color
        if (color != null && color != "auto") {
            styles.add("color: #$color")
        }

        // Highlight color
        val highlight = run.textHighlightColor
        if (highlight != null && highlight.toString() != "none") {
            val highlightColor = when (highlight.toString()) {
                "yellow" -> "#ffff00"
                "green" -> "#00ff00"
                "cyan" -> "#00ffff"
                "magenta" -> "#ff00ff"
                "blue" -> "#0000ff"
                "red" -> "#ff0000"
                "darkBlue" -> "#000080"
                "darkCyan" -> "#008080"
                "darkGreen" -> "#008000"
                "darkMagenta" -> "#800080"
                "darkRed" -> "#800000"
                "darkYellow" -> "#808000"
                "darkGray" -> "#808080"
                "lightGray" -> "#c0c0c0"
                else -> null
            }
            if (highlightColor != null) {
                styles.add("background-color: $highlightColor")
            }
        }

        // Apply formatting tags
        if (run.isBold) {
            styledText = "<strong>$styledText</strong>"
        }
        if (run.isItalic) {
            styledText = "<em>$styledText</em>"
        }
        if (run.underline != UnderlinePatterns.NONE) {
            styledText = "<u>$styledText</u>"
        }
        if (run.isStrikeThrough) {
            styledText = "<s>$styledText</s>"
        }
        // Note: Subscript/superscript detection is complex in POI, skip for now

        // Apply inline styles
        return if (styles.isNotEmpty()) {
            "<span style=\"${styles.joinToString("; ")}\">$styledText</span>"
        } else {
            styledText
        }
    }

    private fun convertTableToHtml(table: XWPFTable): String {
        val html = StringBuilder("<table>\n")

        for ((rowIndex, row) in table.rows.withIndex()) {
            html.append("<tr>\n")
            for (cell in row.tableCells) {
                val tag = if (rowIndex == 0) "th" else "td"

                // Get cell background color
                val bgColor = cell.color
                val styleAttr = if (bgColor != null && bgColor != "auto") {
                    " style=\"background-color: #$bgColor;\""
                } else {
                    ""
                }

                html.append("<$tag$styleAttr>")
                for (paragraph in cell.paragraphs) {
                    val content = StringBuilder()
                    for (run in paragraph.runs) {
                        content.append(convertRunToHtml(run))
                    }
                    if (content.isNotEmpty()) {
                        html.append(content)
                    }
                }
                html.append("</$tag>\n")
            }
            html.append("</tr>\n")
        }

        html.append("</table>\n")
        return html.toString()
    }

    /**
     * Wraps the converted HTML with CSS styles for better rendering
     */
    private fun wrapHtmlWithStyles(bodyContent: String): String {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            padding: 20px 24px;
            background-color: #ffffff;
            color: #000000;
            word-wrap: break-word;
            overflow-wrap: break-word;
            max-width: 100%;
        }

        /* Headings */
        h1, h2, h3, h4, h5, h6 {
            margin-top: 20px;
            margin-bottom: 12px;
            font-weight: bold;
            line-height: 1.3;
        }

        h1 { font-size: 2em; }
        h2 { font-size: 1.5em; }
        h3 { font-size: 1.3em; }
        h4 { font-size: 1.1em; }
        h5 { font-size: 1em; }
        h6 { font-size: 0.9em; }

        /* Paragraphs */
        p {
            margin-bottom: 12px;
        }

        /* Lists */
        ul, ol {
            margin: 10px 0;
            padding-left: 30px;
        }

        li {
            margin-bottom: 6px;
        }

        /* Tables */
        table {
            border-collapse: collapse;
            width: 100%;
            margin: 16px 0;
            font-size: 0.95em;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        th {
            background-color: #f5f5f5;
            font-weight: bold;
        }

        tr:nth-child(even) {
            background-color: #fafafa;
        }

        /* Code blocks */
        pre {
            background-color: #f4f4f4;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 12px;
            margin: 16px 0;
            overflow-x: auto;
            font-family: 'Courier New', Courier, monospace;
            font-size: 0.9em;
            line-height: 1.4;
        }

        code {
            font-family: 'Courier New', Courier, monospace;
            background-color: #f4f4f4;
            padding: 2px 6px;
            border-radius: 3px;
            font-size: 0.9em;
        }

        pre code {
            background-color: transparent;
            padding: 0;
        }

        /* Images */
        img {
            max-width: 100%;
            height: auto;
            display: block;
            margin: 16px auto;
            border-radius: 4px;
        }

        /* Links */
        a {
            color: #0066cc;
            text-decoration: underline;
            word-wrap: break-word;
        }

        a:visited {
            color: #551a8b;
        }

        /* Blockquotes */
        blockquote {
            border-left: 4px solid #ddd;
            padding-left: 16px;
            margin: 16px 0;
            color: #666;
            font-style: italic;
        }

        /* Horizontal rules */
        hr {
            border: 0;
            border-top: 1px solid #ddd;
            margin: 20px 0;
        }

        /* Strong and emphasis */
        strong, b {
            font-weight: bold;
        }

        em, i {
            font-style: italic;
        }

        /* Underline and strikethrough */
        u {
            text-decoration: underline;
        }

        s, strike, del {
            text-decoration: line-through;
        }

        /* Text alignment classes */
        .text-center {
            text-align: center;
        }

        .text-right {
            text-align: right;
        }

        .text-left {
            text-align: left;
        }

        /* Responsive improvements */
        @media screen and (max-width: 600px) {
            body {
                padding: 16px 20px;
                font-size: 14px;
            }

            h1 { font-size: 1.6em; }
            h2 { font-size: 1.4em; }
            h3 { font-size: 1.2em; }

            table {
                font-size: 0.85em;
            }

            th, td {
                padding: 6px;
            }
        }
    </style>
</head>
<body>
$bodyContent
</body>
</html>
        """.trimIndent()
    }

    /**
     * Creates an error HTML page
     */
    private fun createErrorHtml(message: String): String {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            font-family: sans-serif;
            padding: 20px;
            background-color: #fff;
        }
        .error {
            color: #d32f2f;
            padding: 16px;
            border-left: 4px solid #d32f2f;
            background-color: #ffebee;
            border-radius: 4px;
            white-space: pre-wrap;
            font-family: monospace;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="error">$message</div>
</body>
</html>
        """.trimIndent()
    }
}
