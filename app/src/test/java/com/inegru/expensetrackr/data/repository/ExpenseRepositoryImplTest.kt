package com.inegru.expensetrackr.data.repository

import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.data.db.dao.ExpenseDao
import com.inegru.expensetrackr.data.db.model.ExpenseEntity
import com.inegru.expensetrackr.data.db.model.asExternalModel
import com.inegru.expensetrackr.data.util.asEntity
import com.inegru.expensetrackr.model.Expense
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ExpenseRepositoryImplTest {

    private lateinit var expenseDao: ExpenseDao
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        expenseDao = mockk(relaxed = true)
        dispatcherProvider = mockk()
        every { dispatcherProvider.io } returns testDispatcher
        every { dispatcherProvider.ui } returns testDispatcher

        expenseRepository = ExpenseRepositoryImpl(expenseDao)
    }

    @Test
    fun `test allExpenses flow`() = testScope.runTest {
        // Define a list of expenses
        val expenses = listOf(
            Expense(
                id = 1,
                photoUri = "uri1",
                date = LocalDate.now(),
                total = 100.0,
                currency = "USD",
                description = "description1"
            ).asEntity(),
            Expense(
                id = 2,
                photoUri = "uri2",
                date = LocalDate.now(),
                total = 200.0,
                currency = "EUR",
                description = "description2"
            ).asEntity()
        )

        val job = launch {
            expenseRepository.allExpenses.collect { result ->
                assertEquals(expenses.map { it.asExternalModel() }, result)
            }
        }

        coEvery { expenseDao.getAllExpenses() } returns flowOf(expenses)

        job.join()
    }

    @Test
    fun `test insertExpense`() = testScope.runTest {
        val expense = Expense(
            id = 1,
            photoUri = "uri",
            date = LocalDate.now(),
            total = 100.0,
            currency = "USD",
            description = "description"
        )
        val expenseEntity = expense.asEntity()

        coEvery { expenseDao.insertExpense(expenseEntity) } just Runs

        expenseRepository.insertExpense(expense)

        coVerify { expenseDao.insertExpense(expenseEntity) }
    }

    @Test
    fun `test deleteExpense`() = testScope.runTest {
        val expense = Expense(
            id = 1,
            photoUri = "uri",
            date = LocalDate.now(),
            total = 100.0,
            currency = "USD",
            description = "description"
        )
        val expenseEntity = expense.asEntity()

        coEvery { expenseDao.deleteExpense(expenseEntity) } just Runs

        expenseRepository.deleteExpense(expense)

        coVerify { expenseDao.deleteExpense(expenseEntity) }
    }

    @Test
    fun `test getExpenseById`() = testScope.runTest {
        val expense = Expense(
            id = 1,
            photoUri = "uri",
            date = LocalDate.now(),
            total = 100.0,
            currency = "USD",
            description = "description"
        ).asEntity()

        coEvery { expenseDao.getExpenseById(expense.id) } returns expense

        val result = expenseRepository.getExpenseById(expense.id)
        assertEquals(expense.asExternalModel(), result)
    }

    @Test
    fun `test getExpenseById returns null when expense doesn't exist`() = testScope.runTest {
        val nonExistentId = 999

        coEvery { expenseDao.getExpenseById(nonExistentId) } returns null

        val result = expenseRepository.getExpenseById(nonExistentId)
        assertNull(result)
    }

    @Test
    fun `test insertMultipleExpenses`() = testScope.runTest {
        val expenses = listOf(
            Expense(
                id = 1,
                photoUri = "uri1",
                date = LocalDate.now(),
                total = 100.0,
                currency = "USD"
            ),
            Expense(
                id = 2,
                photoUri = "uri2",
                date = LocalDate.now(),
                total = 200.0,
                currency = "EUR"
            )
        )

        expenses.forEach { expense ->
            coEvery { expenseDao.insertExpense(expense.asEntity()) } just Runs
        }

        expenses.forEach { expense ->
            expenseRepository.insertExpense(expense)
        }

        expenses.forEach { expense ->
            coVerify { expenseDao.insertExpense(expense.asEntity()) }
        }
    }

    @Test
    fun `test allExpenses flow when empty`() = testScope.runTest {
        val emptyList = emptyList<ExpenseEntity>()

        coEvery { expenseDao.getAllExpenses() } returns flowOf(emptyList)

        val job = launch {
            expenseRepository.allExpenses.collect { result ->
                assertEquals(emptyList<ExpenseEntity>(), result)
            }
        }

        job.join()
    }

    @Test
    fun `test updateExpense`() = testScope.runTest {
        val expense = Expense(
            id = 1,
            photoUri = "uri",
            date = LocalDate.now(),
            total = 100.0,
            currency = "USD",
            description = "updated description"
        )
        val expenseEntity = expense.asEntity()

        coEvery { expenseDao.updateExpense(expenseEntity) } just Runs

        expenseRepository.updateExpense(expense)

        coVerify { expenseDao.updateExpense(expenseEntity) }
    }
}