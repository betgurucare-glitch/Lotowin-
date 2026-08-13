package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lottery_draws")
data class LotteryDraw(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // e.g., "DAILY", "MEGA_JACKPOT", "SPECIAL_BPL"
    val jackpotAmount: Double,
    val ticketPrice: Double,
    val drawTimeEpoch: Long, // timestamp when draw happens
    val status: String = "UPCOMING", // "UPCOMING", "DRAWING", "COMPLETED"
    val winningNumbersCsv: String = "", // e.g., "7,14,23,31,45,49"
    val maxNumberChoice: Int = 49, // e.g., select 6 out of 49
    val pickCount: Int = 6,
    val totalTicketsSold: Int = 0
)

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val drawId: Long,
    val drawTitle: String,
    val chosenNumbersCsv: String,
    val purchaseTimeEpoch: Long = System.currentTimeMillis(),
    val price: Double,
    val status: String = "ACTIVE", // "ACTIVE", "WON", "LOST"
    val prizeWon: Double = 0.0,
    val matchedCount: Int = 0
)

@Entity(tableName = "wallet_transactions")
data class WalletTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "DEPOSIT", "WITHDRAWAL", "TICKET_BUY", "PRIZE_WIN"
    val amount: Double,
    val paymentMethod: String = "bKash", // "bKash", "Nagad", "Rocket", "Cards", "Bank"
    val accountNumber: String = "",
    val referenceId: String = "",
    val status: String = "COMPLETED", // "PENDING", "COMPLETED", "REJECTED"
    val timestampEpoch: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Lucky Player",
    val phone: String = "01700000000",
    val balance: Double = 1000.0,
    val totalWinnings: Double = 0.0,
    val totalTicketsBought: Int = 0,
    val language: String = "EN" // "EN" or "BN"
)
