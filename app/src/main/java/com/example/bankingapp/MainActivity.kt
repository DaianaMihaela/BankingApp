package com.example.bankingapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.bankingapp.data.model.User
import com.example.bankingapp.ui.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("BankingData", Context.MODE_PRIVATE)
        val gson = Gson()

        setContent {
            val usersList = remember {
                val json = sharedPref.getString("users", "[]") ?: "[]"
                val type = object : TypeToken<MutableList<User>>() {}.type
                val decoded: MutableList<User> = gson.fromJson(json, type)
                mutableStateListOf<User>().apply { addAll(decoded) }
            }

            var lastUserId by remember { mutableStateOf(sharedPref.getString("last_id", "") ?: "") }
            var targetUser by remember { mutableStateOf(usersList.find { it.idDeLogare == lastUserId }) }
            var currentScreen by remember { mutableStateOf(if (lastUserId.isEmpty()) "START" else "PIN_LOGIN") }

            val saveAll = { sharedPref.edit().putString("users", gson.toJson(usersList.toList())).apply() }
            val setLastUser = { user: User ->
                sharedPref.edit().putString("last_id", user.idDeLogare).apply()
                lastUserId = user.idDeLogare
                targetUser = user
            }

            when (currentScreen) {
                "START" -> StartScreen(onRegister = { currentScreen = "REGISTER" }, onExisting = { currentScreen = "LOGIN_EXISTING" })
                "REGISTER" -> RegisterScreen { newUser -> usersList.add(newUser); saveAll(); setLastUser(newUser); currentScreen = "PIN_LOGIN" }
                "LOGIN_EXISTING" -> LoginExistingScreen { id, pin ->
                    val user = usersList.find { it.idDeLogare == id && it.pin == pin }
                    if (user != null) { setLastUser(user); currentScreen = "PIN_LOGIN" } else Toast.makeText(this, "Greșit!", Toast.LENGTH_SHORT).show()
                }
                "PIN_LOGIN" -> targetUser?.let { user ->
                    PinLoginScreen(user.name, user.pin, { currentScreen = "HOME" }, { currentScreen = "LOGIN_EXISTING" }, { currentScreen = "REGISTER" })
                }
                "HOME" -> targetUser?.let { activeUser ->
                    HomeScreen(activeUser, { currentScreen = "PIN_LOGIN" }, { toIban, amount ->
                        val receiver = usersList.find { it.iban == toIban }
                        if (receiver != null && activeUser.balance >= amount && amount > 0) {
                            val sIdx = usersList.indexOfFirst { it.iban == activeUser.iban }
                            val rIdx = usersList.indexOfFirst { it.iban == toIban }
                            usersList[sIdx] = usersList[sIdx].copy(balance = usersList[sIdx].balance - amount)
                            usersList[rIdx] = usersList[rIdx].copy(balance = usersList[rIdx].balance + amount)
                            targetUser = usersList[sIdx]; saveAll()
                            Toast.makeText(this, "Transfer reușit către ${receiver.name}!", Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(this, "Eroare!", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }
}