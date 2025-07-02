package com.interstellar.travelInsurance.utils.validator

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateValidatorUtils {

    /**
     * Validates if a given date string (in "DD-MM-YYYY" format) is a real date
     * and falls within a specified range (e.g., 1900 to current year).
     *
     * This function is "pure" - it takes input, returns a boolean, and doesn't
     * depend on or modify any UI state.
     *
     * @param dob The date string in "DD-MM-YYYY" format.
     * @return true if the date is valid and within range, false otherwise.
     */
    fun isValidDateContent(dob: String): Boolean {
        // Ensure the string has the correct length for parsing after formatting.
        // This utility assumes the input is already in "DD-MM-YYYY" format.
        if (dob.length != 10) return false

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return try {
            val parsedDate = LocalDate.parse(dob, formatter)
            val minDate = LocalDate.of(1900, 1, 1)
            val maxDate = LocalDate.now() // Current date (2025-07-02 as of your context)

            // Date must not be before minDate and not after maxDate
            !parsedDate.isBefore(minDate) && !parsedDate.isAfter(maxDate)
        } catch (e: DateTimeParseException) {
            // Catches cases like "31-02-2023" (invalid day for month) or malformed strings
            false
        }
    }
}