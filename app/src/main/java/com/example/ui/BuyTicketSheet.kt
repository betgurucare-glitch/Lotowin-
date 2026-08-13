package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LotteryDraw
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyTicketSheet(
    draw: LotteryDraw,
    selectedNumbers: Set<Int>,
    walletBalance: Double,
    language: String,
    onNumberToggle: (Int) -> Unit,
    onQuickPick: () -> Unit,
    onConfirmPurchase: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSurface,
        scrimColor = Color.Black.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("buy_ticket_bottom_sheet"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = draw.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (language == "BN") "${draw.pickCount}টি সংখ্যা নির্বাচন করুন (১ - ${draw.maxNumberChoice})" else "Select ${draw.pickCount} numbers (1 - ${draw.maxNumberChoice})",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            // Quick Pick & Selection Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${selectedNumbers.size} / ${draw.pickCount} ${if (language == "BN") "নির্বাচিত" else "Selected"}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (selectedNumbers.size == draw.pickCount) EmeraldGreen else Gold80,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                OutlinedButton(
                    onClick = onQuickPick,
                    modifier = Modifier.testTag("quick_pick_button"),
                    border = BorderStroke(1.dp, Gold80),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Gold80,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == "BN") "অটো পিক" else "Quick Pick",
                        color = Gold80,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Number Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                val numbers = (1..draw.maxNumberChoice).toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(numbers, key = { it }) { number ->
                        val isSelected = selectedNumbers.contains(number)
                        NumberBall(
                            number = number,
                            isSelected = isSelected,
                            onClick = { onNumberToggle(number) }
                        )
                    }
                }
            }

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            // Wallet Balance & Ticket Cost
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == "BN") "আপনার ওয়ালেট ব্যালেন্স" else "Wallet Balance",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "৳ ${String.format("%,.2f", walletBalance)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (walletBalance >= draw.ticketPrice) EmeraldGreen else CrimsonRed
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (language == "BN") "মোট খরচ" else "Total Cost",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "৳ ${draw.ticketPrice.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Gold80
                        )
                    )
                }
            }

            // Confirm Purchase Button
            Button(
                onClick = onConfirmPurchase,
                enabled = selectedNumbers.size == draw.pickCount && walletBalance >= draw.ticketPrice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_purchase_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold80,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (walletBalance < draw.ticketPrice)
                        (if (language == "BN") "পর্যাপ্ত ব্যালেন্স নেই" else "Insufficient Balance")
                    else
                        (if (language == "BN") "টিকেট নিশ্চিত করুন (৳ ${draw.ticketPrice.toInt()})" else "Confirm Purchase (৳ ${draw.ticketPrice.toInt()})"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun NumberBall(
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Gold80 else DarkSurfaceVariant
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .testTag("number_ball_$number"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else Color.White
            )
        )
    }
}
