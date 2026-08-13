package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppTab {
    HOME, MY_TICKETS, RESULTS, WALLET, ADMIN
}

class LotteryViewModel(private val repository: LotteryRepository) : ViewModel() {

    val upcomingDraws: StateFlow<List<LotteryDraw>> = repository.upcomingDraws
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedDraws: StateFlow<List<LotteryDraw>> = repository.completedDraws
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTickets: StateFlow<List<Ticket>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<WalletTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation state
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Ticket Purchase Dialog State
    private val _selectedDrawForPurchase = MutableStateFlow<LotteryDraw?>(null)
    val selectedDrawForPurchase: StateFlow<LotteryDraw?> = _selectedDrawForPurchase.asStateFlow()

    private val _selectedNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val selectedNumbers: StateFlow<Set<Int>> = _selectedNumbers.asStateFlow()

    // Snackbars / Feedback Messages
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Live Drawing Animation State
    private val _isDrawingInProgress = MutableStateFlow(false)
    val isDrawingInProgress: StateFlow<Boolean> = _isDrawingInProgress.asStateFlow()

    private val _liveDrawnNumbers = MutableStateFlow<List<Int>>(emptyList())
    val liveDrawnNumbers: StateFlow<List<Int>> = _liveDrawnNumbers.asStateFlow()

    // Admin State
    private val _adminUnlocked = MutableStateFlow(false)
    val adminUnlocked: StateFlow<Boolean> = _adminUnlocked.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectDrawForPurchase(draw: LotteryDraw?) {
        _selectedDrawForPurchase.value = draw
        _selectedNumbers.value = emptySet()
    }

    fun toggleNumberSelection(number: Int, maxPick: Int) {
        val current = _selectedNumbers.value.toMutableSet()
        if (current.contains(number)) {
            current.remove(number)
        } else {
            if (current.size < maxPick) {
                current.add(number)
            } else {
                _userMessage.value = "You can only select up to $maxPick numbers."
            }
        }
        _selectedNumbers.value = current
    }

    fun quickPickRandom(maxChoice: Int, pickCount: Int) {
        val numbers = (1..maxChoice).toList().shuffled().take(pickCount).toSet()
        _selectedNumbers.value = numbers
    }

    fun buyTicket() {
        val draw = _selectedDrawForPurchase.value ?: return
        val numbers = _selectedNumbers.value

        if (numbers.size < draw.pickCount) {
            _userMessage.value = "Please select exactly ${draw.pickCount} numbers."
            return
        }

        viewModelScope.launch {
            val result = repository.buyTicket(draw, numbers.toList())
            result.onSuccess {
                _userMessage.value = "Ticket purchased successfully! Good luck 🎉"
                _selectedDrawForPurchase.value = null
                _selectedNumbers.value = emptySet()
            }.onFailure { ex ->
                _userMessage.value = ex.message ?: "Purchase failed."
            }
        }
    }

    fun depositMoney(method: String, amount: Double, accountNumber: String, referenceId: String) {
        if (amount <= 0 || accountNumber.isBlank() || referenceId.isBlank()) {
            _userMessage.value = "Please fill in all deposit details correctly."
            return
        }

        viewModelScope.launch {
            repository.requestDeposit(method, amount, accountNumber, referenceId)
            _userMessage.value = "Deposit request submitted! Pending admin approval."
        }
    }

    fun withdrawMoney(method: String, amount: Double, accountNumber: String) {
        if (amount <= 0 || accountNumber.isBlank()) {
            _userMessage.value = "Please fill in all withdrawal details."
            return
        }

        viewModelScope.launch {
            val result = repository.requestWithdrawal(method, amount, accountNumber)
            result.onSuccess {
                _userMessage.value = "Withdrawal request submitted! Processing."
            }.onFailure { ex ->
                _userMessage.value = ex.message ?: "Withdrawal failed."
            }
        }
    }

    // Admin Panel Methods
    fun toggleAdminMode(unlock: Boolean) {
        _adminUnlocked.value = unlock
    }

    fun createNewDraw(
        title: String,
        category: String,
        jackpotAmount: Double,
        ticketPrice: Double,
        durationMinutes: Int
    ) {
        if (title.isBlank() || jackpotAmount <= 0 || ticketPrice <= 0 || durationMinutes <= 0) {
            _userMessage.value = "Please provide valid draw configuration."
            return
        }

        viewModelScope.launch {
            repository.createDraw(
                title = title,
                category = category,
                jackpotAmount = jackpotAmount,
                ticketPrice = ticketPrice,
                durationMinutes = durationMinutes
            )
            _userMessage.value = "New draw '$title' created successfully!"
        }
    }

    fun approveTransaction(tx: WalletTransaction) {
        viewModelScope.launch {
            repository.approveTransaction(tx)
            _userMessage.value = "Transaction #${tx.id} approved!"
        }
    }

    fun rejectTransaction(tx: WalletTransaction) {
        viewModelScope.launch {
            repository.rejectTransaction(tx)
            _userMessage.value = "Transaction #${tx.id} rejected."
        }
    }

    fun triggerLiveDraw(draw: LotteryDraw, manualWinningNumbers: List<Int>? = null) {
        viewModelScope.launch {
            _isDrawingInProgress.value = true
            _liveDrawnNumbers.value = emptyList()

            // Calculate or get target winning numbers
            val finalWinning = manualWinningNumbers ?: run {
                (1..draw.maxNumberChoice).toList().shuffled().take(draw.pickCount).sorted()
            }

            // Animate revealing balls one by one
            for (num in finalWinning) {
                delay(1200) // delay between revealing each ball
                _liveDrawnNumbers.value = _liveDrawnNumbers.value + num
            }

            delay(1000)

            // Finalize draw in DB
            repository.triggerDrawExecution(draw, finalWinning)

            _isDrawingInProgress.value = false
            _userMessage.value = "Draw '${draw.title}' completed! Winning numbers: ${finalWinning.joinToString(", ")}"
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val currentLang = userProfile.value?.language ?: "EN"
            val newLang = if (currentLang == "EN") "BN" else "EN"
            repository.setLanguage(newLang)
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
