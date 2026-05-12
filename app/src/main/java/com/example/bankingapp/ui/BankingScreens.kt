package com.example.bankingapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.bankingapp.data.model.SavingsAccount
import kotlin.random.Random

val PrimaryColor = Color(0xFF1A3B8E)
val SaveColor = Color(0xFF4CAF50)

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
        Spacer(Modifier.height(12.dp))
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
        Text("Salut,", fontSize = 16.sp)
        Text(userName, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
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
fun HomeScreen(
    user: User,
    savingsAccount: SavingsAccount?,
    onLogout: () -> Unit,
    onTransferToOther: (String, Double) -> Unit,
    onTransferToSavings: (Double) -> Unit,
    onTransferFromSavings: (Double) -> Unit
) {
    var showTransferMenu by remember { mutableStateOf(false) }
    var showExternalTransfer by remember { mutableStateOf(false) }
    var showSavingsTransfer by remember { mutableStateOf(false) }
    var showWithdraw by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showTransferMenu = true }, containerColor = PrimaryColor) {
                Icon(Icons.Default.Send, null, tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues).padding(20.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    IconButton(onClick = onLogout) { Icon(Icons.Default.LockOpen, null, tint = PrimaryColor) }
                }
            }

            item {
                Card(Modifier.fillMaxWidth().height(200.dp).padding(vertical = 15.dp), colors = CardDefaults.cardColors(containerColor = PrimaryColor)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Sold Cont Curent", color = Color.White.copy(0.7f), fontSize = 12.sp)
                        Text("${String.format("%.2f", user.balance)} RON", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(15.dp))
                        Text("IBAN: ${user.iban}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            if (savingsAccount != null) {
                item {
                    Card(Modifier.fillMaxWidth().height(180.dp), colors = CardDefaults.cardColors(containerColor = SaveColor)) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Pușculiță", color = Color.White.copy(0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(10.dp))
                            Text("${String.format("%.2f", savingsAccount.balance)} RON", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { showSavingsTransfer = true },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                ) {
                                    Text("Depune", color = SaveColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { showWithdraw = true },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                ) {
                                    Text("Retrage", color = SaveColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTransferMenu) {
        AlertDialog(
            onDismissRequest = { showTransferMenu = false },
            title = { Text("Alege tip transfer") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { showTransferMenu = false; showExternalTransfer = true }, Modifier.fillMaxWidth()) {
                        Text("Transfer către alt cont")
                    }
                    if (savingsAccount != null) {
                        Button(onClick = { showTransferMenu = false; showSavingsTransfer = true }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SaveColor)) {
                            Text("Transfer către Pușculiță")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showExternalTransfer) {
        var tIban by remember { mutableStateOf("") }
        var tAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showExternalTransfer = false },
            title = { Text("Transfer rapid") },
            text = {
                Column {
                    OutlinedTextField(value = tIban, onValueChange = { tIban = it }, label = { Text("IBAN Destinatar") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = tAmount, onValueChange = { tAmount = it }, label = { Text("Sumă (RON)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                }
            },
            confirmButton = {
                Button(onClick = {
                    onTransferToOther(tIban, tAmount.toDoubleOrNull() ?: 0.0)
                    showExternalTransfer = false
                }) {
                    Text("Trimite")
                }
            }
        )
    }

    if (showSavingsTransfer) {
        var sAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSavingsTransfer = false },
            title = { Text("Depune în Pușculiță") },
            text = {
                Column {
                    Text("Soldul curent: ${String.format("%.2f", user.balance)} RON", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = sAmount, onValueChange = { sAmount = it }, label = { Text("Sumă de depus (RON)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                }
            },
            confirmButton = {
                Button(onClick = {
                    onTransferToSavings(sAmount.toDoubleOrNull() ?: 0.0)
                    showSavingsTransfer = false
                }) {
                    Text("Depune")
                }
            }
        )
    }

    if (showWithdraw) {
        var wAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showWithdraw = false },
            title = { Text("Retrage din Pușculiță") },
            text = {
                Column {
                    Text("Disponibil: ${String.format("%.2f", savingsAccount?.balance ?: 0.0)} RON", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = wAmount, onValueChange = { wAmount = it }, label = { Text("Sumă de retras (RON)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                }
            },
            confirmButton = {
                Button(onClick = {
                    onTransferFromSavings(wAmount.toDoubleOrNull() ?: 0.0)
                    showWithdraw = false
                }) {
                    Text("Retrage")
                }
            }
        )
    }
}
