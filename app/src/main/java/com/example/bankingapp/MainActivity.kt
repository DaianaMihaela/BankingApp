package com.example.bankingapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bankingapp.data.model.User
import com.example.bankingapp.data.model.SavingsAccount
import com.example.bankingapp.ui.*
import com.example.bankingapp.ui.theme.BankingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BankingAppTheme {
                // --- LOGICA DE ISTORIC ---
                var currentScreen by remember { mutableStateOf("START") }
                val backStack = remember { mutableStateListOf<String>() } // Ține minte paginile trecute
                val forwardStack = remember { mutableStateListOf<String>() } // Ține minte paginile pentru "Inainte"

                // Funcție pentru navigare normală (curăță forward stack când alegi o cale nouă)
                val navigateTo = { screen: String ->
                    if (currentScreen != screen) {
                        backStack.add(currentScreen)
                        forwardStack.clear()
                        currentScreen = screen
                    }
                }

                // Funcție pentru Înapoi
                val goBack = {
                    if (backStack.isNotEmpty()) {
                        forwardStack.add(currentScreen)
                        currentScreen = backStack.removeAt(backStack.size - 1)
                    }
                }

                // Funcție pentru Înainte
                val goForward = {
                    if (forwardStack.isNotEmpty()) {
                        backStack.add(currentScreen)
                        currentScreen = forwardStack.removeAt(forwardStack.size - 1)
                    }
                }
                // -------------------------

                val usersList = remember {
                    mutableStateListOf(
                        User("123456", "RO06DAE1234567", "Ion Popescu", "1234", 5000.0),
                        User("654321", "RO06DAE7654321", "Maria Ionescu", "4321", 3000.0)
                    )
                }

                var targetUser by remember { mutableStateOf<User?>(null) }
                var userSavingsAccount by remember { mutableStateOf<SavingsAccount?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // BARA DE JOS CU BUTOANELE PERMANENTE
                        BottomAppBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Buton INAPOI
                                Button(
                                    onClick = { goBack() },
                                    enabled = backStack.isNotEmpty() // Se dezactivează dacă nu avem unde să mergem înapoi
                                ) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Înapoi")
                                }

                                // Buton INAINTE
                                Button(
                                    onClick = { goForward() },
                                    enabled = forwardStack.isNotEmpty() // Se activează doar dacă am dat "Înapoi" anterior
                                ) {
                                    Text("Înainte")
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    // Conținutul ecranelor, ajustat cu padding pentru a nu fi acoperit de butoane
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        when (currentScreen) {
                            "START" -> StartScreen(
                                onRegister = { navigateTo("REGISTER") },
                                onExisting = { navigateTo("LOGIN_EXISTING") }
                            )

                            "REGISTER" -> RegisterScreen { newUser ->
                                usersList.add(newUser)
                                targetUser = newUser
                                userSavingsAccount = SavingsAccount("SAV-${newUser.idDeLogare}", 0.0, "Pușculiță")
                                navigateTo("PIN_LOGIN")
                            }

                            "LOGIN_EXISTING" -> LoginExistingScreen { id, pin ->
                                val user = usersList.find { it.idDeLogare == id && it.pin == pin }
                                if (user != null) {
                                    targetUser = user
                                    userSavingsAccount = SavingsAccount("SAV-${user.idDeLogare}", 0.0, "Pușculiță")
                                    navigateTo("PIN_LOGIN")
                                } else {
                                    Toast.makeText(this@MainActivity, "Eroare!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            "PIN_LOGIN" -> targetUser?.let { user ->
                                PinLoginScreen(
                                    userName = user.name,
                                    correctPin = user.pin,
                                    onSuccess = { navigateTo("HOME") },
                                    onSwitch = { navigateTo("LOGIN_EXISTING") },
                                    onCreateNew = { navigateTo("REGISTER") }
                                )
                            }

                            "HOME" -> targetUser?.let { activeUser ->
                                HomeScreen(
                                    user = activeUser,
                                    savingsAccount = userSavingsAccount,
                                    onLogout = { navigateTo("PIN_LOGIN") },
                                    onTransferToOther = { toIban, amount ->
                                        // Logica de transfer (păstrată din codul tău)
                                    },
                                    onTransferToSavings = { amount -> /* logica ta */ },
                                    onTransferFromSavings = { amount -> /* logica ta */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}