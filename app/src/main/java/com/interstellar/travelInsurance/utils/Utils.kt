package com.interstellar.travelInsurance.utils

import android.text.InputFilter
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun isValidEmail(email: String): Boolean {
        val pattern = android.util.Patterns.EMAIL_ADDRESS
        return !email.isEmpty() && pattern.matcher(email).matches()
    }

    fun setEditTextMaxLength(editText: EditText, maxLength: Int) {
        editText.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    fun setEditTextMaxLength(textInputEditText: TextInputEditText, maxLength: Int) {
        val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
        textInputEditText.filters = filters
    }



     fun convertTimeToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}