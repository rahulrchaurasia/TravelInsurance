package com.interstellar.travelInsurance.utils.validator

import java.util.Calendar

//validation handler
class DateValidator(
    private val format: String = "dd-MM-yyyy", // Default format can be changed
    private val minYear: Int = Calendar.getInstance().get(Calendar.YEAR) - 120, // Current year minus 120
    private val maxYear: Int = Calendar.getInstance().get(Calendar.YEAR) // Current year by default
) {


    private val separators = listOf("-", "/")

    /**
     * Validates a date string in the specified format.
     *
     * @param dateString The date string to validate.
     * @return True if the date is valid, false otherwise.
     */
    fun isValid(dateString: String): Boolean {
        if (dateString.isEmpty() || dateString.length != format.length) return false

        val parts = dateString.split(Regex(separators.joinToString("|"))) // Use Regex instead of toRegex()
        if (parts.size != 3) return false

        return try {
            val year = parts[2].toInt()
            val month = parts[1].toInt()
            val day = parts[0].toInt()
            validateYear(year) && validateMonth(month) && validateDay(day, month, year)
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun validateYear(year: Int): Boolean {
        return year in minYear..maxYear
    }

    private fun validateMonth(month: Int): Boolean {
        return month in 1..12
    }

    private fun validateDay(day: Int, month: Int, year: Int): Boolean {
        if (day < 1 || day > 31) return false

        // Handle leap years correctly
        val daysInMonth = if (month in setOf(4, 6, 9, 11)) 30
        else if (month == 2) {
            if (isLeapYear(year)) 29 else 28
        } else 31
        return day <= daysInMonth
    }

    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

}

