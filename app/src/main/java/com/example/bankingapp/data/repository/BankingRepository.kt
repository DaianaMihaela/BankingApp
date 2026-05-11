package com.example.bankingapp.data.repository

import com.example.bankingapp.data.model.Transaction

class BankingRepository {
    private val _transactions = mutableListOf<Transaction>()

    fun getTransactions(): List<Transaction> = _transactions

    fun addTransaction(transaction: Transaction) {
        _transactions.add(transaction)
    }

    fun getBalance(): Double {
        val totalSpent = _transactions.sumOf { it.amount }
        return 5000.0 - totalSpent
    }
}