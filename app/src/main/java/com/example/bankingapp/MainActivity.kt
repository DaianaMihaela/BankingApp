package com.example.bankingapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.bankingapp.ui.HomeScreen
import com.example.bankingapp.ui.LoginScreen
import com.example.bankingapp.ui.RegisterScreen
import com.example.bankingapp.viewmodel.BankingViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BankingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("BankingPrefs", Context.MODE_PRIVATE)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {

                    var savedName by remember {
                        mutableStateOf(sharedPref.getString("user_name", "") ?: "")
                    }
                    var savedPin by remember {
                        mutableStateOf(sharedPref.getString("user_pin", "") ?: "")
                    }

                    var currentScreen by remember {
                        mutableStateOf(if (savedName.isEmpty()) "REGISTER" else "LOGIN")
                    }

                    when (currentScreen) {
                        "REGISTER" -> RegisterScreen { name, pin ->
                            sharedPref.edit().apply {
                                putString("user_name", name)
                                putString("user_pin", pin)
                                apply()
                            }
                            savedName = name
                            savedPin = pin
                            currentScreen = "LOGIN"
                        }

                        "LOGIN" -> LoginScreen(
                            savedPin = savedPin,
                            userName = savedName,
                            onLoginSuccess = { currentScreen = "MAIN" },
                            onGoToRegister = { currentScreen = "REGISTER" }
                        )

                        "MAIN" -> HomeScreen(
                            viewModel = viewModel,
                            userName = savedName,
                            onReset = {
                                sharedPref.edit().clear().apply()
                                savedName = ""
                                savedPin = ""
                                currentScreen = "REGISTER"
                            }
                        )
                    }
                }
            }
        }
    }
}