package com.example.bankingapp.data.model

data class Transfer(
    val fromAccount: String,
    val toAccount: String,
    val amount: Double,
    val date: String,
    val status: String = "Completat"
)
