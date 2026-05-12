package com.example.bankingapp.data.model

data class User(
    val idDeLogare: String,
    val iban: String,
    val name: String,
    val pin: String,
    var balance: Double,
    var savingsBalance: Double = 0.0
)