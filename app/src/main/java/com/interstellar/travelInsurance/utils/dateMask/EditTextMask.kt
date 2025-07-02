// File: app/src/main/java/com/yourpackage/yourapp/utils/EditTextExtensions.kt
package com.interstellar.travelInsurance.utils.dateMask // Adjust this to your actual package

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import com.interstellar.travelInsurance.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

// Helper function for date parsing (optional, but good for validation)
fun parseDateString(dateString: String): Date? {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    dateFormat.isLenient = false // Strict parsing
    return try {
        dateFormat.parse(dateString)
    } catch (e: Exception) {
        null // Handle invalid date
    }
}


/**
 * Applies a DD-MM-YYYY date mask to an EditText, handling input formatting and cursor position.
 * @param separator The character to use as a separator (default is '-').
 * @param onFullDateEntered Optional callback invoked when the full 10 characters (DD-MM-YYYY) are entered.
 */
fun EditText.applyDateMask(
    separator: Char = '-',
    onFullDateEntered: ((fullDate: String) -> Unit)? = null
) {
    // Set input type to number, allowing the separator character (for pasting or external input)
    this.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

    // Set max length to 10 characters (DD-MM-YYYY) using InputFilter
    this.filters = arrayOf(InputFilter.LengthFilter(10))

    // Remove any existing TextWatchers associated with this mask type (important for re-applying or avoiding duplicates)
    val existingWatchersTag = this.getTag(R.id.date_mask_text_watcher_tag_key) // Use a unique ID for the tag key
    val existingWatchers = if (existingWatchersTag is MutableList<*>) {
        existingWatchersTag.filterIsInstance<TextWatcher>().toMutableList()
    } else {
        mutableListOf()
    }

    existingWatchers.forEach { this.removeTextChangedListener(it) }
    existingWatchers.clear()


    val dateMaskWatcher = object : TextWatcher {
        private var currentFormattedText = "" // Stores the currently formatted text to prevent re-applying same text
        private var isFormatting = false     // Flag to prevent infinite recursive calls

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // No action needed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // No action needed
        }

        override fun afterTextChanged(editable: Editable?) {
            if (isFormatting || editable == null) {
                return
            }

            // Get the raw digits only from the current text
            val cleanDigits = editable.toString().replace(separator.toString(), "").filter { it.isDigit() }
            val length = cleanDigits.length

            // Build the new formatted string
            val newFormattedTextBuilder = StringBuilder()
            for (i in cleanDigits.indices) {
                newFormattedTextBuilder.append(cleanDigits[i])
                // Add separator after 2 digits (DD) and 4 digits (DDMM), but not at the very end
                if ((i == 1 || i == 3) && i < cleanDigits.length - 1) {
                    newFormattedTextBuilder.append(separator)
                }
            }
            val newFormattedText = newFormattedTextBuilder.toString()

            // Only update if the formatted text has actually changed
            if (newFormattedText == currentFormattedText) {
                return
            }

            isFormatting = true // Set flag to true to prevent recursive calls

            // Store current cursor position to restore it later
            var selectionStart = this@applyDateMask.selectionStart.coerceAtLeast(0) // Ensure non-negative

            // Apply the new formatted text to the EditText
            this@applyDateMask.setText(newFormattedText)
            currentFormattedText = newFormattedText // Update our internal tracking

            // Adjust cursor position based on changes
            val originalEditableLength = editable.length
            val newTextLength = newFormattedText.length

            if (originalEditableLength > newTextLength) { // User deleted characters
                // If cursor was right after a separator that just got removed, move it back one more
                if (selectionStart > 0 && newFormattedText.length >= selectionStart && newFormattedText.getOrNull(selectionStart - 1) == separator) {
                    selectionStart--
                }
            } else if (originalEditableLength < newTextLength) { // User typed characters
                // If a separator was just inserted at or before the cursor position, move cursor forward
                if (selectionStart > 0 && newFormattedText.length >= selectionStart && newFormattedText.getOrNull(selectionStart - 1) == separator) {
                     selectionStart++
                }
            }

            // Ensure the cursor position is within the bounds of the new text length
            this@applyDateMask.setSelection(selectionStart.coerceAtMost(newTextLength))

            isFormatting = false // Reset flag

            // Invoke callback if the full date format is achieved
            if (newFormattedText.length == 10) {
                onFullDateEntered?.invoke(newFormattedText)
            }
        }
    }

    this.addTextChangedListener(dateMaskWatcher)
    // Store the watcher in the tag to allow removal if applyDateMask is called again on the same EditText
    existingWatchers.add(dateMaskWatcher)
    this.setTag(R.id.date_mask_text_watcher_tag_key, existingWatchers)
}