package com.inegru.expensetrackr.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.inegru.expensetrackr.data.db.ExpenseDatabase
import com.inegru.expensetrackr.data.db.model.ExpenseEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34], manifest = Config.NONE)
class ExpenseDaoTest {

    private lateinit var database: ExpenseDatabase
    private lateinit var expenseDao: ExpenseDao

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ExpenseDatabase::class.java)
            .allowMainThreadQueries().build()
        expenseDao = database.expenseDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetExpense() = testScope.runTest {
        val expense = ExpenseEntity(
            id = 1,
            photoUri = "test_uri",
            date = LocalDate.now(),
            total = 100.0,
            currency = "USD",
            description = "Test expense"
        )
        expenseDao.insertExpense(expense)

        val retrievedExpense = expenseDao.getExpenseById(1)
        assertEquals(expense, retrievedExpense)
    }

    @Test
    fun getAllExpenses() = testScope.runTest {
        val expenses = listOf(
            ExpenseEntity(
                id = 1, photoUri = "uri1", date = LocalDate.now(), total = 100.0, currency = "USD"
            ), ExpenseEntity(
                id = 2,
                photoUri = "uri2",
                date = LocalDate.now().minusDays(1),
                total = 200.0,
                currency = "EUR"
            )
        )
        expenses.forEach { expenseDao.insertExpense(it) }

        val allExpenses = expenseDao.getAllExpenses().first()
        assertEquals(2, allExpenses.size)
        assertEquals(expenses.sortedByDescending { it.date }, allExpenses)
    }

    @Test
    fun deleteExpense() = testScope.runTest {
        val expense = ExpenseEntity(
            id = 1, photoUri = "uri", date = LocalDate.now(), total = 100.0, currency = "USD"
        )
        expenseDao.insertExpense(expense)
        expenseDao.deleteExpense(expense)

        val retrievedExpense = expenseDao.getExpenseById(1)
        assertNull(retrievedExpense)
    }

    @Test
    fun updateExpense() = testScope.runTest {
        val expense = ExpenseEntity(
            id = 1, photoUri = "uri", date = LocalDate.now(), total = 100.0, currency = "USD"
        )
        expenseDao.insertExpense(expense)

        val updatedExpense = expense.copy(total = 150.0)
        expenseDao.updateExpense(updatedExpense)

        val retrievedExpense = expenseDao.getExpenseById(1)
        assertEquals(updatedExpense, retrievedExpense)
    }

    @Test
    fun getNonExistentExpense() = testScope.runTest {
        val retrievedExpense = expenseDao.getExpenseById(999)
        assertNull(retrievedExpense)
    }
}