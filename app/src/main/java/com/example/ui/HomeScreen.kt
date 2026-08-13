package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LotteryDraw
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    upcomingDraws: List<LotteryDraw>,
    language: String,
    onBuyTicketClick: (LotteryDraw) -> Unit,
    onViewResultsClick: () -> Unit,
    onDepositClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Hero Banner
        item {
            HeroBanner(language = language, onDepositClick = onDepositClick)
        }

        // Quick Stats Ticker
        item {
            LiveStatsTicker(language = language)
        }

        // Section Title: Active Lottery Draws
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "BN") "🔥 চলমান লটারি ইভেন্টসমূহ" else "🔥 Active Lottery Events",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold80
                    )
                )
                TextButton(onClick = onViewResultsClick) {
                    Text(
                        text = if (language == "BN") "ফলাফল দেখুন ›" else "Past Results ›",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Active Draws List
        if (upcomingDraws.isEmpty()) {
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
                            text = if (language == "BN") "কোন চলমান লটারি নেই। নতুন ড্র তৈরি করুন।" else "No active draws. Admin can create new draws.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(upcomingDraws, key = { it.id }) { draw ->
                LotteryDrawCard(
                    draw = draw,
                    language = language,
                    onBuyTicketClick = { onBuyTicketClick(draw) }
                )
            }
        }

        // How To Play Guide
        item {
            HowToPlaySection(language = language)
        }
    }
}

@Composable
fun HeroBanner(language: String, onDepositClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(RoyalIndigo, IndigoLight, DarkSurface)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(Gold80, EmeraldGreen)),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Gold80.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Jackpot",
                                tint = Gold80
                            )
                        }
                    }
                    Text(
                        text = if (language == "BN") "মেগা জ্যাকপট বিপিএল লটারি" else "MEGA FORTUNE JACKPOT",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Gold80,
                            letterSpacing = 1.2.sp
                        )
                    )
                }

                Text(
                    text = if (language == "BN") "৳ ৫,০০০,০০০" else "৳ 5,000,000",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )

                Text(
                    text = if (language == "BN") "রিয়েল-টাইম ড্র এবং তাৎক্ষণিক ওয়ালেট উইথড্র সুবিধা!" else "Real-time draw results & instant wallet payouts!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onDepositClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("deposit_hero_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "BN") "টাকা ডিপোজিট করুন (bKash / Nagad)" else "Top Up Wallet (bKash / Nagad)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LiveStatsTicker(language: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                label = if (language == "BN") "আজকের বিজয়ীরা" else "Today's Winners",
                value = "1,420+",
                color = EmeraldGreen
            )
            Divider(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp),
                color = Color.Gray.copy(alpha = 0.3f)
            )
            StatItem(
                label = if (language == "BN") "মোট পেআউট" else "Total Paid",
                value = "৳ 8.5M+",
                color = Gold80
            )
            Divider(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp),
                color = Color.Gray.copy(alpha = 0.3f)
            )
            StatItem(
                label = if (language == "BN") "নিরাপদ গেটওয়ে" else "Secure Gateway",
                value = "bKash / Nagad",
                color = Color.White
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray
            )
        )
    }
}

@Composable
fun LotteryDrawCard(
    draw: LotteryDraw,
    language: String,
    onBuyTicketClick: () -> Unit
) {
    var remainingSeconds by remember(draw.drawTimeEpoch) {
        mutableStateOf(maxOf(0L, (draw.drawTimeEpoch - System.currentTimeMillis()) / 1000))
    }

    LaunchedEffect(draw.drawTimeEpoch) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds = maxOf(0L, (draw.drawTimeEpoch - System.currentTimeMillis()) / 1000)
        }
    }

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("draw_card_${draw.id}"),
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
                Surface(
                    color = Gold80.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = draw.category.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Gold80,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (remainingSeconds < 300) CrimsonRed else Gold80,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (remainingSeconds > 0) timeFormatted else (if (language == "BN") "ড্র প্রস্তুত" else "Ready to Draw"),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (remainingSeconds < 300) CrimsonRed else Color.White
                        )
                    )
                }
            }

            Text(
                text = draw.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == "BN") "জ্যাকপট প্রাইস" else "Jackpot Prize",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "৳ ${String.format("%,.0f", draw.jackpotAmount)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Gold80
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (language == "BN") "টিকেট মূল্য" else "Ticket Price",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "৳ ${draw.ticketPrice.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    )
                }
            }

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${draw.totalTicketsSold} ${if (language == "BN") "টিকেট বিক্রি হয়েছে" else "tickets sold"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                Button(
                    onClick = onBuyTicketClick,
                    modifier = Modifier.testTag("buy_ticket_button_${draw.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold80,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (language == "BN") "টিকেট কিনুন" else "Buy Ticket",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HowToPlaySection(language: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (language == "BN") "💡 কিভাবে খেলবেন?" else "💡 How to Play?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Gold80
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                StepBadge(step = "1")
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (language == "BN") "লটারি নির্বাচন করুন এবং পছন্দের ৬টি সংখ্যা বা 'Quick Pick' টিপুন।" else "Choose a lottery event and pick your 6 lucky numbers or use 'Quick Pick'.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StepBadge(step = "2")
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (language == "BN") "bKash, Nagad বা ওয়ালেট ব্যালেন্স থেকে টিকেট ক্রয় নিশ্চিত করুন।" else "Confirm ticket purchase using wallet balance, bKash, or Nagad.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StepBadge(step = "3")
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (language == "BN") "লাইভ ড্র কাউন্টডাউন দেখুন। বিজয়ী হলে সাথে সাথে ওয়ালেটে টাকা জমা হবে!" else "Watch live real-time draw. Winnings are automatically deposited to your wallet!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }
        }
    }
}

@Composable
fun StepBadge(step: String) {
    Surface(
        color = Gold80,
        shape = CircleShape,
        modifier = Modifier.size(28.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = step,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }
    }
}
