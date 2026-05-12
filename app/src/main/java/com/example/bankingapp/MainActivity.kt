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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankingapp.data.model.User
import com.example.bankingapp.data.repository.BillsApi
import com.example.bankingapp.ui.*
import com.example.bankingapp.ui.theme.BankingAppTheme
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BankingAppTheme {
                var currentScreen by remember { mutableStateOf("START") }
                val backStack = remember { mutableStateListOf<String>() }
                val forwardStack = remember { mutableStateListOf<String>() }
                val transactionsHistory = remember { mutableStateListOf<Pair<String, String>>() }

                var lastLoggedInUser by remember { mutableStateOf<User?>(null) }
                var targetUser by remember { mutableStateOf<User?>(null) }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val usersList = remember {
                    mutableStateListOf(User("123456", "RO06DAE111222", "Ion Popescu", "1234", 5000.0))
                }

                val navigateTo = { screen: String ->
                    if (currentScreen != screen) {
                        backStack.add(currentScreen)
                        forwardStack.clear()
                        currentScreen = screen
                    }
                }

                val handleLogin = { user: User ->
                    targetUser = user
                    lastLoggedInUser = user
                    if (BillsApi.checkAndResetLogin()) {
                        val idx = usersList.indexOfFirst { it.idDeLogare == user.idDeLogare }
                        if (idx != -1) {
                            usersList[idx] = usersList[idx].copy(balance = usersList[idx].balance + 5000.0)
                            targetUser = usersList[idx]
                            transactionsHistory.add(0, "Salariu" to "+5000.00 RON")
                        }
                    }
                    navigateTo("HOME")
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = currentScreen in listOf("HOME", "BILLS", "HISTORY"),
                    drawerContent = {
                        ModalDrawerSheet {
                            Text("Meniu", Modifier.padding(16.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            NavigationDrawerItem(label = { Text("Acasă") }, selected = currentScreen == "HOME", onClick = { scope.launch { drawerState.close() }; navigateTo("HOME") }, icon = { Icon(Icons.Default.Home, null) })
                            NavigationDrawerItem(label = { Text("Facturi") }, selected = currentScreen == "BILLS", onClick = { scope.launch { drawerState.close() }; navigateTo("BILLS") }, icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, null) })
                            NavigationDrawerItem(label = { Text("Istoric") }, selected = currentScreen == "HISTORY", onClick = { scope.launch { drawerState.close() }; navigateTo("HISTORY") }, icon = { Icon(Icons.Default.History, null) })
                        }
                    }
                ) {
                    Scaffold(
                        bottomBar = {
                            if (currentScreen != "START") {
                                Surface(Modifier.fillMaxWidth().height(80.dp).shadow(8.dp), color = MaterialTheme.colorScheme.surface) {
                                    Row(Modifier.fillMaxSize().padding(horizontal = 24.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                        ProfiNavButton("Înapoi", Icons.AutoMirrored.Filled.ArrowBack, {
                                            if (backStack.isNotEmpty()) {
                                                forwardStack.add(currentScreen)
                                                currentScreen = backStack.removeAt(backStack.size - 1)
                                            }
                                        }, backStack.isNotEmpty())
                                        ProfiNavButton("Înainte", Icons.AutoMirrored.Filled.ArrowForward, {
                                            if (forwardStack.isNotEmpty()) {
                                                backStack.add(currentScreen)
                                                currentScreen = forwardStack.removeAt(forwardStack.size - 1)
                                            }
                                        }, forwardStack.isNotEmpty())
                                    }
                                }
                            }
                        }
                    ) { p ->
                        Box(Modifier.padding(p)) {
                            when (currentScreen) {
                                "START" -> {
                                    if (lastLoggedInUser != null) {
                                        targetUser = lastLoggedInUser
                                        currentScreen = "PIN_LOGIN"
                                    } else {
                                        StartScreen({ navigateTo("REGISTER") }, { navigateTo("LOGIN_EXISTING") })
                                    }
                                }
                                "REGISTER" -> RegisterScreen { u: User ->
                                    usersList.add(u); targetUser = u; lastLoggedInUser = u
                                    navigateTo("PIN_LOGIN")
                                }
                                "LOGIN_EXISTING" -> LoginExistingScreen { id, pin ->
                                    val u = usersList.find { it.idDeLogare == id && it.pin == pin }
                                    if (u != null) { targetUser = u; lastLoggedInUser = u; navigateTo("PIN_LOGIN") }
                                    else Toast.makeText(this@MainActivity, "Date incorecte!", Toast.LENGTH_SHORT).show()
                                }
                                "PIN_LOGIN" -> targetUser?.let {
                                    PinLoginScreen(it.name, it.pin, { handleLogin(it) },
                                        { lastLoggedInUser = null; navigateTo("LOGIN_EXISTING") },
                                        { lastLoggedInUser = null; navigateTo("REGISTER") })
                                }
                                "HOME" -> targetUser?.let { activeUser ->
                                    HomeScreen(activeUser, { scope.launch { drawerState.open() } }, { navigateTo("START") },
                                        { iban, amount ->
                                            val expIdx = usersList.indexOfFirst { it.idDeLogare == activeUser.idDeLogare }
                                            if (expIdx != -1 && activeUser.balance >= amount) {
                                                usersList[expIdx] = usersList[expIdx].copy(balance = activeUser.balance - amount)
                                                targetUser = usersList[expIdx]
                                                transactionsHistory.add(0, "Transfer $iban" to "-${String.format(Locale.getDefault(), "%.2f", amount)} RON")
                                            }
                                        },
                                        { amount ->
                                            val idx = usersList.indexOfFirst { it.idDeLogare == activeUser.idDeLogare }
                                            if (idx != -1 && activeUser.balance >= amount) {
                                                usersList[idx] = usersList[idx].copy(balance = activeUser.balance - amount, savingsBalance = activeUser.savingsBalance + amount)
                                                targetUser = usersList[idx]
                                                transactionsHistory.add(0, "Spre Pușculiță" to "-${String.format(Locale.getDefault(), "%.2f", amount)} RON")
                                            }
                                        },
                                        { amount ->
                                            val idx = usersList.indexOfFirst { it.idDeLogare == activeUser.idDeLogare }
                                            if (idx != -1 && activeUser.savingsBalance >= amount) {
                                                usersList[idx] = usersList[idx].copy(balance = activeUser.balance + amount, savingsBalance = activeUser.savingsBalance - amount)
                                                targetUser = usersList[idx]
                                                transactionsHistory.add(0, "Din Pușculiță" to "+${String.format(Locale.getDefault(), "%.2f", amount)} RON")
                                            }
                                        }
                                    )
                                }
                                "BILLS" -> targetUser?.let { activeUser ->
                                    BillsScreen(activeUser) { amount, provider, success ->
                                        val idx = usersList.indexOfFirst { it.idDeLogare == activeUser.idDeLogare }
                                        if (idx != -1) {
                                            if (success) {
                                                usersList[idx] = usersList[idx].copy(balance = usersList[idx].balance - amount)
                                                targetUser = usersList[idx]
                                                transactionsHistory.add(0, "Plată $provider" to "-${String.format(Locale.getDefault(), "%.2f", amount)} RON")
                                            } else {
                                                transactionsHistory.add(0, "EȘUAT: $provider" to "Insuficient")
                                            }
                                        }
                                    }
                                }
                                "HISTORY" -> HistoryScreen(transactionsHistory)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfiNavButton(text: String, icon: ImageVector, onClick: () -> Unit, enabled: Boolean) {
    val col = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    Row(Modifier.width(130.dp).height(50.dp).clip(RoundedCornerShape(12.dp)).background(col).clickable(enabled) { onClick() }, Arrangement.Center, Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(text)
    }
}