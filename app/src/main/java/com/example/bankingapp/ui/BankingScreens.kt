package com.example.bankingapp.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager // Importat pentru copiere
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString // Necesar pentru Clipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankingapp.R
import com.example.bankingapp.data.model.User
import com.example.bankingapp.data.repository.BillsApi
import com.example.bankingapp.data.repository.CurrencyRepository
import java.util.Locale

val PrimaryColor = Color(0xFF1A3B8E)
val SaveColor = Color(0xFF4CAF50)

@Composable
fun StartScreen(onRegister: () -> Unit, onExisting: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        Icon(Icons.Default.AccountBalance, null, Modifier.size(80.dp), PrimaryColor)
        Spacer(Modifier.height(40.dp))
        Button(onClick = onRegister, Modifier.fillMaxWidth().height(56.dp)) { Text("Cont Nou") }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onExisting, Modifier.fillMaxWidth().height(56.dp)) { Text("Logare ID") }
    }
}

@Composable
fun RegisterScreen(onRegisterComplete: (User) -> Unit) {
    var nume by remember { mutableStateOf("") }
    var idAles by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var showHelpDialog by remember { mutableStateOf(false) }
    val ibanFinal = remember { "RO06DAE" + (100000..999999).random().toString() }

    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        Text("Înregistrare", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        OutlinedTextField(value = nume, onValueChange = { nume = it }, label = { Text("Nume Complet") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = idAles,
            onValueChange = { if(it.length <= 6) idAles = it },
            label = { Text("ID Logare (6 cifre)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = { IconButton(onClick = { showHelpDialog = true }) { Icon(Icons.Default.Info, null, tint = PrimaryColor) } }
        )
        OutlinedTextField(value = pin, onValueChange = { if(it.length <= 4) pin = it }, label = { Text("PIN (4 cifre)") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(Modifier.height(30.dp))
        Button(onClick = { if(nume.isNotEmpty() && idAles.length == 6) onRegisterComplete(User(idAles, ibanFinal, nume, pin, 1000.0)) }, Modifier.fillMaxWidth().height(56.dp)) { Text("Finalizează") }
    }
    if (showHelpDialog) {
        AlertDialog(onDismissRequest = { showHelpDialog = false }, confirmButton = { TextButton(onClick = { showHelpDialog = false }) { Text("Am înțeles") } },
            title = { Text("Unde e ID-ul?") },
            text = { Column { Text("ID-ul se află pe contractul tău."); Spacer(Modifier.height(8.dp)); Image(painterResource(R.drawable.id), null, Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp))) } }
        )
    }
}

@Composable
fun LoginExistingScreen(onLogin: (String, String) -> Unit) {
    var idIn by remember { mutableStateOf("") }; var pinIn by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        Text("Autentificare", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = idIn, onValueChange = { idIn = it }, label = { Text("ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pinIn, onValueChange = { pinIn = it }, label = { Text("PIN") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(32.dp))
        Button(onClick = { onLogin(idIn, pinIn) }, Modifier.fillMaxWidth().height(56.dp)) { Text("Intră") }
    }
}

@Composable
fun PinLoginScreen(userName: String, correctPin: String, onSuccess: () -> Unit, onSwitch: () -> Unit, onCreateNew: () -> Unit) {
    var pinIn by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(50.dp)); Text("Salut,", fontSize = 16.sp); Text(userName, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Spacer(Modifier.height(40.dp)); Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) { repeat(4) { i -> Box(Modifier.size(20.dp).background(if(i < pinIn.length) PrimaryColor else Color.LightGray, CircleShape)) } }
        Spacer(Modifier.height(50.dp)); val keys = listOf(listOf("1","2","3"), listOf("4","5","6"), listOf("7","8","9"), listOf("C","0","OK"))
        Column { keys.forEach { row -> Row { row.forEach { char -> Box(Modifier.size(85.dp).clickable { when(char) { "C" -> pinIn = ""; "OK" -> if(pinIn == correctPin) onSuccess() else pinIn = ""; else -> if(pinIn.length < 4) { pinIn += char; if(pinIn.length == 4 && pinIn == correctPin) onSuccess() } } }, Alignment.Center) { Text(char, fontSize = 24.sp) } } } } }
        Spacer(Modifier.weight(1f)); Row { TextButton(onClick = onSwitch) { Text("Alt cont") }; TextButton(onClick = onCreateNew) { Text("Nou") } }
    }
}

@Composable
fun HomeScreen(user: User, onMenu: () -> Unit, onLogout: () -> Unit, onTransfer: (String, Double) -> Unit, onDeposit: (Double) -> Unit, onWithdraw: (Double) -> Unit) {
    var showT by remember { mutableStateOf(false) }; var showS by remember { mutableStateOf(false) }; var isDep by remember { mutableStateOf(true) }
    var exchangeRate by remember { mutableStateOf(0.20) }

    LaunchedEffect(Unit) {
        exchangeRate = CurrencyRepository.getRonToEurRate()
    }

    // Obținem managerul pentru Clipboard
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = { FloatingActionButton(onClick = { showT = true }, containerColor = PrimaryColor) { Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White) } }
    ) { p ->
        LazyColumn(Modifier.padding(p).padding(20.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    IconButton(onClick = onMenu) { Icon(Icons.Default.Menu, null, tint = PrimaryColor) }
                    Text(user.name, fontWeight = FontWeight.Bold); IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) }
                }
                Card(Modifier.fillMaxWidth().height(180.dp).padding(vertical = 10.dp), colors = CardDefaults.cardColors(containerColor = PrimaryColor)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Sold Curent", color = Color.White.copy(0.7f)); Text(String.format(Locale.getDefault(), "%.2f RON", user.balance), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text(String.format(Locale.getDefault(), "≈ %.2f EUR", user.balance * exchangeRate), color = Color.White.copy(0.9f), fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))

                        // ROW pentru IBAN + BUTON COPIERE
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("IBAN: ${user.iban}", color = Color.White, fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copiază IBAN",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(user.iban))
                                        // Feedback vizual opțional ar putea fi adăugat aici (Toast/Snackbar)
                                    }
                            )
                        }
                    }
                }
                Card(Modifier.fillMaxWidth().padding(top = 10.dp), colors = CardDefaults.cardColors(containerColor = SaveColor)) {
                    Column(Modifier.padding(15.dp)) {
                        Text("Pușculiță", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(String.format(Locale.getDefault(), "%.2f RON", user.savingsBalance), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(String.format(Locale.getDefault(), "≈ %.2f EUR", user.savingsBalance * exchangeRate), color = Color.White.copy(0.8f), fontSize = 16.sp)
                        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { isDep = true; showS = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) { Text("Depune", color = SaveColor) }
                            Button(onClick = { isDep = false; showS = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) { Text("Retrage", color = SaveColor) }
                        }
                    }
                }
            }
        }
    }
    // Dialogurile rămân neschimbate
    if(showT) {
        var iban by remember { mutableStateOf("") }; var sum by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = {showT=false}, title = {Text("Transfer Extern")}, text = {Column{OutlinedTextField(value=iban, onValueChange={iban=it}, label={Text("IBAN")}); OutlinedTextField(value=sum, onValueChange={sum=it}, label={Text("Suma")}, keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))}}, confirmButton = {Button(onClick={onTransfer(iban, sum.toDoubleOrNull()?:0.0); showT=false}){Text("Trimite")}})
    }
    if(showS) {
        var sumS by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = {showS=false}, title = {Text(if(isDep)"Depunere" else "Retragere")}, text = {OutlinedTextField(value=sumS, onValueChange={sumS=it}, label={Text("Suma")}, keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))}, confirmButton = {Button(onClick={if(isDep) onDeposit(sumS.toDoubleOrNull()?:0.0) else onWithdraw(sumS.toDoubleOrNull()?:0.0); showS=false}){Text("Confirmă")}})
    }
}

