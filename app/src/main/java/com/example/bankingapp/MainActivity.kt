package com.example.bankingapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.bankingapp.data.model.User
import com.example.bankingapp.data.model.SavingsAccount
import com.example.bankingapp.ui.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val usersList = remember {
                mutableStateListOf(
                    User("123456", "RO06DAE1234567", "Ion Popescu", "1234", 5000.0),
                    User("654321", "RO06DAE7654321", "Maria Ionescu", "4321", 3000.0)
                )
            }

            var lastUserId by remember { mutableStateOf("") }
            var targetUser by remember { mutableStateOf<User?>(null) }
            var userSavingsAccount by remember { mutableStateOf<SavingsAccount?>(null) }
            var currentScreen by remember { mutableStateOf("START") }

            val setLastUser = { user: User ->
                lastUserId = user.idDeLogare
                targetUser = user
                userSavingsAccount = SavingsAccount(
                    accountId = "SAV-${user.idDeLogare}",
                    balance = 0.0,
                    accountName = "Pușculiță"
                )
            }

            val transferToSavings = { amount: Double ->
                if (targetUser != null && userSavingsAccount != null && targetUser!!.balance >= amount && amount > 0) {
                    val userIndex = usersList.indexOfFirst { it.idDeLogare == targetUser!!.idDeLogare }
                    if (userIndex != -1) {
                        usersList[userIndex] = usersList[userIndex].copy(balance = usersList[userIndex].balance - amount)
                        userSavingsAccount = userSavingsAccount!!.copy(balance = userSavingsAccount!!.balance + amount)
                        targetUser = usersList[userIndex]
                        Toast.makeText(this@MainActivity, "Transfer către Pușculiță reușit cu $amount RON!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Transfer nereușit!", Toast.LENGTH_SHORT).show()
                }
            }

            val transferFromSavings = { amount: Double ->
                if (targetUser != null && userSavingsAccount != null && userSavingsAccount!!.balance >= amount && amount > 0) {
                    val userIndex = usersList.indexOfFirst { it.idDeLogare == targetUser!!.idDeLogare }
                    if (userIndex != -1) {
                        usersList[userIndex] = usersList[userIndex].copy(balance = usersList[userIndex].balance + amount)
                        userSavingsAccount = userSavingsAccount!!.copy(balance = userSavingsAccount!!.balance - amount)
                        targetUser = usersList[userIndex]
                        Toast.makeText(this@MainActivity, "Retragere din Pușculiță reușit cu $amount RON!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Retragere nereușit!", Toast.LENGTH_SHORT).show()
                }
            }

            when (currentScreen) {
                "START" -> StartScreen(
                    onRegister = { currentScreen = "REGISTER" },
                    onExisting = { currentScreen = "LOGIN_EXISTING" }
                )

                "REGISTER" -> RegisterScreen { newUser ->
                    usersList.add(newUser)
                    setLastUser(newUser)
                    currentScreen = "PIN_LOGIN"
                }

                "LOGIN_EXISTING" -> LoginExistingScreen { id, pin ->
                    val user = usersList.find { it.idDeLogare == id && it.pin == pin }
                    if (user != null) {
                        setLastUser(user)
                        currentScreen = "PIN_LOGIN"
                    } else {
                        Toast.makeText(this@MainActivity, "Credențiale incorecte!", Toast.LENGTH_SHORT).show()
                    }
                }

                "PIN_LOGIN" -> targetUser?.let { user ->
                    PinLoginScreen(
                        userName = user.name,
                        correctPin = user.pin,
                        onSuccess = { currentScreen = "HOME" },
                        onSwitch = { currentScreen = "LOGIN_EXISTING" },
                        onCreateNew = { currentScreen = "REGISTER" }
                    )
                }

                "HOME" -> targetUser?.let { activeUser ->
                    HomeScreen(
                        user = activeUser,
                        savingsAccount = userSavingsAccount,
                        onLogout = { currentScreen = "PIN_LOGIN" },
                        onTransferToOther = { toIban, amount ->
                            val receiver = usersList.find { it.iban == toIban }
                            if (receiver != null && activeUser.balance >= amount && amount > 0) {
                                val sIdx = usersList.indexOfFirst { it.iban == activeUser.iban }
                                val rIdx = usersList.indexOfFirst { it.iban == toIban }
                                usersList[sIdx] = usersList[sIdx].copy(balance = usersList[sIdx].balance - amount)
                                usersList[rIdx] = usersList[rIdx].copy(balance = usersList[rIdx].balance + amount)
                                targetUser = usersList[sIdx]
                                Toast.makeText(this@MainActivity, "Transfer reușit către ${receiver.name}!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Transfer nereușit!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onTransferToSavings = { amount -> transferToSavings(amount) },
                        onTransferFromSavings = { amount -> transferFromSavings(amount) }
                    )
                }
            }
        }
    }
}
