package com.example.bankingapp.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankingapp.R
import com.example.bankingapp.data.model.User
import com.example.bankingapp.data.model.Loan
import com.example.bankingapp.data.model.Template
import com.example.bankingapp.data.repository.BillsApi
import com.example.bankingapp.data.repository.CurrencyRepository
import com.example.bankingapp.ui.theme.*
import java.util.Locale

val PrimaryColor = CyanAccent
val SaveColor = Color(0xFF10B981)
val AccentPink = PinkAccent

@Composable
fun StartScreen(onRegister: () -> Unit, onExisting: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Text("Banking App", color = MaterialTheme.colorScheme.onBackground.copy(0.7f), fontSize = 18.sp)
        Spacer(Modifier.height(80.dp))
        
        Text("Hello!", color = PrimaryColor, fontSize = 64.sp, fontWeight = FontWeight.Bold)
        Box(Modifier.width(120.dp).height(4.dp).background(PrimaryColor))
        
        Spacer(Modifier.height(40.dp))
        Text(
            "Gestionează-ți finanțele cu stil și siguranță.",
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(Modifier.height(100.dp))
        
        Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("NEXT", fontWeight = FontWeight.Bold, color = Color.White)
        }
        
        Spacer(Modifier.height(16.dp))
        
        TextButton(onClick = onExisting) {
            Text("Ai deja cont? Loghează-te", color = PrimaryColor)
        }
    }
}

