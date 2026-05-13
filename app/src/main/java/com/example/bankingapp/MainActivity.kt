package com.example.bankingapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankingapp.data.model.User
import com.example.bankingapp.data.model.Loan
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
                var currentScreen by remember { mutableStateOf("SPLASH") }
                val backStack = remember { mutableStateListOf<String>() }
                val forwardStack = remember { mutableStateListOf<String>() }
                val transactionsHistory = remember { mutableStateListOf<Pair<String, String>>() }
                val loans = remember { mutableStateListOf<Loan>() }

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
                            NavigationDrawerItem(label = { Text("Social Lending") }, selected = currentScreen == "LENDING", onClick = { scope.launch { drawerState.close() }; navigateTo("LENDING") }, icon = { Icon(Icons.Default.People, null) })
                        }
                    }
                ) {
                    Scaffold(
                        bottomBar = {
                            if (currentScreen != "START" && currentScreen != "SPLASH") {
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
                                "SPLASH" -> SplashScreen {
                                    currentScreen = if (lastLoggedInUser != null) {
                                        targetUser = lastLoggedInUser
                                        "PIN_LOGIN"
                                    } else {
                                        "START"
                                    }
                                }
                                "START" -> StartScreen({ navigateTo("REGISTER") }, { navigateTo("LOGIN_EXISTING") })
                                "REGISTER" -> RegisterScreen { u ->
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
                                "LENDING" -> targetUser?.let { activeUser ->
                                    // Simulare retragere automată la scadență
                                    LaunchedEffect(Unit) {
                                        loans.filter { !it.isRepaid && it.borrowerIban == activeUser.iban }.forEach { loan ->
                                            // Aici s-ar verifica data curentă față de loan.dueDate
                                            // Pentru demo, considerăm că dacă e activ, verificăm dacă putem trage banii
                                        }
                                    }

                                    SocialLendingScreen(
                                        activeUser,
                                        loans.filter { it.lenderIban == activeUser.iban || it.borrowerIban == activeUser.iban },
                                        { friendIban: String, amount: Double, interest: Double, dueDate: String ->
                                            val lenderIdx = usersList.indexOfFirst { it.idDeLogare == activeUser.idDeLogare }
                                            val borrowerIdx = usersList.indexOfFirst { it.iban == friendIban }
                                            
                                            if (lenderIdx != -1 && borrowerIdx != -1 && activeUser.balance >= amount) {
                                                usersList[lenderIdx] = usersList[lenderIdx].copy(balance = activeUser.balance - amount)
                                                usersList[borrowerIdx] = usersList[borrowerIdx].copy(balance = usersList[borrowerIdx].balance + amount)
                                                targetUser = usersList[lenderIdx]
                                                
                                                loans.add(Loan(
                                                    id = (1000..9999).random().toString(),
                                                    lenderName = activeUser.name,
                                                    lenderIban = activeUser.iban,
                                                    borrowerName = usersList[borrowerIdx].name,
                                                    borrowerIban = friendIban,
                                                    amount = amount,
                                                    interestRate = interest,
                                                    dueDate = dueDate,
                                                    contractDate = "15.11.2024"
                                                ))
                                                
                                                transactionsHistory.add(0, "Acordat împrumut către ${usersList[borrowerIdx].name}" to "-${String.format(Locale.getDefault(), "%.2f", amount)} RON")
                                            } else {
                                                Toast.makeText(this@MainActivity, "Eroare: IBAN incorect sau fonduri insuficiente!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        { loan: Loan ->
                                            val borrowerIdx = usersList.indexOfFirst { it.iban == loan.borrowerIban }
                                            val lenderIdx = usersList.indexOfFirst { it.iban == loan.lenderIban }
                                            
                                            if (borrowerIdx != -1 && lenderIdx != -1) {
                                                val totalRepay = loan.amount * (1 + loan.interestRate / 100)
                                                if (usersList[borrowerIdx].balance >= totalRepay) {
                                                    usersList[borrowerIdx] = usersList[borrowerIdx].copy(balance = usersList[borrowerIdx].balance - totalRepay)
                                                    usersList[lenderIdx] = usersList[lenderIdx].copy(balance = usersList[lenderIdx].balance + totalRepay)
                                                    
                                                    if (targetUser?.iban == loan.borrowerIban) targetUser = usersList[borrowerIdx]
                                                    if (targetUser?.iban == loan.lenderIban) targetUser = usersList[lenderIdx]
                                                    
                                                    val loanIdx = loans.indexOfFirst { it.id == loan.id }
                                                    if (loanIdx != -1) loans[loanIdx] = loans[loanIdx].copy(isRepaid = true)
                                                    
                                                    transactionsHistory.add(0, "Rambursare împrumut către ${loan.lenderName}" to "-${String.format(Locale.getDefault(), "%.2f", totalRepay)} RON")
                                                    transactionsHistory.add(0, "Încasare împrumut de la ${loan.borrowerName}" to "+${String.format(Locale.getDefault(), "%.2f", totalRepay)} RON")
                                                } else {
                                                    Toast.makeText(this@MainActivity, "Fonduri insuficiente pentru rambursare!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_dae),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("Atingeți ecranul", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfiNavButton(text: String, icon: ImageVector, onClick: () -> Unit, enabled: Boolean) {
    val col = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    Row(
        modifier = Modifier
            .width(130.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(col)
            .clickable(enabled) { onClick() },
        Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}