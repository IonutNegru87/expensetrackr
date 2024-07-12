package com.inegru.expensetrackr.common.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")

    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    fun parseDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, dateFormatter)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDateTime(dateTime: LocalDateTime = LocalDateTime.now()): String {
        return dateTime.format(dateTimeFormatter)
    }

    @Suppress("unused")
    fun parseDateTime(dateTimeString: String): LocalDateTime? {
        return try {
            LocalDateTime.parse(dateTimeString, dateTimeFormatter)
        } catch (e: Exception) {
            null
        }
    }
}