@Composable
fun RegisterScreen(onRegisterComplete: (User) -> Unit) {
    var nume by remember { mutableStateOf("") }
    var idAles by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var showHelpDialog by remember { mutableStateOf(false) }
    val ibanFinal = remember { "RO06DAE" + (100000..999999).random().toString() }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Creează cont", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Spacer(Modifier.height(24.dp))
        
        OutlinedTextField(
            value = nume,
            onValueChange = { nume = it },
            label = { Text("Nume Complet") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedBorderColor = PrimaryColor
            )
        )
        OutlinedTextField(
            value = idAles,
            onValueChange = { if(it.length <= 6) idAles = it },
            label = { Text("ID Logare (6 cifre)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = { IconButton(onClick = { showHelpDialog = true }) { Icon(Icons.Default.Info, null, tint = PrimaryColor) } },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onBackground, unfocusedTextColor = MaterialTheme.colorScheme.onBackground, focusedBorderColor = PrimaryColor)
        )
        OutlinedTextField(
            value = pin,
            onValueChange = { if(it.length <= 4) pin = it },
            label = { Text("PIN (4 cifre)") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onBackground, unfocusedTextColor = MaterialTheme.colorScheme.onBackground, focusedBorderColor = PrimaryColor)
        )
        
        Spacer(Modifier.height(40.dp))
        
        Button(
            onClick = { if(nume.isNotEmpty() && idAles.length == 6) onRegisterComplete(User(idAles, ibanFinal, nume, pin, 1000.0)) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("SIGN UP", fontWeight = FontWeight.Bold)
        }
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
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Autentificare", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = idIn, onValueChange = { idIn = it }, label = { Text("ID") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onBackground, unfocusedTextColor = MaterialTheme.colorScheme.onBackground))
        OutlinedTextField(value = pinIn, onValueChange = { pinIn = it }, label = { Text("PIN") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onBackground, unfocusedTextColor = MaterialTheme.colorScheme.onBackground))
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = { onLogin(idIn, pinIn) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("LOG IN", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PinLoginScreen(userName: String, correctPin: String, onSuccess: () -> Unit, onSwitch: () -> Unit, onCreateNew: () -> Unit) {
    var pinIn by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(50.dp)); Text("Salut,", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground.copy(0.7f)); Text(userName, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
        Spacer(Modifier.height(40.dp)); Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) { repeat(4) { i -> Box(Modifier.size(20.dp).background(if(i < pinIn.length) PrimaryColor else MaterialTheme.colorScheme.onBackground.copy(0.1f), CircleShape)) } }
        Spacer(Modifier.height(50.dp)); val keys = listOf(listOf("1","2","3"), listOf("4","5","6"), listOf("7","8","9"), listOf("C","0","OK"))
        Column { 
            keys.forEach { row -> 
                Row { 
                    row.forEach { char -> 
                        Box(
                            Modifier
                                .size(85.dp)
                                .clickable { 
                                    when(char) { 
                                        "C" -> pinIn = ""
                                        "OK" -> if(pinIn == correctPin) onSuccess() else pinIn = ""
                                        else -> if(pinIn.length < 4) { pinIn += char; if(pinIn.length == 4 && pinIn == correctPin) onSuccess() }
                                    } 
                                }, 
                            Alignment.Center
                        ) { 
                            Text(char, fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground) 
                        } 
                    } 
                } 
            } 
        }
        Spacer(Modifier.weight(1f)); Row { TextButton(onClick = onSwitch) { Text("Alt cont", color = PrimaryColor) }; TextButton(onClick = onCreateNew) { Text("Nou", color = PrimaryColor) } }
    }
}

@Composable
fun FlipCard(user: User, exchangeRate: Double) {
    var rotated by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(durationMillis = 600)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { rotated = !rotated }
    ) {
        if (rotation <= 90f) {
            // Front Side
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(colors = listOf(CardCyanStart, CardCyanEnd)))
                        .padding(24.dp)
                ) {
                    Column {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text("BANKING APP", color = Color.White.copy(0.8f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Icon(Icons.Default.Wifi, null, tint = Color.White.copy(0.6f), modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.height(20.dp))
                        Text(
                            String.format(Locale.getDefault(), "%.2f RON", user.balance),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            String.format(Locale.getDefault(), "≈ %.2f EUR", user.balance * exchangeRate),
                            color = Color.White.copy(0.8f),
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.weight(1f))
                        Text(user.name.uppercase(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text(user.iban.takeLast(10).chunked(4).joinToString(" "), color = Color.White.copy(0.7f), fontSize = 14.sp)
                    }
                }
            }
        } else {
            // Back Side
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(colors = listOf(CardCyanEnd, CardCyanStart)))
                        .padding(vertical = 24.dp)
                ) {
                    Column {
                        Box(Modifier.fillMaxWidth().height(40.dp).background(Color.Black.copy(0.8f)))
                        Spacer(Modifier.height(20.dp))
                        Row(Modifier.padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.width(150.dp).height(30.dp).background(Color.White.copy(0.9f))) {
                                Text("CVV: 123", color = Color.Black, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp), fontSize = 12.sp)
                            }
                            Spacer(Modifier.width(16.dp))
                            Text("EXP: 12/28", color = Color.White, fontSize = 14.sp)
                        }
                        Spacer(Modifier.weight(1f))
                        Text("Personal Info Hidden", color = Color.White.copy(0.5f), modifier = Modifier.padding(horizontal = 24.dp), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    user: User,
    isLightMode: Boolean,
    transactions: List<Pair<String, String>>,
    templates: List<Template>,
    onMenu: () -> Unit,
    onLogout: () -> Unit,
    onTransfer: (String, Double) -> Unit,
    onDeposit: (Double) -> Unit,
    onWithdraw: (Double) -> Unit
) {
    var showT by remember { mutableStateOf(false) }; var showS by remember { mutableStateOf(false) }; var isDep by remember { mutableStateOf(true) }
    var exchangeRate by remember { mutableStateOf(0.20) }

    LaunchedEffect(Unit) {
        exchangeRate = CurrencyRepository.getRonToEurRate()
    }

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = { 
            FloatingActionButton(onClick = { showT = true }, containerColor = PrimaryColor) { 
                Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.Black) 
            } 
        }
    ) { p ->
        LazyColumn(Modifier.padding(p).padding(horizontal = 20.dp)) {
            item {
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    IconButton(onClick = onMenu) { Icon(Icons.Default.Menu, null, tint = PrimaryColor) }
                    Text("Salut, ${user.name}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp)
                    IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = PrimaryColor) }
                }
                Spacer(Modifier.height(30.dp))
                
                FlipCard(user, exchangeRate)
                
                Spacer(Modifier.height(24.dp))
                
                // Savings Card
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.1f))
                ) {
                    Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Pușculiță", color = MaterialTheme.colorScheme.onSurface.copy(0.8f), fontWeight = FontWeight.Medium)
                            Text(
                                String.format(Locale.getDefault(), "%.2f RON", user.savingsBalance),
                                color = SaveColor,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier.size(44.dp).background(PrimaryColor, CircleShape).clickable { isDep = true; showS = true },
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.Add, null, tint = Color.Black) }
                            
                            Box(
                                Modifier.size(44.dp).background(AccentPink, CircleShape).clickable { isDep = false; showS = true },
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.Remove, null, tint = Color.White) }
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                Text("Tranzacții recente", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
            }
            
            // Render top 5 transactions
            items(transactions.take(5)) { (t, v) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.05f))
                ) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(t, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface) },
                        trailingContent = { Text(v, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if(v.contains("+")) SaveColor else if(v.contains("EȘUAT")) Color.Red else MaterialTheme.colorScheme.onSurface) },
                        leadingContent = { 
                            Box(Modifier.size(40.dp).background(PrimaryColor.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(if(v.contains("EȘUAT")) Icons.Default.Error else if(v.contains("+")) Icons.Default.Add else Icons.Default.History, null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
                            }
                        }
                    )
                }
            }
            
            if (transactions.isEmpty()) {
                item {
                    Text("Nu există tranzacții recente.", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                }
            }
        }
    }
    
    if(showT) {
        var iban by remember { mutableStateOf("") }; var sum by remember { mutableStateOf("") }
        var showTemplateDialog by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {showT=false}, 
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Transfer Extern", color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = { showTemplateDialog = true }) {
                        Icon(Icons.Default.Star, null, tint = PrimaryColor)
                    }
                }
            }, 
            text = {
                Column {
                    OutlinedTextField(value=iban, onValueChange={iban=it}, label={Text("IBAN")}, modifier=Modifier.fillMaxWidth(), colors=OutlinedTextFieldDefaults.colors(focusedTextColor=MaterialTheme.colorScheme.onSurface, unfocusedTextColor=MaterialTheme.colorScheme.onSurface))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value=sum, onValueChange={sum=it}, label={Text("Suma")}, keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number), modifier=Modifier.fillMaxWidth(), colors=OutlinedTextFieldDefaults.colors(focusedTextColor=MaterialTheme.colorScheme.onSurface, unfocusedTextColor=MaterialTheme.colorScheme.onSurface))
                }
            }, 
            confirmButton = { Button(onClick={onTransfer(iban, sum.toDoubleOrNull()?:0.0); showT=false}, colors=ButtonDefaults.buttonColors(containerColor=PrimaryColor)) { Text("Trimite", color=Color.Black) } }
        )

        if (showTemplateDialog) {
            AlertDialog(
                onDismissRequest = { showTemplateDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text("Alege un șablon") },
                text = {
                    LazyColumn {
                        items(templates) { template ->
                            ListItem(
                                modifier = Modifier.clickable { 
                                    iban = template.iban
                                    showTemplateDialog = false 
                                },
                                headlineContent = { Text(template.name) },
                                supportingContent = { Text(template.iban, fontSize = 12.sp) },
                                leadingContent = { Icon(Icons.Default.Person, null, tint = PrimaryColor) }
                            )
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showTemplateDialog = false }) { Text("Anulează") } }
            )
        }
    }
    if(showS) {
        var sumS by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {showS=false}, 
            containerColor = MaterialTheme.colorScheme.surface,
            title = {Text(if(isDep)"Depunere" else "Retragere", color=MaterialTheme.colorScheme.onSurface)}, 
            text = { OutlinedTextField(value=sumS, onValueChange={sumS=it}, label={Text("Suma")}, keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number), modifier=Modifier.fillMaxWidth(), colors=OutlinedTextFieldDefaults.colors(focusedTextColor=MaterialTheme.colorScheme.onSurface, unfocusedTextColor=MaterialTheme.colorScheme.onSurface)) }, 
            confirmButton = { Button(onClick={if(isDep) onDeposit(sumS.toDoubleOrNull()?:0.0) else onWithdraw(sumS.toDoubleOrNull()?:0.0); showS=false}, colors=ButtonDefaults.buttonColors(containerColor=PrimaryColor)) { Text("Confirmă", color=Color.Black) } }
        )
    }
}

