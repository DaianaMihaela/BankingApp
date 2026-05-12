package com.example.bankingapp.data.model

data class BillResponse(
    val id: Int,
    val provider: String,
    var amount: Double,
    var isPaid: Boolean = false
)