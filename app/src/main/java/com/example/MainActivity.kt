package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.LotteryRepository
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = LotteryRepository(database.lotteryDao())
        val viewModelFactory = LotteryViewModelFactory(repository)

        setContent {
            LottoWinTheme {
                val viewModel: LotteryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = viewModelFactory
                )
                LottoWinApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LottoWinApp(viewModel: LotteryViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val upcomingDraws by viewModel.upcomingDraws.collectAsStateWithLifecycle()
    val completedDraws by viewModel.completedDraws.collectAsStateWithLifecycle()
    val allTickets by viewModel.allTickets.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val selectedDrawForPurchase by viewModel.selectedDrawForPurchase.collectAsStateWithLifecycle()
    val selectedNumbers by viewModel.selectedNumbers.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val isDrawingInProgress by viewModel.isDrawingInProgress.collectAsStateWithLifecycle()
    val liveDrawnNumbers by viewModel.liveDrawnNumbers.collectAsStateWithLifecycle()
    val adminUnlocked by viewModel.adminUnlocked.collectAsStateWithLifecycle()

    val language = userProfile?.language ?: "EN"
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = Gold80
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Gold80,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = "Logo",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = Strings.getText("app_title", language),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Gold80
                            )
                        )
                    }
                },
                actions = {
                    // Wallet Balance Badge
                    Surface(
                        color = RoyalIndigo,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable { viewModel.setTab(AppTab.WALLET) }
                            .testTag("top_bar_wallet_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = Gold80,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "৳ ${String.format("%,.0f", userProfile?.balance ?: 0.0)}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Language Switcher Toggle
                    IconButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("language_toggle_button")
                    ) {
                        Surface(
                            color = DarkSurfaceVariant,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (language == "BN") "EN" else "বাং",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Gold80
                                    )
                                )
                            }
                        }
                    }

                    // Admin Mode Toggle Switch
                    IconButton(
                        onClick = { viewModel.toggleAdminMode(!adminUnlocked) },
                        modifier = Modifier.testTag("admin_mode_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (adminUnlocked) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                            contentDescription = "Admin Toggle",
                            tint = if (adminUnlocked) EmeraldGreen else Color.Gray
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.HOME,
                    onClick = { viewModel.setTab(AppTab.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(Strings.getText("home", language)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold80,
                        selectedTextColor = Gold80,
                        indicatorColor = RoyalIndigo
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.MY_TICKETS,
                    onClick = { viewModel.setTab(AppTab.MY_TICKETS) },
                    icon = {
                        BadgedBox(badge = {
                            if (allTickets.isNotEmpty()) {
                                Badge(containerColor = Gold80) {
                                    Text(allTickets.size.toString(), color = Color.Black)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = "Tickets")
                        }
                    },
                    label = { Text(Strings.getText("tickets", language)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold80,
                        selectedTextColor = Gold80,
                        indicatorColor = RoyalIndigo
                    ),
                    modifier = Modifier.testTag("nav_tickets")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.RESULTS,
                    onClick = { viewModel.setTab(AppTab.RESULTS) },
                    icon = { Icon(Icons.Default.Casino, contentDescription = "Results") },
                    label = { Text(Strings.getText("results", language)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold80,
                        selectedTextColor = Gold80,
                        indicatorColor = RoyalIndigo
                    ),
                    modifier = Modifier.testTag("nav_results")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.WALLET,
                    onClick = { viewModel.setTab(AppTab.WALLET) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                    label = { Text(Strings.getText("wallet", language)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Gold80,
                        selectedTextColor = Gold80,
                        indicatorColor = RoyalIndigo
                    ),
                    modifier = Modifier.testTag("nav_wallet")
                )

                if (adminUnlocked) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.ADMIN,
                        onClick = { viewModel.setTab(AppTab.ADMIN) },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                        label = { Text(Strings.getText("admin", language)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldGreen,
                            selectedTextColor = EmeraldGreen,
                            indicatorColor = RoyalIndigo
                        ),
                        modifier = Modifier.testTag("nav_admin")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> HomeScreen(
                    upcomingDraws = upcomingDraws,
                    language = language,
                    onBuyTicketClick = { draw -> viewModel.selectDrawForPurchase(draw) },
                    onViewResultsClick = { viewModel.setTab(AppTab.RESULTS) },
                    onDepositClick = { viewModel.setTab(AppTab.WALLET) }
                )

                AppTab.MY_TICKETS -> MyTicketsScreen(
                    tickets = allTickets,
                    language = language
                )

                AppTab.RESULTS -> LiveDrawResultsScreen(
                    completedDraws = completedDraws,
                    isDrawingInProgress = isDrawingInProgress,
                    liveDrawnNumbers = liveDrawnNumbers,
                    language = language
                )

                AppTab.WALLET -> WalletScreen(
                    userProfile = userProfile,
                    transactions = allTransactions,
                    language = language,
                    onDepositSubmit = { method, amount, account, refId ->
                        viewModel.depositMoney(method, amount, account, refId)
                    },
                    onWithdrawSubmit = { method, amount, account ->
                        viewModel.withdrawMoney(method, amount, account)
                    }
                )

                AppTab.ADMIN -> AdminPanelScreen(
                    upcomingDraws = upcomingDraws,
                    completedDraws = completedDraws,
                    transactions = allTransactions,
                    tickets = allTickets,
                    language = language,
                    onCreateDraw = { title, category, jackpot, price, minutes ->
                        viewModel.createNewDraw(title, category, jackpot, price, minutes)
                    },
                    onTriggerDraw = { draw, customNumbers ->
                        viewModel.triggerLiveDraw(draw, customNumbers)
                        viewModel.setTab(AppTab.RESULTS)
                    },
                    onApproveTx = { tx -> viewModel.approveTransaction(tx) },
                    onRejectTx = { tx -> viewModel.rejectTransaction(tx) }
                )
            }
        }
    }

    // Modal Sheet for Purchasing Tickets
    selectedDrawForPurchase?.let { draw ->
        BuyTicketSheet(
            draw = draw,
            selectedNumbers = selectedNumbers,
            walletBalance = userProfile?.balance ?: 0.0,
            language = language,
            onNumberToggle = { num -> viewModel.toggleNumberSelection(num, draw.pickCount) },
            onQuickPick = { viewModel.quickPickRandom(draw.maxNumberChoice, draw.pickCount) },
            onConfirmPurchase = { viewModel.buyTicket() },
            onDismiss = { viewModel.selectDrawForPurchase(null) }
        )
    }
}