@Composable
fun BillsScreen(user: User, onPay: (Double, String, Boolean) -> Unit) {
    val bills = BillsApi.fetchBills()
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Text("Facturi", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor); Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(bills) { bill ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp), 
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (bill.isPaid) SaveColor.copy(0.3f) else AccentPink.copy(0.3f))
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column { 
                            Text(bill.provider, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(String.format(Locale.getDefault(), "%.2f RON", bill.amount), color = if (bill.isPaid) SaveColor else AccentPink, fontWeight = FontWeight.Bold) 
                        }
                        if (!bill.isPaid) { 
                            Button(
                                onClick = { 
                                    if (user.balance >= bill.amount) { BillsApi.payBill(bill.id); onPay(bill.amount, bill.provider, true) } 
                                    else onPay(bill.amount, bill.provider, false) 
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                            ) { Text("Plătește", color = Color.Black) } 
                        } else Icon(Icons.Default.CheckCircle, null, tint = SaveColor)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(hist: List<Pair<String, String>>) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Text("Istoric", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor); Spacer(Modifier.height(20.dp))
        LazyColumn {
            items(hist) { (t, v) ->
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(t, color = MaterialTheme.colorScheme.onSurface) }, 
                    trailingContent = { Text(v, color = if(v.contains("+")) SaveColor else if(v.contains("EȘUAT")) Color.Red else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) }, 
                    leadingContent = { Icon(if(v.contains("EȘUAT")) Icons.Default.Error else Icons.Default.History, null, tint = if(v.contains("EȘUAT")) Color.Red else PrimaryColor) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(0.1f))
            }
        }
    }
}

