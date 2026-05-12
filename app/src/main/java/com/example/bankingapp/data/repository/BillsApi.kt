package com.example.bankingapp.data.repository

import com.example.bankingapp.data.model.BillResponse
import kotlin.random.Random

object BillsApi {
    private var loginCounter = 0
    private var remoteDatabase = mutableListOf<BillResponse>()

    init {
        generateNewMonthBills()
    }

    private fun generateNewMonthBills() {
        remoteDatabase = mutableListOf(
            BillResponse(1, "Digi", 70.0),
            BillResponse(2, "Orange", 50.0),
            BillResponse(3, "Netflix", 120.0),
            BillResponse(4, "Engie", Random.nextInt(200, 501).toDouble()),
            BillResponse(5, "Electrica", Random.nextInt(200, 501).toDouble()),
            BillResponse(6, "Apa Nova", Random.nextInt(200, 501).toDouble())
        )
    }
    fun fetchBills(): List<BillResponse> = remoteDatabase
    fun payBill(billId: Int): Boolean {
        val bill = remoteDatabase.find { it.id == billId }
        if (bill != null && !bill.isPaid) {
            bill.isPaid = true
            return true
        }
        return false
    }

    fun checkAndResetLogin(): Boolean {
        loginCounter++

        if (loginCounter >= 3) {
            loginCounter = 0
            generateNewMonthBills()
            return true
        }
        return false
    }
}