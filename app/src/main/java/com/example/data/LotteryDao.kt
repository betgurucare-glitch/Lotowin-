package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LotteryDao {
    // Draws
    @Query("SELECT * FROM lottery_draws ORDER BY drawTimeEpoch ASC")
    fun getAllDraws(): Flow<List<LotteryDraw>>

    @Query("SELECT * FROM lottery_draws WHERE id = :id")
    suspend fun getDrawById(id: Long): LotteryDraw?

    @Query("SELECT * FROM lottery_draws WHERE status = 'UPCOMING' ORDER BY drawTimeEpoch ASC")
    fun getUpcomingDraws(): Flow<List<LotteryDraw>>

    @Query("SELECT * FROM lottery_draws WHERE status = 'COMPLETED' ORDER BY drawTimeEpoch DESC")
    fun getCompletedDraws(): Flow<List<LotteryDraw>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraw(draw: LotteryDraw): Long

    @Update
    suspend fun updateDraw(draw: LotteryDraw)

    @Query("DELETE FROM lottery_draws WHERE id = :id")
    suspend fun deleteDrawById(id: Long)

    // Tickets
    @Query("SELECT * FROM tickets ORDER BY purchaseTimeEpoch DESC")
    fun getAllTickets(): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE drawId = :drawId")
    suspend fun getTicketsForDraw(drawId: Long): List<Ticket>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket): Long

    @Update
    suspend fun updateTicket(ticket: Ticket)

    // Wallet Transactions
    @Query("SELECT * FROM wallet_transactions ORDER BY timestampEpoch DESC")
    fun getAllTransactions(): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransaction): Long

    @Update
    suspend fun updateTransaction(transaction: WalletTransaction)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET balance = balance + :amount WHERE id = 1")
    suspend fun creditUserBalance(amount: Double)

    @Query("UPDATE user_profile SET balance = balance - :amount WHERE id = 1")
    suspend fun debitUserBalance(amount: Double)
}
