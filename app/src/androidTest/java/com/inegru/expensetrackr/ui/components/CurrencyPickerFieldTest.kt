package com.inegru.expensetrackr.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class CurrencyPickerFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun currencyPickerField_DisplaysCorrectLabel() {
        composeTestRule.setContent {
            CurrencyPickerField(
                currency = "",
                onCurrencyChange = {},
                error = null,
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText("Currency")
            .assertIsDisplayed()
    }

    @Test
    fun currencyPickerField_SelectPredefinedCurrency() {
        var selectedCurrency = ""
        composeTestRule.setContent {
            CurrencyPickerField(
                currency = selectedCurrency,
                onCurrencyChange = { selectedCurrency = it },
                error = null,
                modifier = Modifier
            )
        }

        // Open dropdown
        composeTestRule
            .onNodeWithTag("TrailingIcon")
            .performClick()

        // Select USD from dropdown
        composeTestRule
            .onNodeWithText("USD")
            .performClick()

        // Verify selected currency is USD
        assertEquals(selectedCurrency, "USD")
    }

    @Test
    fun currencyPickerField_SelectCustomCurrency() {
        var selectedCurrency = ""
        composeTestRule.setContent {
            CurrencyPickerField(
                currency = selectedCurrency,
                onCurrencyChange = { selectedCurrency = it },
                error = null,
                modifier = Modifier
            )
        }

        // Open dropdown
        composeTestRule
            .onNodeWithTag("TrailingIcon")
            .performClick()

        // Click on "Custom" dropdown item
        composeTestRule
            .onNodeWithText("Custom")
            .performClick()

        // Verify custom input mode is activated (optional)
        // Verify selected currency is initially empty (if applicable)
        assertEquals("", selectedCurrency)

        // Enter custom currency
        composeTestRule
            .onNodeWithTag("CurrencyTextField")
            .performTextInput("XYZ")

        // Verify selected currency is "XYZ"
        assertEquals("XYZ", selectedCurrency)
    }

    @Test
    fun currencyPickerField_ValidationError() {
        val errorMessage = "Invalid currency"
        composeTestRule.setContent {
            CurrencyPickerField(
                currency = "",
                onCurrencyChange = {},
                error = errorMessage,
                modifier = Modifier
            )
        }

        // Verify error message appears
        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }
}
