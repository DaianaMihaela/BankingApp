package com.example.bankingapp.data.repository

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object CurrencyRepository {
    private const val API_URL = "https://open.er-api.com/v6/latest/RON"
    private var cachedRate: Double = 0.20 // Valoare default (1 RON ~ 0.20 EUR)

    suspend fun getRonToEurRate(): Double {
        return withContext(Dispatchers.IO) {
            try {
                val response = URL(API_URL).readText()
                val json = Gson().fromJson(response, Map::class.java)
                val rates = json["rates"] as Map<String, Any>
                val eurRate = (rates["EUR"] as? Double) ?: 0.20
                cachedRate = eurRate
                eurRate
            } catch (e: Exception) {
                cachedRate
            }
        }
    }
}
