package com.inegru.expensetrackr.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class DatePickerFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun datePickerField_displays_initial_date() {
        val initialDate = "2023-07-15"

        composeTestRule.setContent {
            DatePickerField(
                date = initialDate,
                onDateChange = {}
            )
        }

        composeTestRule
            .onNodeWithText(initialDate)
            .assertIsDisplayed()
    }

    @Test
    fun datePickerField_shows_dialog_on_click() {
        composeTestRule.setContent {
            DatePickerField(
                date = "2023-07-15",
                onDateChange = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Select date")
            .performClick()

        composeTestRule
            .onNodeWithTag("DatePickerDialog")
            .assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun datePickerField_shows_and_dismisses_dialog() {
        composeTestRule.setContent {
            DatePickerField(
                date = "2023-07-15",
                onDateChange = {}
            )
        }

        // Open the dialog
        composeTestRule
            .onNodeWithContentDescription("Select date")
            .performClick()

        // Assert that the dialog is displayed
        composeTestRule
            .onNodeWithTag("DatePickerDialog")
            .assertIsDisplayed()

        // Click the Cancel button
        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        // Wait for the dialog to disappear
        composeTestRule.waitUntilDoesNotExist(
            matcher = hasTestTag("DatePickerDialog"),
            timeoutMillis = 5000
        )

        // Assert that the dialog is no longer displayed
        composeTestRule
            .onNodeWithTag("DatePickerDialog")
            .assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun datePickerField_selects_date() {
        var selectedDate = "2024-07-15"
        val newSelectedDate = "2024-07-16"
        // Although this is not displayed in the Dialog, this is what the Text actually is
        // In the format EEEE, MMMM d, yyyy
        val selectedDateWidget = "Tuesday, July 16, 2024"

        composeTestRule.setContent {
            DatePickerField(
                date = selectedDate,
                onDateChange = { selectedDate = it }
            )
        }

        // Open the dialog
        composeTestRule
            .onNodeWithContentDescription("Select date")
            .performClick()

        // Select a date
        composeTestRule
            .onNodeWithText(selectedDateWidget)
            .performClick()

        // Click OK
        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        // Wait for the dialog to disappear
        composeTestRule.waitUntilDoesNotExist(
            matcher = hasTestTag("DatePickerDialog"),
            timeoutMillis = 5000
        )

        // Check if the date was updated
        assert(selectedDate == newSelectedDate)
    }
}