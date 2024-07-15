package com.inegru.expensetrackr.data.db.converter


import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import java.time.LocalDate

class DateConverterTest {

    private val dateConverter = DateConverter()

    @Test
    fun `fromTimestamp converts valid date string to LocalDate`() {
        val dateString = "2024-07-15"
        val expectedDate = LocalDate.of(2024, 7, 15)

        val result = dateConverter.fromTimestamp(dateString)

        assertEquals(expectedDate, result)
    }

    @Test
    fun `fromTimestamp returns null for null input`() {
        val result = dateConverter.fromTimestamp(null)

        assertNull(result)
    }

    @Test
    fun `fromTimestamp handles invalid date string`() {
        val invalidDateString = "invalid-date"

        val result = dateConverter.fromTimestamp(invalidDateString)

        assertNull(result)
    }

    @Test
    fun `dateToTimestamp converts LocalDate to string`() {
        val date = LocalDate.of(2024, 7, 15)
        val expectedString = "2024-07-15"

        val result = dateConverter.dateToTimestamp(date)

        assertEquals(expectedString, result)
    }

    @Test
    fun `dateToTimestamp returns null for null input`() {
        val result = dateConverter.dateToTimestamp(null)

        assertNull(result)
    }

    @Test
    fun `fromTimestamp and dateToTimestamp are inverse operations`() {
        val originalDate = LocalDate.of(2024, 7, 15)

        val timestamp = dateConverter.dateToTimestamp(originalDate)
        val reconstructedDate = dateConverter.fromTimestamp(timestamp)

        assertEquals(originalDate, reconstructedDate)
    }


    @Test
    fun `fromTimestamp handles edge cases`() {
        val edgeCases = listOf(
            "2023-01-01", // New Year's Day
            "2023-12-31", // New Year's Eve
            "2024-02-29", // Leap year
            "2023-02-28"  // Non-leap year February end
        )

        edgeCases.forEach { dateString ->
            val result = dateConverter.fromTimestamp(dateString)
            assertEquals("Failed for date string: $dateString", LocalDate.parse(dateString), result)
        }
    }
}