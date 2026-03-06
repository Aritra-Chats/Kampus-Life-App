package com.example.kampus_life_official.data_insertion

import android.util.Log
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Checks if a given date falls on a holiday, supporting multiple formats:
 * - Single date: "dd-MM-yyyy" or ISO "yyyy-MM-ddT00:00:00.000Z"
 * - Date range: "dd-MM-yyyy--dd-MM-yyyy" or mix of above.
 */
fun isDateOnHoliday(selectedDate: LocalDate, holiday: Holiday): Boolean {
    val holidayDateString = holiday.dateString?.trim() ?: return false
    val dmyFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    return try {
        if (holidayDateString.contains("--")) {
            val dates = holidayDateString.split("--").map { it.trim() }
            if (dates.size < 2) return false
            
            val startDate = parseFlexDate(dates[0], dmyFormatter)
            val endDate = parseFlexDate(dates[1], dmyFormatter)
            
            if (startDate == null || endDate == null) return false
            
            // Inclusive range check
            !selectedDate.isBefore(startDate) && !selectedDate.isAfter(endDate)
        } else {
            val holidayDate = parseFlexDate(holidayDateString, dmyFormatter)
            selectedDate.isEqual(holidayDate)
        }
    } catch (_: Exception) { false }
}

/**
 * Robust date parser that handles both DMY and ISO formats.
 */
private fun parseFlexDate(dateStr: String, dmyFormatter: DateTimeFormatter): LocalDate? {
    return try {
        if (dateStr.contains("T")) {
            OffsetDateTime.parse(dateStr).toLocalDate()
        } else {
            LocalDate.parse(dateStr, dmyFormatter)
        }
    } catch (_: Exception) { null }
}

/**
 * Helper to verify data in Logcat.
 */
fun checkHolidayData(holidays: List<Holiday>) {
    if (holidays.isEmpty()) {
        Log.d("HolidaySync", "VERIFICATION: Holiday list is EMPTY")
    } else {
        Log.d("HolidaySync", "VERIFICATION: Found ${holidays.size} holidays:")
        holidays.forEachIndexed { index, holiday ->
            Log.d("HolidaySync", "[$index] Date: \"${holiday.dateString}\", Event: \"${holiday.event}\"")
        }
    }
}
