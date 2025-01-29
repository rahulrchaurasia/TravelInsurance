package com.interstellar.travelInsurance.utils

import android.text.Editable
import android.text.TextWatcher

class MaxLengthTextWatcher(private val maxLength: Int) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s?.length ?: 0 > maxLength) {
            val newText = s.toString().take(maxLength)
            s?.replace(0, s.length, newText)
        }
    }
}