// Restul ecranelor (Bills, History) rămân la fel ca în codul tău original...
@Composable
fun BillsScreen(user: User, onPay: (Double, String, Boolean) -> Unit) {
    val bills = BillsApi.fetchBills()
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Facturi", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor); Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(bills) { bill ->
                Card(Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = if (bill.isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3F3)), border = BorderStroke(1.dp, if (bill.isPaid) SaveColor else Color.Red)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column { Text(bill.provider, fontWeight = FontWeight.Bold); Text(String.format(Locale.getDefault(), "%.2f RON", bill.amount), color = if (bill.isPaid) SaveColor else Color.Red, fontWeight = FontWeight.Bold) }
                        if (!bill.isPaid) { Button(onClick = { if (user.balance >= bill.amount) { BillsApi.payBill(bill.id); onPay(bill.amount, bill.provider, true) } else onPay(bill.amount, bill.provider, false) }) { Text("Plătește") } } else Icon(Icons.Default.CheckCircle, null, tint = SaveColor)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(hist: List<Pair<String, String>>) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Istoric", fontSize = 24.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(20.dp))
        LazyColumn {
            items(hist) { (t, v) ->
                ListItem(headlineContent = { Text(t) }, trailingContent = { Text(v, color = if(v.contains("+")) SaveColor else if(v.contains("EȘUAT")) Color.Red else Color.Black, fontWeight = FontWeight.Bold) }, leadingContent = { Icon(if(v.contains("EȘUAT")) Icons.Default.Error else Icons.Default.History, null, tint = if(v.contains("EȘUAT")) Color.Red else PrimaryColor) })
                HorizontalDivider()
            }
        }
    }
}