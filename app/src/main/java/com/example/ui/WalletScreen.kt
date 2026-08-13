package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.data.WalletTransaction
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    userProfile: UserProfile?,
    transactions: List<WalletTransaction>,
    language: String,
    onDepositSubmit: (method: String, amount: Double, account: String, refId: String) -> Unit,
    onWithdrawSubmit: (method: String, amount: Double, account: String) -> Unit
) {
    var showDepositModal by remember { mutableStateOf(false) }
    var showWithdrawModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Wallet Card
        item {
            WalletBalanceCard(
                userProfile = userProfile,
                language = language,
                onDepositClick = { showDepositModal = true },
                onWithdrawClick = { showWithdrawModal = true }
            )
        }

        // Section Title
        item {
            Text(
                text = if (language == "BN") "📜 ওয়ালেট লেনদেন ইতিহাস" else "📜 Wallet Transaction History",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Gold80
                )
            )
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == "BN") "কোন লেনদেন তথ্য পাওয়া যায়নি।" else "No transactions recorded yet.",
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(transactions, key = { it.id }) { tx ->
                TransactionRow(tx = tx, language = language)
            }
        }
    }

    if (showDepositModal) {
        DepositDialog(
            language = language,
            onDismiss = { showDepositModal = false },
            onSubmit = { method, amount, account, refId ->
                onDepositSubmit(method, amount, account, refId)
                showDepositModal = false
            }
        )
    }

    if (showWithdrawModal) {
        WithdrawDialog(
            balance = userProfile?.balance ?: 0.0,
            language = language,
            onDismiss = { showWithdrawModal = false },
            onSubmit = { method, amount, account ->
                onWithdrawSubmit(method, amount, account)
                showWithdrawModal = false
            }
        )
    }
}

@Composable
fun WalletBalanceCard(
    userProfile: UserProfile?,
    language: String,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wallet_balance_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(RoyalIndigo, DarkSurface)
                    )
                )
                .border(1.dp, Gold80.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == "BN") "ক্যাশ ব্যালেন্স" else "Cash Balance",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray)
                    )
                    Surface(
                        color = Gold80.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = userProfile?.name ?: "Player",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Gold80,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Text(
                    text = "৳ ${String.format("%,.2f", userProfile?.balance ?: 0.0)}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Gold80
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${if (language == "BN") "মোট জয়:" else "Total Winnings:"} ৳ ${String.format("%,.0f", userProfile?.totalWinnings ?: 0.0)}",
                        style = MaterialTheme.typography.bodySmall.copy(color = EmeraldGreen)
                    )
                    Text(
                        text = "${if (language == "BN") "কেনা টিকেট:" else "Tickets Bought:"} ${userProfile?.totalTicketsBought ?: 0}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                    )
                }

                Divider(color = Color.Gray.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDepositClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("deposit_wallet_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCard,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "BN") "ডিপোজিট" else "Deposit",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onWithdrawClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("withdraw_wallet_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "BN") "উইথড্র" else "Withdraw",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRow(tx: WalletTransaction, language: String) {
    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateStr = sdf.format(Date(tx.timestampEpoch))

    val (icon, color, prefix) = when (tx.type) {
        "DEPOSIT" -> Triple(Icons.Default.ArrowDownward, EmeraldGreen, "+")
        "PRIZE_WIN" -> Triple(Icons.Default.EmojiEvents, Gold80, "+")
        "WITHDRAWAL" -> Triple(Icons.Default.ArrowUpward, CrimsonRed, "-")
        else -> Triple(Icons.Default.ConfirmationNumber, Color.White, "-")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_row_${tx.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = color.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = color)
                    }
                }

                Column {
                    Text(
                        text = tx.type.replace("_", " "),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "${tx.paymentMethod} • $dateStr",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    if (tx.note.isNotBlank()) {
                        Text(
                            text = tx.note,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray.copy(alpha = 0.7f))
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$prefix৳ ${String.format("%,.0f", tx.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )

                Surface(
                    color = when (tx.status) {
                        "COMPLETED" -> EmeraldGreen.copy(alpha = 0.2f)
                        "REJECTED" -> CrimsonRed.copy(alpha = 0.2f)
                        else -> Gold80.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = tx.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = when (tx.status) {
                                "COMPLETED" -> EmeraldGreen
                                "REJECTED" -> CrimsonRed
                                else -> Gold80
                            },
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DepositDialog(
    language: String,
    onDismiss: () -> Unit,
    onSubmit: (method: String, amount: Double, account: String, refId: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("bKash") }
    var amountText by remember { mutableStateOf("500") }
    var accountText by remember { mutableStateOf("") }
    var refIdText by remember { mutableStateOf("") }

    val merchantAccount = when (selectedMethod) {
        "bKash" -> "01700-112233 (Merchant)"
        "Nagad" -> "01800-445566 (Merchant)"
        "Rocket" -> "01900-778899-1 (Merchant)"
        else -> "LottoWin Bank: 102938475"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (language == "BN") "💳 ডিপোজিট পেমেন্ট (bKash / Nagad)" else "💳 Top Up Wallet Deposit",
                color = Gold80,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (language == "BN") "পেমেন্ট মেথড নির্বাচন করুন:" else "Select Payment Method:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("bKash", "Nagad", "Rocket", "Cards").forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method) }
                        )
                    }
                }

                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (language == "BN") "মার্চেন্ট একাউন্ট নম্বর:" else "Merchant Account:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = merchantAccount,
                            style = MaterialTheme.typography.titleMedium,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_amount_field")
                )

                OutlinedTextField(
                    value = accountText,
                    onValueChange = { accountText = it },
                    label = { Text("Your Mobile/Account No") },
                    placeholder = { Text("017XXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_account_field")
                )

                OutlinedTextField(
                    value = refIdText,
                    onValueChange = { refIdText = it },
                    label = { Text("Transaction Ref / TrxID") },
                    placeholder = { Text("e.g. BK8X92M1L") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_refid_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    onSubmit(selectedMethod, amt, accountText, refIdText)
                },
                modifier = Modifier.testTag("submit_deposit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text("Submit Deposit Request", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun WithdrawDialog(
    balance: Double,
    language: String,
    onDismiss: () -> Unit,
    onSubmit: (method: String, amount: Double, account: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("bKash") }
    var amountText by remember { mutableStateOf("500") }
    var accountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (language == "BN") "💸 টাকা উত্তোলন (Withdrawal)" else "💸 Withdraw Winnings",
                color = Gold80,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Available Balance: ৳ ${String.format("%,.2f", balance)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("bKash", "Nagad", "Rocket", "Bank").forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Withdraw Amount (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_amount_field")
                )

                OutlinedTextField(
                    value = accountText,
                    onValueChange = { accountText = it },
                    label = { Text("Receiver Mobile / Account No") },
                    placeholder = { Text("017XXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_account_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    onSubmit(selectedMethod, amt, accountText)
                },
                modifier = Modifier.testTag("submit_withdraw_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Gold80, contentColor = Color.Black)
            ) {
                Text("Confirm Withdrawal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
