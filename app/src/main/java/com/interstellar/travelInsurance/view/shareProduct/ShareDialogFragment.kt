package com.interstellar.travelInsurance.view.shareProduct

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentShareDialogBinding
import java.io.File


import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.view.Window


import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.utils.PdfUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


import java.io.FileOutputStream
import java.io.IOException

class ShareDialogFragment : DialogFragment() {

    private var _binding: FragmentShareDialogBinding? = null
    private val binding get() = _binding!!

    private var pdfFile: File? = null

    // Request permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            createAndSharePdf()
        } else {
            Toast.makeText(requireContext(), "Storage permission is required to share PDF", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.CustomMaterial3Dialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareDialogBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set dialog width to 90% of screen width

        setupWebView()

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnShare.setOnClickListener {
            checkStoragePermission()
        }
    }


    private fun setupWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            // Add JavaScript interface for WebView to Android communication
            addJavascriptInterface(WebAppInterface(requireContext()), "Android")

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    // Call JavaScript function to populate data
                    val jsonData = getOrderDataJson()
                    view?.evaluateJavascript(
                        "populateFields('$jsonData', 'PRIME - (GOREGAON (W))', '351-2526')",
                        null
                    )

                    //region comment
                    // Process JSON in background
//                    lifecycleScope.launch(Dispatchers.Default) {
//                        val rawJson = getOrderDataJson()
//                        val safeJson = rawJson
//                            .replace("\\", "\\\\")
//                            .replace("\"", "\\\"")
//                            .replace("\n", "\\n")
//                            .replace("\r", "")
//
//                        // Now inject in WebView on main thread
//                        withContext(Dispatchers.Main) {
//                            view?.evaluateJavascript(
//                                "populateFields(\"$safeJson\", \"PRIME - (GOREGAON (W))\", \"351-2526\")",
//                                null
//                            )
//                        }
//                    }
                    //endregion

                }
            }

            // Load HTML from assets
            try {
                val htmlContent = loadHtmlFromAssets("orderbook.html")
                loadDataWithBaseURL(
                    "file:///android_asset/",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            } catch (e: IOException) {
                Toast.makeText(context, "Error loading HTML file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadHtmlFromAssets(fileName: String): String {
        return requireContext().assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun getOrderDataJson(): String {
        // Sample data for the order book
        val jsonData = """
            [{"isSelected":false,"productColorID":10,"productColorImage":"","productColorName":"BLACK","productDesc":"SETS","productGuid":"3b6f6fdb-2a43-431c-8a1f-f9b2faa48aa2","productID":73884,"productImageID":0,"productNo":"1848a","productParts":[{"productCostPrice":0,"productDescription":"","productID":0,"productLength":"M","productMRP":3975,"productOpeningQuantity":0,"productPartID":0,"productQty":2,"productSalePrice":2345,"productToProductID":190238,"productType":0},{"productCostPrice":0,"productDescription":"","productID":0,"productLength":"L","productMRP":3975,"productOpeningQuantity":0,"productPartID":0,"productQty":2,"productSalePrice":2345,"productToProductID":190239,"productType":0},{"productCostPrice":0,"productDescription":"","productID":0,"productLength":"XL","productMRP":3975,"productOpeningQuantity":0,"productPartID":0,"productQty":2,"productSalePrice":2345,"productToProductID":190240,"productType":0}]}]
        """.trimIndent()



        // Escape double quotes for JavaScript
        return jsonData.replace("\"", "\\\"")
    }

    private fun checkStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // ✅ No permission needed on Android 10+
            createAndSharePdf()
        } else {

            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    createAndSharePdf()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    Toast.makeText(
                        requireContext(),
                        "Storage permission is needed to save and share PDF",
                        Toast.LENGTH_SHORT
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }

    }

    private fun createAndSharePdf() {
        binding.progressBar.visibility = View.VISIBLE

        PdfUtils.createPdfFromWebView(
            requireContext(),
            binding.webView,
            "OrderBook_${System.currentTimeMillis()}",

        ) { success, message ->
            binding.progressBar.visibility = View.GONE

            if (success) {
                Toast.makeText(requireContext(), "PDF Created Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to create PDF: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // JavaScript interface for WebView to communicate with Android
    inner class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun onContentReady() {
            // Modern way using lifecycleScope for UI thread operations
            lifecycleScope.launch(Dispatchers.Main) {
                // Ready to take action when the content is loaded
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}