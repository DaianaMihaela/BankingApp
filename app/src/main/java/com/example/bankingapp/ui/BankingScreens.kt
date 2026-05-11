package com.example.bankingapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankingapp.viewmodel.BankingViewModel
import com.example.bankingapp.data.model.Transaction

// Culori
val PrimaryColor = Color(0xFF6366F1)
val SecondaryColor = Color(0xFF4F46E5)
val BackgroundColor = Color(0xFFF1F5F9)

@Composable
fun RegisterScreen(onRegisterComplete: (String, String) -> Unit) {
    var nume by remember { mutableStateOf("") }
    var prenume by remember { mutableStateOf("") }
    var bankId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cont Nou", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryColor)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = nume, onValueChange = { nume = it }, label = { Text(text = "Nume") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = prenume, onValueChange = { prenume = it }, label = { Text(text = "Prenume") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = bankId, onValueChange = { if (it.length <= 6) bankId = it }, label = { Text(text = "ID Contract") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = pin, onValueChange = { if (it.length <= 4) pin = it }, label = { Text(text = "PIN") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))
        Button(onClick = { if(bankId.length == 6 && pin.length == 4) onRegisterComplete(prenume + " " + nume, pin) }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text(text = "Finalizează")
        }
    }
}

@Composable
fun LoginScreen(savedPin: String, userName: String, onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Salut,", fontSize = 16.sp, color = Color.Gray)
        Text(text = userName.toString(), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(
            value = enteredPin,
            onValueChange = {
                if (it.length <= 4) {
                    enteredPin = it
                    if (it == savedPin) onLoginSuccess()
                    else if (it.length == 4) isError = true
                }
            },
            label = { Text(text = "PIN") },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )
        if (isError) Text(text = "Incorect", color = Color.Red)
        Spacer(Modifier.height(40.dp))
        TextButton(onClick = onGoToRegister) {
            Text(text = "Creează cont nou", color = PrimaryColor)
        }
    }
}

@Composable
fun HomeScreen(viewModel: BankingViewModel, userName: String) {
    var showPayDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text(text = "Home") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.SwapHoriz, null) }, label = { Text(text = "Plăți") }, selected = false, onClick = { showPayDialog = true })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(padding).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(50.dp).clip(CircleShape).background(PrimaryColor), contentAlignment = Alignment.Center) {
                    // REPARAT LINIA 91: Concatenare cu String gol pentru a forța tipul String
                    val litera = "" + (if (userName.isNotEmpty()) userName[0] else "?")
                    Text(text = litera, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Text(text = userName.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Box(Modifier.fillMaxSize().background(PrimaryColor).padding(24.dp)) {
                    Column {
                        Text(text = "Sold disponibil", color = Color.White.copy(alpha = 0.7f))
                        // REPARAT LINIA 106: Conversie forțată prin String.format sau concatenare
                        val valoareSold = "" + viewModel.balance.value + " RON"
                        Text(text = valoareSold, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(text = "Tranzacții", fontWeight = FontWeight.Bold)

            LazyColumn {
                items(viewModel.transactions) { tx ->
                    ListItem(
                        headlineContent = { Text(text = tx.receiverName.toString()) },
                        trailingContent = {
                            val sumaText = "-" + tx.amount + " RON"
                            Text(text = sumaText, color = Color.Red)
                        }
                    )
                }
            }
        }
    }

    if (showPayDialog) {
        var r by remember { mutableStateOf("") }
        var a by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPayDialog = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.addTransaction(r, a.toDoubleOrNull() ?: 0.0)
                    showPayDialog = false
                }) { Text(text = "Trimite") }
            },
            title = { Text(text = "Plată") },
            text = {
                Column {
                    OutlinedTextField(value = r, onValueChange = { r = it }, label = { Text(text = "Către") })
                    OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text(text = "Sumă") })
                }
            }
        )
    }
}
