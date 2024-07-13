package com.inegru.expensetrackr.ui.screens.expenselist

import app.cash.turbine.test
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.model.Expense
import com.inegru.expensetrackr.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

class ExpenseListViewModelTest {

    private lateinit var viewModel: ExpenseListViewModel
    private lateinit var repository: ExpenseRepository
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        dispatcherProvider = mockk()
        every { dispatcherProvider.io } returns testDispatcher

        // Mock the flow of expenses
        coEvery { repository.allExpenses } returns flowOf(emptyList())

        viewModel = ExpenseListViewModel(repository, dispatcherProvider)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init fetches expenses and sorts them`() = testScope.runTest {
        // Given
        val expense1 = Expense(
            id = 1,
            photoUri = "uri1",
            date = LocalDate.of(2023, 1, 15),
            total = 100.0,
            currency = "USD"
        )
        val expense2 = Expense(
            id = 2,
            photoUri = "uri2",
            date = LocalDate.of(2023, 6, 20),
            total = 200.0,
            currency = "EUR"
        )
        val expense3 = Expense(
            id = 3,
            photoUri = "uri3",
            date = LocalDate.of(2023, 6, 20),
            total = 300.0,
            currency = "GBP"
        )
        val unsortedExpenses = listOf(expense1, expense2, expense3)

        coEvery { repository.allExpenses } returns flowOf(unsortedExpenses)

        // When
        val viewModel = ExpenseListViewModel(repository, dispatcherProvider)
        advanceUntilIdle()

        // Then
        viewModel.expenses.test {
            val result = awaitItem()
            assertEquals(listOf(expense3, expense2, expense1), result)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `deleteExpense calls repository`() = testScope.runTest {
        // Given
        val expense = Expense(
            id = 1,
            photoUri = "uri",
            date = LocalDate.of(2023, 7, 15),
            total = 100.0,
            currency = "USD"
        )

        // When
        viewModel.deleteExpense(expense)
        advanceUntilIdle()

        // Then
        coVerify { repository.deleteExpense(expense) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `expenses flow updates and sorts when repository data changes`() = testScope.runTest {
        // Given
        val initialExpenses = listOf(
            Expense(
                id = 1,
                photoUri = "uri1",
                date = LocalDate.of(2023, 1, 15),
                total = 100.0,
                currency = "USD"
            )
        )
        val updatedExpenses = listOf(
            Expense(
                id = 1,
                photoUri = "uri1",
                date = LocalDate.of(2023, 1, 15),
                total = 100.0,
                currency = "USD"
            ),
            Expense(
                id = 2,
                photoUri = "uri2",
                date = LocalDate.of(2023, 6, 20),
                total = 200.0,
                currency = "EUR"
            ),
            Expense(
                id = 3,
                photoUri = "uri3",
                date = LocalDate.of(2023, 6, 20),
                total = 300.0,
                currency = "GBP"
            )
        )
        val expensesFlow = MutableStateFlow(initialExpenses)
        coEvery { repository.allExpenses } returns expensesFlow

        // When
        val viewModel = ExpenseListViewModel(repository, dispatcherProvider)
        advanceUntilIdle()

        // Then
        viewModel.expenses.test {
            assertEquals(initialExpenses, awaitItem())

            // Update the flow with unsorted expenses
            expensesFlow.value = updatedExpenses

            // The ViewModel should sort the updated expenses
            val sortedUpdatedExpenses = listOf(
                updatedExpenses[2], // id = 3, latest date
                updatedExpenses[1], // id = 2, same date as above but lower id
                updatedExpenses[0]  // id = 1, earliest date
            )
            assertEquals(sortedUpdatedExpenses, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init with empty repository returns empty list`() = testScope.runTest {
        // Given
        coEvery { repository.allExpenses } returns flowOf(emptyList())

        // When
        val viewModel = ExpenseListViewModel(repository, dispatcherProvider)
        advanceUntilIdle()

        // Then
        viewModel.expenses.test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `expenses are sorted by date in descending order`() = testScope.runTest {
        // Given
        val oldestExpense = Expense(
            id = 1,
            photoUri = "uri1",
            date = LocalDate.of(2023, 1, 1),
            total = 100.0,
            currency = "USD"
        )
        val middleExpense = Expense(
            id = 2,
            photoUri = "uri2",
            date = LocalDate.of(2023, 6, 15),
            total = 200.0,
            currency = "EUR"
        )
        val newestExpense = Expense(
            id = 3,
            photoUri = "uri3",
            date = LocalDate.of(2023, 12, 31),
            total = 300.0,
            currency = "GBP"
        )
        val unsortedExpenses = listOf(middleExpense, oldestExpense, newestExpense)

        coEvery { repository.allExpenses } returns flowOf(unsortedExpenses)

        // When
        val viewModel = ExpenseListViewModel(repository, dispatcherProvider)
        advanceUntilIdle()

        // Then
        viewModel.expenses.test {
            val result = awaitItem()
            assertEquals(listOf(newestExpense, middleExpense, oldestExpense), result)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `expenses with same date are sorted by id in descending order`() = testScope.runTest {
        // Given
        val date = LocalDate.of(2023, 7, 15)
        val expense1 = Expense(
            id = 1,
            photoUri = "uri1",
            date = date,
            total = 100.0,
            currency = "USD"
        )
        val expense2 = Expense(
            id = 2,
            photoUri = "uri2",
            date = date,
            total = 200.0,
            currency = "EUR"
        )
        val expense3 = Expense(
            id = 3,
            photoUri = "uri3",
            date = date,
            total = 300.0,
            currency = "GBP"
        )
        val unsortedExpenses = listOf(expense2, expense1, expense3)

        coEvery { repository.allExpenses } returns flowOf(unsortedExpenses)

        // When
        val viewModel = ExpenseListViewModel(repository, dispatcherProvider)
        advanceUntilIdle()

        // Then
        viewModel.expenses.test {
            val result = awaitItem()
            assertEquals(listOf(expense3, expense2, expense1), result)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetching expenses handles empty list`() = testScope.runTest {
        // Given
        coEvery { repository.allExpenses } returns flowOf(emptyList())

        // When
        val viewModel = ExpenseListViewModel(repository, dispatcherProvider)
        advanceUntilIdle()

        // Then
        viewModel.expenses.test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetching expenses handles error`() = testScope.runTest {
        // Given
        val errorMessage = "Unknown error"
        coEvery { repository.allExpenses } throws IOException(errorMessage)

        // When
        val viewModel = ExpenseListViewModel(repository, dispatcherProvider)
        advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.errorState.value)
        assertTrue(viewModel.expenses.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `deleting last expense results in empty list`() = testScope.runTest {
        // Given
        val singleExpense = Expense(
            id = 1,
            photoUri = "uri",
            date = LocalDate.of(2023, 7, 15),
            total = 100.0,
            currency = "USD"
        )
        val expensesFlow = MutableStateFlow(listOf(singleExpense))
        coEvery { repository.allExpenses } returns expensesFlow
        coEvery { repository.deleteExpense(any()) } coAnswers {
            expensesFlow.value = emptyList()
        }

        val viewModel = ExpenseListViewModel(repository, dispatcherProvider)
        advanceUntilIdle()

        // When
        viewModel.deleteExpense(singleExpense)
        advanceUntilIdle()

        // Then
        viewModel.expenses.test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
        }
    }
}