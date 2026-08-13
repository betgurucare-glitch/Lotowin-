package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.Ticket
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyTicketsScreen(
    tickets: List<Ticket>,
    language: String
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "WON"

    val filteredTickets = when (selectedFilter) {
        "ACTIVE" -> tickets.filter { it.status == "ACTIVE" }
        "WON" -> tickets.filter { it.status == "WON" }
        else -> tickets
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (language == "BN") "🎟️ আমার ক্রয়কৃত টিকেটসমূহ" else "🎟️ My Purchased Tickets",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Gold80
            )
        )

        // Filter Pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" },
                label = { Text(if (language == "BN") "সব টিকেট (${tickets.size})" else "All (${tickets.size})") }
            )
            FilterChip(
                selected = selectedFilter == "ACTIVE",
                onClick = { selectedFilter = "ACTIVE" },
                label = { Text(if (language == "BN") "সক্রিয় (${tickets.count { it.status == "ACTIVE" }})" else "Active (${tickets.count { it.status == "ACTIVE" }})") }
            )
            FilterChip(
                selected = selectedFilter == "WON",
                onClick = { selectedFilter = "WON" },
                label = { Text(if (language == "BN") "বিজয়ী (${tickets.count { it.status == "WON" }})" else "Winners (${tickets.count { it.status == "WON" }})") }
            )
        }

        if (filteredTickets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = if (language == "BN") "কোন টিকেট পাওয়া যায়নি।" else "No tickets found.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTickets, key = { it.id }) { ticket ->
                    TicketCard(ticket = ticket, language = language)
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket, language: String) {
    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateStr = sdf.format(Date(ticket.purchaseTimeEpoch))

    val numbersList = remember(ticket.chosenNumbersCsv) {
        ticket.chosenNumbersCsv.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ticket_card_${ticket.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = ticket.drawTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Ticket #${ticket.id} • $dateStr",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                TicketStatusBadge(status = ticket.status, prize = ticket.prizeWon, language = language)
            }

            // Ticket Numbers Balls
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(numbersList) { num ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (ticket.status == "WON") Gold80 else DarkSurfaceVariant,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (ticket.status == "WON") Color.White else Color.Gray.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (ticket.status == "WON") Color.Black else Color.White
                            )
                        )
                    }
                }
            }

            if (ticket.status == "WON") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Gold80,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == "BN") "${ticket.matchedCount}টি ম্যাচ হয়েছে!" else "${ticket.matchedCount} Numbers Matched!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = "Prize: ৳ ${String.format("%,.0f", ticket.prizeWon)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Gold80,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TicketStatusBadge(status: String, prize: Double, language: String) {
    val (bgColor, textColor, text) = when (status) {
        "WON" -> Triple(
            EmeraldGreen.copy(alpha = 0.2f),
            EmeraldGreen,
            if (language == "BN") "বিজয়ী ৳${prize.toInt()}" else "WON ৳${prize.toInt()}"
        )
        "LOST" -> Triple(
            Color.Gray.copy(alpha = 0.2f),
            Color.Gray,
            if (language == "BN") "অবিজয়ী" else "No Win"
        )
        else -> Triple(
            Gold80.copy(alpha = 0.2f),
            Gold80,
            if (language == "BN") "সক্রিয়" else "ACTIVE"
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}
