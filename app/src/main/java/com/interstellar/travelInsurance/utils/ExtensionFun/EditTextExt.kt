package com.policyboss.demoandroidapp.Utility.ExtensionFun

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText



fun EditText.setTextIfDifferent(newText: String?) {
    if (this.text.toString() != newText) {
        this.setText(newText)
    }
}


fun EditText.addPlateNumberFormatter() {
    this.addTextChangedListener(object : TextWatcher {
        private var isFormatting = false
        private var previousText = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            previousText = s.toString()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (isFormatting) return

            val currentText = s.toString().uppercase().replace("-", "")
            val formattedText = StringBuilder()

            if (currentText.isNotEmpty()) {
                for (i in currentText.indices) {
                    when (i) {
                        0 -> formattedText.append(currentText[i]) // M
                        1 -> formattedText.append(currentText[i]).append("-") // MH-
                        2 -> formattedText.append(currentText[i]) // 0
                        3 -> formattedText.append(currentText[i]).append("-") // 02-
                        4 -> formattedText.append(currentText[i]) // F
                        5 -> formattedText.append(currentText[i]).append("-") // FN-
                        else -> formattedText.append(currentText[i]) // Remaining numbers
                    }
                }
            }

            // Apply only if there's a change
            if (formattedText.toString() != previousText) {
                isFormatting = true
                this@addPlateNumberFormatter.setText(formattedText)
                this@addPlateNumberFormatter.setSelection(
                    minOf(formattedText.length, formattedText.length)
                )
                isFormatting = false
            }
        }
    })
}



// The extension function itself, taking the separator and optional callback
fun EditText.applyDateMask2(
    separator: Char = '-', // Default to hyphen, but can be customized
    onLastEntry: ((strData: String) -> Unit)? = null // Optional callback
) {
    // Set input type to number, allowing the separator character
    this.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

    // Set max length to 10 characters (DD-MM-YYYY)
    this.filters = arrayOf(InputFilter.LengthFilter(10))

    // Remove any existing TextWatchers of this type to prevent duplicates
    // (Optional, but good for re-applying masks)
    val existingWatchers = this.tag as? MutableList<TextWatcher> ?: mutableListOf()
    existingWatchers.forEach { this.removeTextChangedListener(it) }
    existingWatchers.clear()


    val textWatcher = object : TextWatcher {
        private var currentFormattedText = "" // Stores the currently formatted text
        private var isFormatting = false     // Flag to prevent infinite loops

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

            // Get the raw digits only
            val cleanDigits = editable.toString().replace(separator.toString(), "").filter { it.isDigit() }
            val length = cleanDigits.length

            // Build the formatted string
            val newFormattedText = StringBuilder()

            for (i in cleanDigits.indices) {
                newFormattedText.append(cleanDigits[i])
                // Add separator after 2 digits (DD) and 4 digits (DDMM)
                if ((i == 1 || i == 3) && i < cleanDigits.length - 1) {
                    newFormattedText.append(separator)
                }
            }

            // Only update if the formatted text actually changed
            if (newFormattedText.toString() == currentFormattedText) {
                return
            }

            isFormatting = true // Set flag to prevent re-entry

            val selectionStart = this@applyDateMask2.selectionStart // Store current cursor position

            this@applyDateMask2.setText(newFormattedText.toString()) // Set the new formatted text
            currentFormattedText = newFormattedText.toString() // Update the stored formatted text

            // Restore cursor position
            var newSelection = selectionStart
            val originalLength = editable.length
            val newLength = newFormattedText.length

            if (originalLength > newLength) { // Backspacing - adjust cursor back if a separator was removed
                if (selectionStart > 0 && newFormattedText.length > selectionStart - 1 && newFormattedText[selectionStart - 1] == separator) {
                    newSelection-- // Cursor was to the right of a separator that got removed
                }
            } else if (originalLength < newLength) { // Typing - adjust cursor forward if a separator was inserted
                if (selectionStart > 0 && originalLength > selectionStart - 1 && newFormattedText[selectionStart] == separator) {
                    newSelection++ // Cursor was to the left of a position where a separator was inserted
                }
            }

            // Ensure new selection is within bounds
            newSelection = newSelection.coerceAtMost(newLength).coerceAtLeast(0)
            this@applyDateMask2.setSelection(newSelection)

            isFormatting = false // Reset flag

            // Invoke callback if the full date is entered
            if (currentFormattedText.length == 10) {
                onLastEntry?.invoke(currentFormattedText)
            }
        }
    }

    this.addTextChangedListener(textWatcher)
    // Store the watcher in the tag to allow removal if `applyDateMask` is called again
    existingWatchers.add(textWatcher)
    this.tag = existingWatchers
}







