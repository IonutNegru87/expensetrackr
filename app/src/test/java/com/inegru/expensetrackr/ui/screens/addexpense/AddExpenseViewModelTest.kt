package com.inegru.expensetrackr.ui.screens.addexpense

import android.net.Uri
import com.inegru.expensetrackr.common.coroutines.DispatcherProvider
import com.inegru.expensetrackr.data.repository.ExpenseRepository
import com.inegru.expensetrackr.model.Expense
import com.inegru.expensetrackr.util.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate


class AddExpenseViewModelTest {

    private lateinit var viewModel: AddExpenseViewModel
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

        viewModel = AddExpenseViewModel(repository, dispatcherProvider)
    }

    @Test
    fun `initial state is empty and form is invalid`() = testScope.runTest {
        assertEquals(null, viewModel.photoUri.value)
        assertEquals("", viewModel.date.value)
        assertEquals("", viewModel.total.value)
        assertEquals("", viewModel.currency.value)
        assertEquals("", viewModel.description.value)
        assertFalse(viewModel.isFormValid.value)
    }

    @Test
    fun `updating photo uri validates form`() = testScope.runTest {
        val uri = mockk<Uri>()
        viewModel.updatePhotoUri(uri)
        assertEquals(uri, viewModel.photoUri.value)
        assertFalse(viewModel.isFormValid.value) // Still false because other fields are empty
    }

    @Test
    fun `updating date validates form`() = testScope.runTest {
        viewModel.updateDate("2023-07-15")
        assertEquals("2023-07-15", viewModel.date.value)
        assertFalse(viewModel.isFormValid.value) // Still false because other fields are empty
    }

    @Test
    fun `updating total validates form and checks for errors`() = testScope.runTest {
        viewModel.updateTotal("100.00")
        assertEquals("100.00", viewModel.total.value)
        assertNull(viewModel.totalError.value)
        assertFalse(viewModel.isFormValid.value) // Still false because other fields are empty

        viewModel.updateTotal("")
        assertEquals("Total is required", viewModel.totalError.value)

        viewModel.updateTotal("invalid")
        assertEquals("Invalid total amount", viewModel.totalError.value)

        viewModel.updateTotal("-10")
        assertEquals("Total must be greater than zero", viewModel.totalError.value)
    }

    @Test
    fun `updating currency validates form and checks for errors`() = testScope.runTest {
        viewModel.updateCurrency("USD")
        assertEquals("USD", viewModel.currency.value)
        assertNull(viewModel.currencyError.value)
        assertFalse(viewModel.isFormValid.value) // Still false because other fields are empty

        viewModel.updateCurrency("")
        assertEquals("Currency is required", viewModel.currencyError.value)

        viewModel.updateCurrency("USDD")
        assertEquals("Currency must be a 3-letter code", viewModel.currencyError.value)
    }

    @Test
    fun `updating description does not affect form validity`() = testScope.runTest {
        viewModel.updateDescription("Test expense")
        assertEquals("Test expense", viewModel.description.value)
        assertFalse(viewModel.isFormValid.value)
    }

    @Test
    fun `form becomes valid when all required fields are filled correctly`() = testScope.runTest {
        val uri = mockk<Uri>()
        viewModel.updatePhotoUri(uri)
        viewModel.updateDate("2023-07-15")
        viewModel.updateTotal("100.00")
        viewModel.updateCurrency("USD")

        assertTrue(viewModel.isFormValid.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saveExpense emits SaveSuccess event on successful save`() = testScope.runTest {
        val uri = mockk<Uri>()
        viewModel.updatePhotoUri(uri)
        viewModel.updateDate("2023-07-15")
        viewModel.updateTotal("100.00")
        viewModel.updateCurrency("USD")
        viewModel.updateDescription("Test expense")

        coEvery { repository.insertExpense(any()) } just Runs

        var capturedEvent: AddExpenseViewModel.UiEvent? = null
        val job = launch {
            viewModel.uiEvent.collect { event ->
                capturedEvent = event
            }
        }

        viewModel.saveExpense()
        advanceUntilIdle()

        assertEquals(AddExpenseViewModel.UiEvent.SaveSuccess, capturedEvent)
        coVerify { repository.insertExpense(any()) }
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saveExpense emits SaveError event on failure`() = testScope.runTest {
        val uri = mockk<Uri>()
        viewModel.updatePhotoUri(uri)
        viewModel.updateDate("2023-07-15")
        viewModel.updateTotal("100.00")
        viewModel.updateCurrency("USD")

        val errorMessage = "Failed to save expense"
        coEvery { repository.insertExpense(any()) } throws Exception(errorMessage)

        var capturedEvent: AddExpenseViewModel.UiEvent? = null
        val job = launch {
            viewModel.uiEvent.collect { event ->
                capturedEvent = event
            }
        }

        viewModel.saveExpense()
        advanceUntilIdle()

        assertTrue(capturedEvent is AddExpenseViewModel.UiEvent.SaveError)
        assertEquals(errorMessage, (capturedEvent as AddExpenseViewModel.UiEvent.SaveError).message)
        job.cancel()
    }

    @Test
    fun `form remains invalid when only some fields are filled`() = testScope.runTest {
        val uri = mockk<Uri>()
        viewModel.updatePhotoUri(uri)
        viewModel.updateDate("2023-07-15")
        // Total and Currency are still empty
        assertFalse(viewModel.isFormValid.value)

        viewModel.updateTotal("100.00")
        // Currency is still empty
        assertFalse(viewModel.isFormValid.value)

        viewModel.updateCurrency("USD")
        // Now all required fields are filled
        assertTrue(viewModel.isFormValid.value)
    }

    @Test
    fun `form becomes invalid when a required field is cleared`() = testScope.runTest {
        // Set up a valid form
        val uri = mockk<Uri>()
        viewModel.updatePhotoUri(uri)
        viewModel.updateDate("2023-07-15")
        viewModel.updateTotal("100.00")
        viewModel.updateCurrency("USD")
        assertTrue(viewModel.isFormValid.value)

        // Clear a required field
        viewModel.updateTotal("")
        assertFalse(viewModel.isFormValid.value)
    }

    @Test
    fun `updateTotal handles decimal input correctly`() = testScope.runTest {
        viewModel.updateTotal("100.50")
        assertEquals("100.50", viewModel.total.value)
        assertNull(viewModel.totalError.value)

        viewModel.updateTotal("100.5")
        assertEquals("100.5", viewModel.total.value)
        assertNull(viewModel.totalError.value)

        viewModel.updateTotal("100.")
        assertEquals("100.", viewModel.total.value)
        assertNull(viewModel.totalError.value)

        viewModel.updateTotal(".5")
        assertEquals(".5", viewModel.total.value)
        assertNull(viewModel.totalError.value)
    }

    @Test
    fun `updateCurrency converts input to uppercase`() = testScope.runTest {
        viewModel.updateCurrency("usd")
        assertEquals("USD", viewModel.currency.value)
        assertNull(viewModel.currencyError.value)

        viewModel.updateCurrency("eUr")
        assertEquals("EUR", viewModel.currency.value)
        assertNull(viewModel.currencyError.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saveExpense uses current date if date field is empty`() = testScope.runTest {
        val uri = mockk<Uri>()
        viewModel.updatePhotoUri(uri)
        viewModel.updateTotal("100.00")
        viewModel.updateCurrency("USD")
        // Intentionally not setting the date

        var capturedExpense: Expense? = null
        coEvery { repository.insertExpense(any()) } answers {
            capturedExpense = firstArg()
        }

        viewModel.saveExpense()
        advanceUntilIdle()

        assertNotNull(capturedExpense)
        assertEquals(LocalDate.now(), capturedExpense!!.date)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saveExpense handles very large amounts`() = testScope.runTest {
        val uri = mockk<Uri>()
        viewModel.updatePhotoUri(uri)
        viewModel.updateDate("2023-07-15")
        viewModel.updateTotal("1000000000.00")
        viewModel.updateCurrency("USD")

        var capturedExpense: Expense? = null
        coEvery { repository.insertExpense(any()) } answers {
            capturedExpense = firstArg()
        }

        viewModel.saveExpense()
        advanceUntilIdle()

        assertNotNull(capturedExpense)
        assertEquals(1000000000.00, capturedExpense!!.total, 0.001)
    }

    @Test
    fun `form validation handles edge case of zero total`() = testScope.runTest {
        viewModel.updateTotal("0.00")
        assertEquals("Total must be greater than zero", viewModel.totalError.value)
        assertFalse(viewModel.isFormValid.value)

        viewModel.updateTotal("0")
        assertEquals("Total must be greater than zero", viewModel.totalError.value)
        assertFalse(viewModel.isFormValid.value)
    }
}