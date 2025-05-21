// PdfUtils.kt
package com.interstellar.travelInsurance.utils

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PdfUtils {


    fun createPdfFromWebView(
        context: Context,
        webView: WebView,
        fileName: String,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            val dpi = context.resources.displayMetrics.densityDpi
            val pageWidth = (8.27f * dpi).toInt()    // A4 width in pixels
            val pageHeight = (11.69f * dpi).toInt()  // A4 height in pixels

            // Step 1: Measure WebView content
            webView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val contentWidth = webView.measuredWidth
            val contentHeight = webView.measuredHeight

            webView.layout(0, 0, contentWidth, contentHeight)

            // Step 2: Compute scale to fit width
            val scaleFactor = pageWidth.toFloat() / contentWidth

            val document = PdfDocument()
            var yOffset = 0
            var pageNumber = 1

            while (yOffset < contentHeight) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                val page = document.startPage(pageInfo)
                val canvas = page.canvas

                // Step 3: Apply translation and scale
                canvas.translate(0f, -yOffset * scaleFactor)
                canvas.scale(scaleFactor, scaleFactor)

                webView.draw(canvas)
                document.finishPage(page)

                yOffset += (pageHeight / scaleFactor).toInt()
                pageNumber++
            }

            // Step 4: File output
            val pdfDir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File(context.getExternalFilesDir(null), "pdfs")
            } else {
                val publicDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!publicDownloads.exists()) publicDownloads.mkdirs()
                publicDownloads
            }

            if (!pdfDir.exists()) pdfDir.mkdirs()
            val outputFile = File(pdfDir, "$fileName.pdf")

            FileOutputStream(outputFile).use { out ->
                document.writeTo(out)
            }
            document.close()

            sharePdf(context, outputFile)
            callback(true, "PDF created successfully at ${outputFile.absolutePath}")
        } catch (e: SecurityException) {
            e.printStackTrace()
            callback(false, "Missing storage permission on Android ≤ 28")
        } catch (e: Exception) {
            e.printStackTrace()
            callback(false, e.message ?: "PDF generation failed")
        }
    }

    /**
     * Share the PDF file using an Intent
     */
    private fun sharePdf(context: Context, file: File) {
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            context.startActivity(Intent.createChooser(intent, "Share Order Book PDF"))
        } else {
            Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show()
        }
    }
}