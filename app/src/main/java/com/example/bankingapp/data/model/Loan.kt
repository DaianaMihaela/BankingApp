package com.example.bankingapp.data.model

data class Loan(
    val id: String,
    val lenderName: String,
    val lenderIban: String,
    val borrowerName: String,
    val borrowerIban: String,
    val amount: Double,
    val interestRate: Double,
    val dueDate: String,
    var isRepaid: Boolean = false,
    val contractDate: String
)