@Composable
fun TemplatesScreen(
    templates: List<Template>,
    onAdd: (String, String) -> Unit,
    onDelete: (Template) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Șabloane", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.AddCircle, null, tint = PrimaryColor, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        
        LazyColumn {
            items(templates) { template ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.1f))
                ) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(template.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(template.iban) },
                        trailingContent = {
                            IconButton(onClick = { onDelete(template) }) {
                                Icon(Icons.Default.Delete, null, tint = AccentPink)
                            }
                        },
                        leadingContent = {
                            Box(Modifier.size(40.dp).background(PrimaryColor.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = PrimaryColor)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var iban by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Șablon Nou") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nume Beneficiar") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = iban, onValueChange = { iban = it }, label = { Text("IBAN") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = { onAdd(name, iban); showAddDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                    Text("Salvează", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Anulează") }
            }
        )
    }
}

@Composable
fun SocialLendingScreen(
    user: User,
    loans: List<Loan>,
    onCreateLoan: (String, Double, Double, String) -> Unit,
    onRepayLoan: (Loan) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("Social Staking", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            IconButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.AddCircle, null, tint = PrimaryColor, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Împrumuturi între prieteni (P2P)", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(16.dp))

        if (loans.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nu ai împrumuturi active.", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(loans) { loan ->
                    val isLender = loan.lenderIban == user.iban
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.2f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Text(if (isLender) "Împrumut acordat lui" else "Împrumut de la", fontSize = 12.sp, color = Color.Gray)
                                Text(if (loan.isRepaid) "ACHITAT" else "ACTIV", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (loan.isRepaid) SaveColor else PrimaryColor)
                            }
                            Text(if (isLender) loan.borrowerName else loan.lenderName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Column { Text("Sumă", fontSize = 12.sp, color = Color.Gray); Text("${String.format(Locale.getDefault(), "%.2f", loan.amount)} RON", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) }
                                Column(horizontalAlignment = Alignment.End) { Text("Dobândă", fontSize = 12.sp, color = Color.Gray); Text("${loan.interestRate}%", fontWeight = FontWeight.Bold, color = PrimaryColor) }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Scadență: ${loan.dueDate}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                            
                            if (!loan.isRepaid && !isLender) {
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { onRepayLoan(loan) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) { Text("Achită acum", color = Color.Black) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var friendIban by remember { mutableStateOf("") }; var amount by remember { mutableStateOf("") }; var interest by remember { mutableStateOf("0") }; var date by remember { mutableStateOf("20.12.2024") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Contract Digital Nou", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    Text("Aplicația acționează ca garant și va retrage automat suma la scadență.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = friendIban, onValueChange = { friendIban = it }, label = { Text("IBAN Prieten") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface))
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Sumă (RON)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface))
                    OutlinedTextField(value = interest, onValueChange = { interest = it }, label = { Text("Dobândă (%)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface))
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Data Scadenței") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface))
                }
            },
            confirmButton = { Button(onClick = { onCreateLoan(friendIban, amount.toDoubleOrNull() ?: 0.0, interest.toDoubleOrNull() ?: 0.0, date); showCreateDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) { Text("Semnează și Trimite", color = Color.Black) } },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Anulează", color = PrimaryColor) } }
        )
    }
}
