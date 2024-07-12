package com.inegru.expensetrackr.common.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun formatDate(date: LocalDate): String {
        return date.format(formatter)
    }

    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            null
        }
    }
}