package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LotteryDraw
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LiveDrawResultsScreen(
    completedDraws: List<LotteryDraw>,
    isDrawingInProgress: Boolean,
    liveDrawnNumbers: List<Int>,
    language: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Live Drawing Animation Stage if active
        if (isDrawingInProgress) {
            item {
                LiveDrawingStage(
                    liveDrawnNumbers = liveDrawnNumbers,
                    language = language
                )
            }
        }

        // Section Title
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = null,
                    tint = Gold80
                )
                Text(
                    text = if (language == "BN") "🏆 পূর্ববর্তী ড্র এর ফলাফল" else "🏆 Live Draw Results & Past Archive",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold80
                    )
                )
            }
        }

        if (completedDraws.isEmpty() && !isDrawingInProgress) {
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
                            text = if (language == "BN") "এখনও কোন ড্র সম্পূর্ণ হয়নি। এডমিন থেকে ড্র পরিচালনা করুন।" else "No completed draws yet. Admin can execute live draws.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(completedDraws, key = { it.id }) { draw ->
                CompletedDrawCard(draw = draw, language = language)
            }
        }
    }
}

@Composable
fun LiveDrawingStage(
    liveDrawnNumbers: List<Int>,
    language: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_drawing_stage_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(RoyalIndigo, DarkSurface)
                    )
                )
                .border(
                    width = 2.dp,
                    color = Gold80,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (language == "BN") "🎰 ড্র কার্যক্রম চলছে..." else "🎰 LIVE DRAW IN PROGRESS...",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Gold80,
                        letterSpacing = 1.sp
                    )
                )

                Text(
                    text = if (language == "BN") "র্যান্ডম নম্বর জেনারেট করা হচ্ছে" else "Generating winning numbers in real-time",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    liveDrawnNumbers.forEach { num ->
                        AnimatedVisibility(
                            visible = true,
                            enter = scaleIn() + fadeIn()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Gold80, CircleShape)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = num.toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }

                CircularProgressIndicator(color = Gold80)
            }
        }
    }
}

@Composable
fun CompletedDrawCard(draw: LotteryDraw, language: String) {
    val sdf = remember { SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault()) }
    val dateStr = sdf.format(Date(draw.drawTimeEpoch))

    val winningNumbers = remember(draw.winningNumbersCsv) {
        draw.winningNumbersCsv.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("completed_draw_card_${draw.id}"),
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
                        text = draw.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                Surface(
                    color = EmeraldGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (language == "BN") "সম্পন্ন" else "COMPLETED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Text(
                text = if (language == "BN") "বিজয়ী নম্বরসমূহ:" else "Winning Numbers:",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Gold80,
                    fontWeight = FontWeight.Bold
                )
            )

            // Winning Numbers Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(winningNumbers) { num ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.radialGradient(listOf(Gold80, GoldVariant)),
                                shape = CircleShape
                            )
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }
                }
            }

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jackpot: ৳ ${String.format("%,.0f", draw.jackpotAmount)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "${draw.totalTicketsSold} ${if (language == "BN") "টিকেট অংশ নিয়েছিল" else "tickets played"}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }
        }
    }
}
