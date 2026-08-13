package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class LotteryRepository(private val dao: LotteryDao) {

    val allDraws: Flow<List<LotteryDraw>> = dao.getAllDraws()
    val upcomingDraws: Flow<List<LotteryDraw>> = dao.getUpcomingDraws()
    val completedDraws: Flow<List<LotteryDraw>> = dao.getCompletedDraws()

    val allTickets: Flow<List<Ticket>> = dao.getAllTickets()
    val allTransactions: Flow<List<WalletTransaction>> = dao.getAllTransactions()
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    suspend fun buyTicket(
        draw: LotteryDraw,
        chosenNumbers: List<Int>
    ): Result<Ticket> {
        val profile = dao.getUserProfileOnce() ?: return Result.failure(Exception("User profile not found"))

        if (profile.balance < draw.ticketPrice) {
            return Result.failure(Exception("Insufficient wallet balance. Please top up your wallet."))
        }

        val csvNumbers = chosenNumbers.sorted().joinToString(",")

        // Debit user balance
        dao.debitUserBalance(draw.ticketPrice)

        // Increment ticket count on draw
        val updatedDraw = draw.copy(totalTicketsSold = draw.totalTicketsSold + 1)
        dao.updateDraw(updatedDraw)

        // Save ticket
        val ticket = Ticket(
            drawId = draw.id,
            drawTitle = draw.title,
            chosenNumbersCsv = csvNumbers,
            price = draw.ticketPrice,
            status = "ACTIVE"
        )
        val ticketId = dao.insertTicket(ticket)

        // Record transaction
        dao.insertTransaction(
            WalletTransaction(
                type = "TICKET_BUY",
                amount = draw.ticketPrice,
                paymentMethod = "Wallet Balance",
                status = "COMPLETED",
                note = "Ticket #${ticketId} for ${draw.title}"
            )
        )

        return Result.success(ticket.copy(id = ticketId))
    }

    suspend fun requestDeposit(
        method: String,
        amount: Double,
        accountNumber: String,
        referenceId: String
    ): Long {
        val transaction = WalletTransaction(
            type = "DEPOSIT",
            amount = amount,
            paymentMethod = method,
            accountNumber = accountNumber,
            referenceId = referenceId,
            status = "PENDING",
            note = "Deposit request via $method ($accountNumber, Ref: $referenceId)"
        )
        return dao.insertTransaction(transaction)
    }

    suspend fun requestWithdrawal(
        method: String,
        amount: Double,
        accountNumber: String
    ): Result<Long> {
        val profile = dao.getUserProfileOnce() ?: return Result.failure(Exception("User profile not found"))

        if (profile.balance < amount) {
            return Result.failure(Exception("Insufficient balance for withdrawal."))
        }

        // Debit immediately into pending state
        dao.debitUserBalance(amount)

        val transaction = WalletTransaction(
            type = "WITHDRAWAL",
            amount = amount,
            paymentMethod = method,
            accountNumber = accountNumber,
            status = "PENDING",
            note = "Withdrawal request to $method ($accountNumber)"
        )
        val txId = dao.insertTransaction(transaction)
        return Result.success(txId)
    }

    // Admin Operations
    suspend fun approveTransaction(tx: WalletTransaction) {
        if (tx.status != "PENDING") return

        val updatedTx = tx.copy(status = "COMPLETED")
        dao.updateTransaction(updatedTx)

        if (tx.type == "DEPOSIT") {
            dao.creditUserBalance(tx.amount)
        }
        // Withdrawal amount was already debited when request was made
    }

    suspend fun rejectTransaction(tx: WalletTransaction) {
        if (tx.status != "PENDING") return

        val updatedTx = tx.copy(status = "REJECTED")
        dao.updateTransaction(updatedTx)

        if (tx.type == "WITHDRAWAL") {
            // Refund debited withdrawal amount
            dao.creditUserBalance(tx.amount)
        }
    }

    suspend fun createDraw(
        title: String,
        category: String,
        jackpotAmount: Double,
        ticketPrice: Double,
        durationMinutes: Int,
        maxChoice: Int = 49,
        pickCount: Int = 6
    ): Long {
        val draw = LotteryDraw(
            title = title,
            category = category,
            jackpotAmount = jackpotAmount,
            ticketPrice = ticketPrice,
            drawTimeEpoch = System.currentTimeMillis() + durationMinutes * 60 * 1000L,
            status = "UPCOMING",
            maxNumberChoice = maxChoice,
            pickCount = pickCount
        )
        return dao.insertDraw(draw)
    }

    suspend fun triggerDrawExecution(draw: LotteryDraw, manualWinningNumbers: List<Int>? = null): List<Int> {
        val winningNumbers = manualWinningNumbers ?: run {
            val pool = (1..draw.maxNumberChoice).toList().shuffled()
            pool.take(draw.pickCount).sorted()
        }

        val winningCsv = winningNumbers.joinToString(",")

        // Mark draw as completed with winning numbers
        val updatedDraw = draw.copy(
            status = "COMPLETED",
            winningNumbersCsv = winningCsv
        )
        dao.updateDraw(updatedDraw)

        // Evaluate all tickets for this draw
        val tickets = dao.getTicketsForDraw(draw.id)
        val winningSet = winningNumbers.toSet()

        var totalPayout = 0.0

        tickets.forEach { ticket ->
            val chosen = ticket.chosenNumbersCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
            val matches = chosen.intersect(winningSet).size

            var prize = 0.0
            var status = "LOST"

            when (matches) {
                draw.pickCount -> {
                    prize = draw.jackpotAmount
                    status = "WON"
                }
                draw.pickCount - 1 -> {
                    prize = draw.jackpotAmount * 0.15
                    status = "WON"
                }
                draw.pickCount - 2 -> {
                    prize = draw.ticketPrice * 20.0
                    status = "WON"
                }
                draw.pickCount - 3 -> {
                    prize = draw.ticketPrice * 3.0
                    status = "WON"
                }
            }

            val updatedTicket = ticket.copy(
                status = status,
                prizeWon = prize,
                matchedCount = matches
            )
            dao.updateTicket(updatedTicket)

            if (prize > 0) {
                totalPayout += prize
                // Credit winning prize to user balance
                dao.creditUserBalance(prize)
                // Log winning transaction
                dao.insertTransaction(
                    WalletTransaction(
                        type = "PRIZE_WIN",
                        amount = prize,
                        paymentMethod = "System Prize",
                        status = "COMPLETED",
                        note = "Prize for ${draw.title} ($matches matches)"
                    )
                )
            }
        }

        return winningNumbers
    }

    suspend fun setLanguage(lang: String) {
        val profile = dao.getUserProfileOnce() ?: UserProfile()
        dao.insertOrUpdateProfile(profile.copy(language = lang))
    }
}
