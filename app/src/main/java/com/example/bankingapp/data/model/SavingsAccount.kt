package com.example.bankingapp.data.model

data class SavingsAccount(
    val accountId: String,
    var balance: Double,
    val accountName: String = "Pușculiță"
)
