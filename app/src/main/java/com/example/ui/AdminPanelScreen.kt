package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.LotteryDraw
import com.example.data.Ticket
import com.example.data.WalletTransaction
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(
    upcomingDraws: List<LotteryDraw>,
    completedDraws: List<LotteryDraw>,
    transactions: List<WalletTransaction>,
    tickets: List<Ticket>,
    language: String,
    onCreateDraw: (title: String, category: String, jackpot: Double, price: Double, minutes: Int) -> Unit,
    onTriggerDraw: (draw: LotteryDraw, customWinningNumbers: List<Int>?) -> Unit,
    onApproveTx: (WalletTransaction) -> Unit,
    onRejectTx: (WalletTransaction) -> Unit
) {
    var showCreateDrawModal by remember { mutableStateOf(false) }
    var selectedDrawForForce by remember { mutableStateOf<LotteryDraw?>(null) }

    val pendingTxs = remember(transactions) {
        transactions.filter { it.status == "PENDING" }
    }

    val totalSales = remember(tickets) {
        tickets.sumOf { it.price }
    }

    val totalPayouts = remember(tickets) {
        tickets.sumOf { it.prizeWon }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Admin Header Banner
        item {
            AdminHeaderCard(
                totalSales = totalSales,
                totalPayouts = totalPayouts,
                pendingCount = pendingTxs.size,
                onCreateDrawClick = { showCreateDrawModal = true }
            )
        }

        // Pending Payment Approvals Section
        item {
            Text(
                text = "🔔 Pending Payment Approvals (${pendingTxs.size})",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Gold80
                )
            )
        }

        if (pendingTxs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pending payment approvals.",
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(pendingTxs, key = { it.id }) { tx ->
                PendingTxApprovalCard(
                    tx = tx,
                    onApprove = { onApproveTx(tx) },
                    onReject = { onRejectTx(tx) }
                )
            }
        }

        // Manage Active Draws
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚙️ Manage Active Draws",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold80
                    )
                )
                Button(
                    onClick = { showCreateDrawModal = true },
                    modifier = Modifier.testTag("admin_create_draw_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Draw", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (upcomingDraws.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No active draws running.", color = Color.Gray)
                    }
                }
            }
        } else {
            items(upcomingDraws, key = { it.id }) { draw ->
                AdminDrawManageCard(
                    draw = draw,
                    onForceDrawClick = { selectedDrawForForce = draw }
                )
            }
        }
    }

    if (showCreateDrawModal) {
        CreateDrawDialog(
            onDismiss = { showCreateDrawModal = false },
            onSubmit = { title, cat, jackpot, price, mins ->
                onCreateDraw(title, cat, jackpot, price, mins)
                showCreateDrawModal = false
            }
        )
    }

    selectedDrawForForce?.let { draw ->
        ForceDrawDialog(
            draw = draw,
            onDismiss = { selectedDrawForForce = null },
            onSubmit = { customNumbers ->
                onTriggerDraw(draw, customNumbers)
                selectedDrawForForce = null
            }
        )
    }
}

@Composable
fun AdminHeaderCard(
    totalSales: Double,
    totalPayouts: Double,
    pendingCount: Int,
    onCreateDrawClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_header_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Gold80,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADMIN CONTROLLER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Gold80
                        )
                    )
                }

                Surface(
                    color = EmeraldGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "LIVE SYSTEM",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AdminStatMetric(label = "Total Revenue", value = "৳ ${totalSales.toInt()}", color = Gold80)
                AdminStatMetric(label = "Total Paid Out", value = "৳ ${totalPayouts.toInt()}", color = EmeraldGreen)
                AdminStatMetric(label = "Pending Approvals", value = pendingCount.toString(), color = CrimsonRed)
            }
        }
    }
}

@Composable
fun AdminStatMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )
    }
}

@Composable
fun PendingTxApprovalCard(
    tx: WalletTransaction,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pending_tx_card_${tx.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (tx.type == "DEPOSIT") EmeraldGreen.copy(alpha = 0.2f) else CrimsonRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = tx.type,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (tx.type == "DEPOSIT") EmeraldGreen else CrimsonRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    text = "৳ ${String.format("%,.0f", tx.amount)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold80
                    )
                )
            }

            Text(
                text = "Method: ${tx.paymentMethod} | Account: ${tx.accountNumber}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )

            if (tx.referenceId.isNotBlank()) {
                Text(
                    text = "Ref / TrxID: ${tx.referenceId}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Gold80,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("approve_tx_button_${tx.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve")
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reject_tx_button_${tx.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                    border = BorderStroke(1.dp, CrimsonRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject")
                }
            }
        }
    }
}

@Composable
fun AdminDrawManageCard(
    draw: LotteryDraw,
    onForceDrawClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_draw_card_${draw.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = draw.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Jackpot: ৳ ${draw.jackpotAmount.toInt()}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Gold80)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tickets Sold: ${draw.totalTicketsSold}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Button(
                    onClick = onForceDrawClick,
                    modifier = Modifier.testTag("force_draw_button_${draw.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold80, contentColor = Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Draw Now!", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CreateDrawDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, category: String, jackpot: Double, price: Double, minutes: Int) -> Unit
) {
    var title by remember { mutableStateOf("BPL Special Fortune") }
    var category by remember { mutableStateOf("SPECIAL_BPL") }
    var jackpot by remember { mutableStateOf("1000000") }
    var price by remember { mutableStateOf("100") }
    var minutes by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text("Create New Lottery Draw", color = Gold80, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Draw Title") },
                    modifier = Modifier.fillMaxWidth().testTag("new_draw_title")
                )
                OutlinedTextField(
                    value = jackpot,
                    onValueChange = { jackpot = it },
                    label = { Text("Jackpot Amount (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("new_draw_jackpot")
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Ticket Price (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("new_draw_price")
                )
                OutlinedTextField(
                    value = minutes,
                    onValueChange = { minutes = it },
                    label = { Text("Draw Countdown (Minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("new_draw_minutes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val j = jackpot.toDoubleOrNull() ?: 0.0
                    val p = price.toDoubleOrNull() ?: 0.0
                    val m = minutes.toIntOrNull() ?: 15
                    onSubmit(title, category, j, p, m)
                },
                modifier = Modifier.testTag("submit_create_draw_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Text("Create Event")
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
fun ForceDrawDialog(
    draw: LotteryDraw,
    onDismiss: () -> Unit,
    onSubmit: (customWinningNumbers: List<Int>?) -> Unit
) {
    var isManual by remember { mutableStateOf(false) }
    var numbersText by remember { mutableStateOf("7, 14, 21, 28, 35, 42") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text("Execute Draw: ${draw.title}", color = Gold80, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "You can trigger an automatic random draw or manually enter the winning numbers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isManual,
                        onCheckedChange = { isManual = it }
                    )
                    Text("Set manual winning numbers")
                }

                if (isManual) {
                    OutlinedTextField(
                        value = numbersText,
                        onValueChange = { numbersText = it },
                        label = { Text("Enter ${draw.pickCount} numbers (comma separated)") },
                        placeholder = { Text("e.g. 7, 12, 18, 25, 33, 41") },
                        modifier = Modifier.fillMaxWidth().testTag("manual_numbers_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isManual) {
                        val parsed = numbersText.split(",").mapNotNull { it.trim().toIntOrNull() }
                        onSubmit(if (parsed.size == draw.pickCount) parsed else null)
                    } else {
                        onSubmit(null)
                    }
                },
                modifier = Modifier.testTag("submit_force_draw_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Gold80, contentColor = Color.Black)
            ) {
                Text("Start Live Draw!", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
