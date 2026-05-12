package com.example.bankingapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankingapp.R
import com.example.bankingapp.data.model.User
import kotlin.random.Random

val PrimaryColor = Color(0xFF1A3B8E)

@Composable
fun StartScreen(onRegister: () -> Unit, onExisting: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.AccountBalance, null, Modifier.size(80.dp), PrimaryColor)
        Spacer(Modifier.height(40.dp))
        Button(onClick = onRegister, Modifier.fillMaxWidth().height(56.dp)) { Text("Deschide Cont Nou") }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onExisting, Modifier.fillMaxWidth().height(56.dp)) { Text("Am deja un ID de logare") }
    }
}

@Composable
fun RegisterScreen(onRegisterComplete: (User) -> Unit) {
    var nume by remember { mutableStateOf("") }
    var idAles by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var showHelpImage by remember { mutableStateOf(false) }

    val generatedIban = remember {
        "RO06DAE" + (1..7).map { Random.nextInt(0, 10) }.joinToString("")
    }

    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        Text("Creează Cont", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(value = nume, onValueChange = { nume = it }, label = { Text("Nume și Prenume") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = idAles,
            onValueChange = { if(it.length <= 6) idAles = it },
            label = { Text("ID Logare (6 cifre)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                IconButton(onClick = { showHelpImage = true }) {
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = PrimaryColor)
                }
            }
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { if(it.length <= 4) pin = it },
            label = { Text("Setează PIN (4 cifre)") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { if(nume.isNotEmpty() && idAles.length == 6 && pin.length == 4) onRegisterComplete(User(idAles, generatedIban, nume, pin, 1000.0)) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Finalizează Înregistrarea")
        }
    }

    if (showHelpImage) {
        AlertDialog(
            onDismissRequest = { showHelpImage = false },
            title = { Text("Unde găsești ID-ul?") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ID-ul se află în colțul contractului.", fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.id),
                        contentDescription = "Help Photo",
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp))
                    )
                }
            },
            confirmButton = { TextButton(onClick = { showHelpImage = false }) { Text("Am înțeles") } }
        )
    }
}

@Composable
fun LoginExistingScreen(onLogin: (String, String) -> Unit) {
    var idInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center) {
        Text("Autentificare", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = idInput, onValueChange = { idInput = it }, label = { Text("Introdu ID-ul") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = pinInput, onValueChange = { pinInput = it }, label = { Text("Introdu PIN") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Spacer(Modifier.height(32.dp))
        Button(onClick = { onLogin(idInput, pinInput) }, Modifier.fillMaxWidth().height(56.dp)) { Text("Conectare") }
    }
}

@Composable
fun PinLoginScreen(userName: String, correctPin: String, onSuccess: () -> Unit, onSwitch: () -> Unit, onCreateNew: () -> Unit) {
    var pinInput by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(50.dp))
        Text("Salut,", fontSize = 16.sp); Text(userName, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Spacer(Modifier.height(40.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            repeat(4) { i -> Box(Modifier.size(16.dp).background(if(i < pinInput.length) PrimaryColor else Color.LightGray, CircleShape)) }
        }
        Spacer(Modifier.height(50.dp))
        val keys = listOf(listOf("1","2","3"), listOf("4","5","6"), listOf("7","8","9"), listOf("C","0","OK"))
        Column {
            keys.forEach { row ->
                Row {
                    row.forEach { char ->
                        Box(Modifier.size(85.dp).clickable {
                            when(char) {
                                "C" -> pinInput = ""
                                "OK" -> if(pinInput == correctPin) onSuccess() else pinInput = ""
                                else -> if(pinInput.length < 4) {
                                    pinInput += char
                                    if(pinInput.length == 4 && pinInput == correctPin) onSuccess()
                                }
                            }
                        }, contentAlignment = Alignment.Center) { Text(char, fontSize = 24.sp, fontWeight = FontWeight.Medium) }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row { TextButton(onClick = onSwitch) { Text("Schimbă contul") }; TextButton(onClick = onCreateNew) { Text("Cont nou") } }
    }
}

@Composable
fun HomeScreen(user: User, onLogout: () -> Unit, onTransfer: (String, Double) -> Unit) {
    var showPay by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = { showPay = true }, containerColor = PrimaryColor) { Icon(Icons.Default.Send, null, tint = Color.White) } }
    ) { p ->
        Column(Modifier.padding(p).padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                IconButton(onClick = onLogout) { Icon(Icons.Default.LockOpen, null, tint = PrimaryColor) }
            }
            Card(Modifier.fillMaxWidth().height(180.dp).padding(vertical = 15.dp), colors = CardDefaults.cardColors(containerColor = PrimaryColor)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Sold curent", color = Color.White.copy(0.7f))
                    Text("${user.balance} RON", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(20.dp))
                    Text("IBAN PENTRU TRANSFERURI:", color = Color.White.copy(0.6f), fontSize = 10.sp)
                    Text(user.iban, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
    if(showPay) {
        var tIban by remember { mutableStateOf("") }
        var tAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPay = false },
            title = { Text("Transfer rapid") },
            text = {
                Column {
                    OutlinedTextField(value = tIban, onValueChange = { tIban = it }, label = { Text("IBAN Destinatar") })
                    OutlinedTextField(value = tAmount, onValueChange = { tAmount = it }, label = { Text("Sumă") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = { Button(onClick = { onTransfer(tIban, tAmount.toDoubleOrNull() ?: 0.0); showPay = false }) { Text("Trimite") } }
        )
    }
}