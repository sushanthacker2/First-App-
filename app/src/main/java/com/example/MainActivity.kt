package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainNavScreen
import com.example.ui.WinArenaViewModel
import com.example.ui.screens.game.LiveArenaGameScreen
import com.example.ui.screens.lobby.GameLobbyScreen
import com.example.ui.screens.wallet.WalletScreen
import com.example.ui.theme.ArenaBlack
import com.example.ui.theme.ArenaSurface
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {

    private val viewModel: WinArenaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WinArenaMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WinArenaMainApp(
    viewModel: WinArenaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentNav by viewModel.currentNav.collectAsStateWithLifecycle()
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val tournaments by viewModel.tournaments.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val activeTournament by viewModel.activeTournament.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackBarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("winarena_root_scaffold"),
        containerColor = ArenaBlack,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            // Only display navigation bar if not inside a live game
            if (currentNav != MainNavScreen.GAMEPLAY) {
                NavigationBar(
                    containerColor = ArenaSurface,
                    modifier = Modifier.testTag("winarena_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentNav == MainNavScreen.LOBBY,
                        onClick = { viewModel.setNav(MainNavScreen.LOBBY) },
                        icon = {
                            Icon(Icons.Default.SportsEsports, contentDescription = "Game Tournaments")
                        },
                        label = {
                            Text("Tournaments", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ArenaBlack,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tournaments_item")
                    )

                    NavigationBarItem(
                        selected = currentNav == MainNavScreen.WALLET,
                        onClick = { viewModel.setNav(MainNavScreen.WALLET) },
                        icon = {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet & Cashout")
                        },
                        label = {
                            val bal = wallet?.totalBalance ?: 0.0
                            Text("Wallet (₹${bal.toInt()})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ArenaBlack,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_wallet_item")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ArenaBlack)
        ) {
            AnimatedContent(
                targetState = currentNav,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { navScreen ->
                when (navScreen) {
                    MainNavScreen.LOBBY -> {
                        GameLobbyScreen(
                            wallet = wallet,
                            tournaments = tournaments,
                            onSelectTournament = { tournament ->
                                viewModel.startTournament(
                                    tournament = tournament,
                                    onInsufficientBalance = {
                                        Toast.makeText(
                                            context,
                                            "Insufficient balance for ₹${tournament.entryFee.toInt()} entry! Add cash via UPI.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        viewModel.setNav(MainNavScreen.WALLET)
                                    }
                                )
                            },
                            onNavigateWallet = { viewModel.setNav(MainNavScreen.WALLET) }
                        )
                    }
                    MainNavScreen.WALLET -> {
                        WalletScreen(
                            wallet = wallet,
                            transactions = transactions,
                            onAddDeposit = { amount, mode, ref ->
                                viewModel.addDeposit(amount, mode, ref)
                            },
                            onRequestWithdrawal = { amount, destType, destAddr ->
                                viewModel.requestWithdrawal(amount, destType, destAddr)
                            }
                        )
                    }
                    MainNavScreen.GAMEPLAY -> {
                        activeTournament?.let { activeTourney ->
                            LiveArenaGameScreen(
                                tournament = activeTourney,
                                onGameComplete = { playerScore, opponentScore, isWon ->
                                    viewModel.completeGame(playerScore, opponentScore, isWon)
                                },
                                onClose = { viewModel.exitGameEarly() }
                            )
                        } ?: run {
                            viewModel.exitGameEarly()
                        }
                    }
                }
            }
        }
    }
}
