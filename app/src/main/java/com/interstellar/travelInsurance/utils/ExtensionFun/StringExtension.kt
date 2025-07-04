package com.policyboss.demoandroidapp.Utility.ExtensionFun

import android.content.res.Resources
import com.interstellar.travelInsurance.utils.validator.DateValidator
import android.util.Patterns
import android.util.TypedValue

fun String.isValidDate(format: String = "dd-MM-yyyy") = DateValidator(format).isValid(this)





/**
 * Extension functions for common string validation rules.
 */
fun String?.isNotNullOrBlank(): Boolean {
    return !this.isNullOrBlank()
}

fun String.isAtLeast(minLength: Int): Boolean {
    return this.length >= minLength
}

fun String.isTenDigits(): Boolean {
    return this.length == 10 && this.all { it.isDigit() }
}

fun String.isAlphabeticWithSpaces(): Boolean {
    // Allows uppercase, lowercase letters, and spaces
    return this.matches(Regex("^[a-zA-Z ]+$"))
}

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun Int.dpToPx(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
)