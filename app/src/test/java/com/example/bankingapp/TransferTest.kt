package com.example.bankingapp

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import com.example.bankingapp.viewmodel.BankingViewModel

class TransferTest {
    private lateinit var viewModel: BankingViewModel

    @Before
    fun setUp() {
        viewModel = BankingViewModel()
    }

    @Test
    fun testTransferValidToPiggyBank() {
        val initialBalance = viewModel.balance.value
        val transferAmount = 200.0
        val piggyBankName = "Pușculiță"

        viewModel.addTransaction(piggyBankName, transferAmount)

        val expectedBalance = initialBalance - transferAmount
        assertEquals(expectedBalance, viewModel.balance.value, 0.01)
        assertEquals(1, viewModel.transactions.size)
        assertEquals(piggyBankName, viewModel.transactions[0].receiverName)
        assertEquals(transferAmount, viewModel.transactions[0].amount, 0.01)
    }


    @Test
    fun testBalanceDecreasesAfterTransfer() {
        val initialBalance = viewModel.balance.value
        val transferAmount = 500.0

        viewModel.addTransaction("Pușculiță", transferAmount)

        val actualBalance = viewModel.balance.value
        val expectedBalance = initialBalance - transferAmount

        assertEquals(expectedBalance, actualBalance, 0.01)
        assertTrue(actualBalance < initialBalance)
    }


    @Test
    fun testTransactionAppearsInHistory() {
        val transferAmount = 100.0
        val piggyBankName = "Pușculiță"

        assertTrue(viewModel.transactions.isEmpty())

        viewModel.addTransaction(piggyBankName, transferAmount)

        assertEquals(1, viewModel.transactions.size)
        val transaction = viewModel.transactions[0]
        assertEquals(piggyBankName, transaction.receiverName)
        assertEquals(transferAmount, transaction.amount, 0.01)
        assertEquals("Azi", transaction.date)
    }


    @Test
    fun testTransferRejectedWhenAmountExceedsBalance() {
        val initialBalance = viewModel.balance.value
        val excessiveAmount = 2000.0

        viewModel.addTransaction("Pușculiță", excessiveAmount)

        assertEquals(initialBalance, viewModel.balance.value, 0.01)
        assertEquals(0, viewModel.transactions.size)
    }

    @Test
    fun testTransferRejectedWithNegativeAmount() {
        val initialBalance = viewModel.balance.value
        val negativeAmount = -100.0

        viewModel.addTransaction("Pușculiță", negativeAmount)

        assertEquals(initialBalance, viewModel.balance.value, 0.01)
        assertEquals(0, viewModel.transactions.size)
    }


    @Test
    fun testTransferRejectedWithZeroAmount() {
        val initialBalance = viewModel.balance.value

        viewModel.addTransaction("Pușculiță", 0.0)

        assertEquals(initialBalance, viewModel.balance.value, 0.01)
        assertEquals(0, viewModel.transactions.size)
    }


    @Test
    fun testTransferAllAvailableFunds() {
        val initialBalance = viewModel.balance.value

        viewModel.addTransaction("Pușculiță", initialBalance)

        assertEquals(0.0, viewModel.balance.value, 0.01)
        assertEquals(1, viewModel.transactions.size)
    }


    @Test
    fun testTransactionOrderInHistory() {
        viewModel.addTransaction("Pușculiță", 100.0)
        viewModel.addTransaction("Pușculiță", 200.0)
        viewModel.addTransaction("Pușculiță", 300.0)

        assertEquals(3, viewModel.transactions.size)
        assertEquals(300.0, viewModel.transactions[0].amount, 0.01) // Cea mai recentă
        assertEquals(200.0, viewModel.transactions[1].amount, 0.01)
        assertEquals(100.0, viewModel.transactions[2].amount, 0.01) // Cea mai veche
    }


    @Test
    fun testCumulativeTransferAmount() {
        val initialBalance = viewModel.balance.value
        val transfer1 = 200.0
        val transfer2 = 300.0
        val transfer3 = 400.0
        val totalTransferred = transfer1 + transfer2 + transfer3

        viewModel.addTransaction("Pușculiță", transfer1)
        viewModel.addTransaction("Pușculiță", transfer2)
        viewModel.addTransaction("Pușculiță", transfer3)

        val expectedBalance = initialBalance - totalTransferred
        assertEquals(expectedBalance, viewModel.balance.value, 0.01)
        assertEquals(3, viewModel.transactions.size)
    }


    @Test
    fun testCorrectBeneficiaryIdentification() {
        val piggyBankName = "Pușculiță"
        val otherBeneficiary = "Economii"

        viewModel.addTransaction(piggyBankName, 100.0)
        viewModel.addTransaction(otherBeneficiary, 50.0)

        assertEquals(2, viewModel.transactions.size)
        assertEquals(piggyBankName, viewModel.transactions[1].receiverName)
        assertEquals(otherBeneficiary, viewModel.transactions[0].receiverName)
    }
}
