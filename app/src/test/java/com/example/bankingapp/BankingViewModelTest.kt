package com.example.bankingapp

import com.example.bankingapp.viewmodel.BankingViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class BankingViewModelTest {

    @Test
    fun `verifică dacă soldul scade corect după o tranzacție`() {
        // 1. Pregătim terenul (GIVEN)
        val viewModel = BankingViewModel()
        val sumaDePlata = 500.0
        val soldInitial = 5000.0

        // 2. Executăm acțiunea (WHEN)
        viewModel.addTransaction("Chirie", sumaDePlata)

        // 3. Verificăm rezultatul (THEN)
        val soldAsteptat = soldInitial - sumaDePlata
        assertEquals(soldAsteptat, viewModel.balance.value, 0.1)
    }
}