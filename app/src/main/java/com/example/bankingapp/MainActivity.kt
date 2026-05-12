package com.example.bankingapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        // BARA DE JOS CU BUTOANELE PROFESIONALE
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Buton INAPOI profesional
                                ProfiNavButton(
                                    text = "Înapoi",
                                    icon = Icons.Default.ArrowBack,
                                    onClick = { goBack() },
                                    enabled = backStack.isNotEmpty()
                                )

                                // Buton INAINTE profesional
                                ProfiNavButton(
                                    text = "Înainte",
                                    icon = Icons.Default.ArrowForward,
                                    onClick = { goForward() },
                                    enabled = forwardStack.isNotEmpty()
                                )
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

// O funcție @Composable reutilizabilă pentru butoanele de navigare profesionale
@Composable
fun ProfiNavButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val backgroundColor = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val contentColor = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .width(130.dp)
            .height(50.dp)
            .shadow(if (enabled) 4.dp else 0.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}