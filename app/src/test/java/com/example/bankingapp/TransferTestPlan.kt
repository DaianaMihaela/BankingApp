package com.example.bankingapp

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import com.example.bankingapp.viewmodel.BankingViewModel

class TransferTestPlan {
    private lateinit var viewModel: BankingViewModel

    @Before
    fun setUp() {
        viewModel = BankingViewModel()
    }

    /**
     * TC001: Transfer valid din cont curent în pușculiță
     * Descriere: Verifică că transferul din contul curent în pușculiță se realizează cu succes
     * Precondițiuni: Soldul inițial = 1500.0 RON
     * Pași:
     * 1. Se execută transfer de 200.0 RON în "Pușculiță"
     * 2. Se verifică că soldul a scăzut cu 200.0 RON
     * 3. Se verifică că tranzacția apare în istoric
     */
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

    /**
     * TC002: Soldul se actualizează corect după transfer
     * Descriere: Verific că soldul contului curent scade exact cu suma transferată
     * Precondițiuni: Soldul initial = 1500.0 RON
     * Pași:
     * 1. Soldul inițial se salvează
     * 2. Se execută transfer de 500.0 RON
     * 3. Se verifică că soldul nou = soldul inițial - 500.0
     */
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

    /**
     * TC003: Tranzacția apare în istoric
     * Descriere: Verific că după transfer, tranzacția apare în lista de tranzacții
     * Precondițiuni: Lista de tranzacții este goală
     * Pași:
     * 1. Se execută transfer de 100.0 RON
     * 2. Se verifică că lista de tranzacții conține 1 element
     * 3. Se verific detaliile tranzacției (beneficiar, sumă, dată)
     */
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

    /**
     * TC004: Transfer respinge suma mai mare decât soldul disponibil
     * Descriere: Verific că nu se poate transfera mai mult decât soldul disponibil
     * Precondițiuni: Soldul = 1500.0 RON
     * Pași:
     * 1. Se încearcă transfer de 2000.0 RON (mai mult decât soldul)
     * 2. Se verifică că soldul rămâne neschimbat
     * 3. Se verifică că tranzacția NU apare în istoric
     */
    @Test
    fun testTransferRejectedWhenAmountExceedsBalance() {
        val initialBalance = viewModel.balance.value
        val excessiveAmount = 2000.0

        viewModel.addTransaction("Pușculiță", excessiveAmount)

        assertEquals(initialBalance, viewModel.balance.value, 0.01)
        assertEquals(0, viewModel.transactions.size)
    }

    /**
     * TC005: Transfer cu sumă negativă este rejectat
     * Descriere: Verific că nu se acceptă transferuri cu sumă negativă
     * Precondițiuni: Soldul = 1500.0 RON
     * Pași:
     * 1. Se încearcă transfer cu sumă negativă (-100.0)
     * 2. Se verifică că soldul rămâne neschimbat
     * 3. Se verifică că tranzacția NU apare în istoric
     */
    @Test
    fun testTransferRejectedWithNegativeAmount() {
        val initialBalance = viewModel.balance.value
        val negativeAmount = -100.0

        viewModel.addTransaction("Pușculiță", negativeAmount)

        assertEquals(initialBalance, viewModel.balance.value, 0.01)
        assertEquals(0, viewModel.transactions.size)
    }

    /**
     * TC006: Transfer cu sumă zero este rejectat
     * Descriere: Verific că nu se acceptă transferuri cu sumă 0
     * Precondițiuni: Soldul = 1500.0 RON
     * Pași:
     * 1. Se încearcă transfer cu sumă 0
     * 2. Se verifică că soldul rămâne neschimbat
     * 3. Se verifică că tranzacția NU apare în istoric
     */
    @Test
    fun testTransferRejectedWithZeroAmount() {
        val initialBalance = viewModel.balance.value

        viewModel.addTransaction("Pușculiță", 0.0)

        assertEquals(initialBalance, viewModel.balance.value, 0.01)
        assertEquals(0, viewModel.transactions.size)
    }

    /**
     * TC007: Transfer de toate fondurile disponibile
     * Descriere: Verific că se poate transfera exact tot soldul disponibil
     * Precondițiuni: Soldul = 1500.0 RON
     * Pași:
     * 1. Se transferă exact 1500.0 RON
     * 2. Se verifică că soldul devine 0
     * 3. Se verifică că tranzacția apare în istoric
     */
    @Test
    fun testTransferAllAvailableFunds() {
        val initialBalance = viewModel.balance.value

        viewModel.addTransaction("Pușculiță", initialBalance)

        assertEquals(0.0, viewModel.balance.value, 0.01)
        assertEquals(1, viewModel.transactions.size)
    }

    /**
     * TC008: Ordine de tranzacții în istoric
     * Descriere: Verific că tranzacțiile noi apar la începutul listei (LIFO)
     * Precondițiuni: Liste de tranzacții este goală
     * Pași:
     * 1. Se execută 3 transferuri consecutive
     * 2. Se verifică că prima tranzacție din istoric este cea mai recentă
     */
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

    /**
     * TC009: Verificare suma cumulată a transferurilor
     * Descriere: Verific că suma tuturor transferurilor cumulate se deduce corect din soldul inițial
     * Precondițiuni: Soldul initial = 1500.0 RON
     * Pași:
     * 1. Se execută 3 transferuri: 200, 300, 400 RON
     * 2. Se calculează suma cumulată = 900 RON
     * 3. Se verifică că soldul final = 1500 - 900 = 600 RON
     */
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

    /**
     * TC010: Transfer cu beneficiar corect identificat
     * Descriere: Verific că "Pușculiță" este corect identificată ca beneficiar al transferului
     * Precondițiuni: N/A
     * Pași:
     * 1. Se execută transfer către "Pușculiță"
     * 2. Se verifică că receiverName = "Pușculiță"
     * 3. Se execută transfer către alt beneficiar și se verifică diferența
     */
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
