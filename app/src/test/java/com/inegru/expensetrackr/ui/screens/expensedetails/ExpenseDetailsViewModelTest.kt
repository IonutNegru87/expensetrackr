package com.inegru.expensetrackr.ui.screens.expensedetails

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.model.Expense
import com.inegru.expensetrackr.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class ExpenseDetailsViewModelTest {

    private lateinit var viewModel: ExpenseDetailsViewModel
    private lateinit var repository: ExpenseRepository
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        repository = mockk()
        dispatcherProvider = mockk()
        coEvery { dispatcherProvider.io } returns testDispatcher
        viewModel = ExpenseDetailsViewModel(repository, dispatcherProvider)
    }

    @Test
    fun `getExpenseById should update expense state with repository result`() = testScope.runTest {
        // Given
        val expenseId = 1
        val expectedExpense = Expense(
            id = expenseId,
            photoUri = "file:///path/to/photo.jpg",
            date = LocalDate.of(2023, 7, 15),
            total = 100.0,
            currency = "USD",
            description = "Test Expense"
        )
        coEvery { repository.getExpenseById(expenseId) } returns expectedExpense

        // When
        viewModel.getExpenseById(expenseId)
        advanceUntilIdle()

        // Then
        viewModel.expense.test {
            assertEquals(expectedExpense, awaitItem())
        }
    }

    @Test
    fun `getExpenseById should set expense state to null when repository returns null`() =
        testScope.runTest {
            // Given
            val expenseId = 1
            coEvery { repository.getExpenseById(expenseId) } returns null

            // When
            viewModel.getExpenseById(expenseId)
            advanceUntilIdle()

            // Then
            viewModel.expense.test {
                assertEquals(null, awaitItem())
            }
        }

    @Test
    fun `initial expense state should be null`() = testScope.runTest {
        // Then
        viewModel.expense.test {
            assertEquals(null, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getExpenseById should handle error`() = testScope.runTest {
        // Given
        val expenseId = 1
        val errorMessage = "Network error occurred"
        coEvery { repository.getExpenseById(expenseId) } throws IOException(errorMessage)

        // When
        viewModel.getExpenseById(expenseId)
        advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.errorState.value)
        assertEquals(null, viewModel.expense.value)
    }

    @Test
    fun `getExpenseById should not update state if ViewModel is cleared`() = testScope.runTest {
        // Given
        val expenseId = 1
        val expense = Expense(
            id = expenseId,
            photoUri = "file:///path/to/photo.jpg",
            date = LocalDate.now(),
            total = 50.0,
            currency = "EUR",
            description = "Test Expense"
        )
        coEvery { repository.getExpenseById(expenseId) } coAnswers {
            delay(1000)
            expense
        }

        // When
        viewModel.getExpenseById(expenseId)
        viewModel.viewModelScope.cancel()
        advanceUntilIdle()

        // Then
        assertEquals(null, viewModel.expense.value)
    }

    @Test
    fun `getExpenseById should handle different currencies`() = testScope.runTest {
        // Given
        val expenseId = 1
        val expense = Expense(
            id = expenseId,
            photoUri = "file:///path/to/photo.jpg",
            date = LocalDate.now(),
            total = 100.0,
            currency = "JPY",
            description = "Test Expense in Yen"
        )
        coEvery { repository.getExpenseById(expenseId) } returns expense

        // When
        viewModel.getExpenseById(expenseId)
        advanceUntilIdle()

        // Then
        viewModel.expense.test {
            val result = awaitItem()
            assertEquals("JPY", result?.currency)
        }
    }

    @Test
    fun `getExpenseById should handle expenses with no description`() = testScope.runTest {
        // Given
        val expenseId = 1
        val expense = Expense(
            id = expenseId,
            photoUri = "file:///path/to/photo.jpg",
            date = LocalDate.now(),
            total = 75.0,
            currency = "USD",
            description = null
        )
        coEvery { repository.getExpenseById(expenseId) } returns expense

        // When
        viewModel.getExpenseById(expenseId)
        advanceUntilIdle()

        // Then
        viewModel.expense.test {
            val result = awaitItem()
            assertEquals(null, result?.description)
        }
    }

    @Test
    fun `getExpenseById should handle very large expense amounts`() = testScope.runTest {
        // Given
        val expenseId = 1
        val veryLargeAmount = 1_000_000_000.0
        val expense = Expense(
            id = expenseId,
            photoUri = "file:///path/to/photo.jpg",
            date = LocalDate.now(),
            total = veryLargeAmount,
            currency = "USD",
            description = "Very large expense"
        )
        coEvery { repository.getExpenseById(expenseId) } returns expense

        // When
        viewModel.getExpenseById(expenseId)
        advanceUntilIdle()

        // Then
        viewModel.expense.test {
            val result = awaitItem()
            assertEquals(veryLargeAmount, result?.total!!, 0.001)
        }
    }
}