package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [LotteryDraw::class, Ticket::class, WalletTransaction::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lotteryDao(): LotteryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lottowin_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default initial data on database creation
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(database.lotteryDao())
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(dao: LotteryDao) {
            // Seed user profile
            dao.insertOrUpdateProfile(
                UserProfile(
                    id = 1,
                    name = "Lucky Player",
                    phone = "01712345678",
                    balance = 2500.0,
                    totalWinnings = 1500.0,
                    totalTicketsBought = 3,
                    language = "EN"
                )
            )

            val now = System.currentTimeMillis()

            // Seed active upcoming draws
            val draw1Id = dao.insertDraw(
                LotteryDraw(
                    title = "BPL Mega Fortune ৳5,000,000",
                    category = "SPECIAL_BPL",
                    jackpotAmount = 5000000.0,
                    ticketPrice = 100.0,
                    drawTimeEpoch = now + 15 * 60 * 1000, // 15 mins from now
                    status = "UPCOMING",
                    maxNumberChoice = 49,
                    pickCount = 6,
                    totalTicketsSold = 1420
                )
            )

            val draw2Id = dao.insertDraw(
                LotteryDraw(
                    title = "Daily Super 6/49 $250,000",
                    category = "DAILY",
                    jackpotAmount = 250000.0,
                    ticketPrice = 50.0,
                    drawTimeEpoch = now + 2 * 3600 * 1000, // 2 hours from now
                    status = "UPCOMING",
                    maxNumberChoice = 49,
                    pickCount = 6,
                    totalTicketsSold = 850
                )
            )

            dao.insertDraw(
                LotteryDraw(
                    title = "Gold Star Lucky 7 ৳1,000,000",
                    category = "GOLD_STAR",
                    jackpotAmount = 1000000.0,
                    ticketPrice = 20.0,
                    drawTimeEpoch = now + 24 * 3600 * 1000, // 1 day from now
                    status = "UPCOMING",
                    maxNumberChoice = 36,
                    pickCount = 5,
                    totalTicketsSold = 310
                )
            )

            // Seed a completed draw with winning numbers
            val completedDrawId = dao.insertDraw(
                LotteryDraw(
                    title = "Midnight Millions Draw #108",
                    category = "DAILY",
                    jackpotAmount = 1000000.0,
                    ticketPrice = 50.0,
                    drawTimeEpoch = now - 3600 * 1000, // 1 hour ago
                    status = "COMPLETED",
                    winningNumbersCsv = "7,14,21,33,42,48",
                    maxNumberChoice = 49,
                    pickCount = 6,
                    totalTicketsSold = 2100
                )
            )

            // Seed sample user tickets
            dao.insertTicket(
                Ticket(
                    drawId = draw1Id,
                    drawTitle = "BPL Mega Fortune ৳5,000,000",
                    chosenNumbersCsv = "7,12,18,25,33,41",
                    price = 100.0,
                    status = "ACTIVE"
                )
            )

            dao.insertTicket(
                Ticket(
                    drawId = completedDrawId,
                    drawTitle = "Midnight Millions Draw #108",
                    chosenNumbersCsv = "7,14,21,30,42,48", // 5 matches out of 6!
                    price = 50.0,
                    status = "WON",
                    prizeWon = 1500.0,
                    matchedCount = 5
                )
            )

            // Seed sample initial transactions
            dao.insertTransaction(
                WalletTransaction(
                    type = "DEPOSIT",
                    amount = 2000.0,
                    paymentMethod = "bKash",
                    accountNumber = "01712345678",
                    referenceId = "BK8X92M1L",
                    status = "COMPLETED",
                    timestampEpoch = now - 86400 * 1000,
                    note = "Initial bKash deposit"
                )
            )

            dao.insertTransaction(
                WalletTransaction(
                    type = "PRIZE_WIN",
                    amount = 1500.0,
                    paymentMethod = "System Prize",
                    status = "COMPLETED",
                    timestampEpoch = now - 3600 * 1000,
                    note = "Midnight Millions Draw #108 - Matched 5 numbers!"
                )
            )

            dao.insertTransaction(
                WalletTransaction(
                    type = "TICKET_BUY",
                    amount = 100.0,
                    paymentMethod = "Wallet Balance",
                    status = "COMPLETED",
                    timestampEpoch = now - 1800 * 1000,
                    note = "Purchased ticket for BPL Mega Fortune ৳5,000,000"
                )
            )
        }
    }
}
