package com.example.bankingapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.bankingapp.data.model.Transaction

class BankingViewModel : ViewModel() {
    var balance = mutableStateOf<Double>(1500.0)
    val transactions = mutableStateListOf<Transaction>()

    fun addTransaction(name: String, amount: Double) {
        if (amount > 0 && amount <= balance.value) {
            transactions.add(0, Transaction(name, amount, "Azi"))
            balance.value -= amount
        }
    }
}