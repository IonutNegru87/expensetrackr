package com.inegru.expensetrackr.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class TotalTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun totalTextField_DisplaysCorrectLabel() {
        composeTestRule.setContent {
            TotalTextField(
                total = "",
                onTotalChanged = {},
                error = null,
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText("Total")
            .assertIsDisplayed()
    }

    @Test
    fun totalTextField_DisplaysEnteredText() {
        composeTestRule.setContent {
            TotalTextField(
                total = "500.0",
                onTotalChanged = {},
                error = null,
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText("500.0")
            .assertIsDisplayed()
    }

    @Test
    fun totalTextField_DisplaysErrorMessage() {
        val errorMessage = "Total is required"
        composeTestRule.setContent {
            TotalTextField(
                total = "",
                onTotalChanged = {},
                error = errorMessage,
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun totalTextField_CallsOnTotalChanged() {
        var changedTotal = ""
        composeTestRule.setContent {
            TotalTextField(
                total = "",
                onTotalChanged = { changedTotal = it },
                error = null,
                modifier = Modifier
            )
        }

        composeTestRule
            .onNodeWithText("Total")
            .performTextInput("100.0")
        assert(changedTotal == "100.0")
    }

    @Test
    fun testDecimalInput() {
        val testTag = "TotalTextField"

        composeTestRule.setContent {
            TotalTextField(
                total = "",
                onTotalChanged = {},
                error = null,
                modifier = Modifier
                    .testTag(testTag)
            )
        }

        composeTestRule
            .onNodeWithTag(testTag)
            .assert(hasSetTextAction())
            .performTextInput("123.45")
    }
}