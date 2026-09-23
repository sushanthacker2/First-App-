package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GamingRepository
import com.example.data.model.GameHistory
import com.example.data.model.GameTournament
import com.example.data.model.TransactionRecord
import com.example.data.model.UserWallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavScreen {
    LOBBY,
    WALLET,
    GAMEPLAY
}

class WinArenaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = GamingRepository(database.gamingDao())

    val wallet: StateFlow<UserWallet?> = repository.walletFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<TransactionRecord>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tournaments: StateFlow<List<GameTournament>> = repository.tournamentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentGames: StateFlow<List<GameHistory>> = repository.recentGamesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentNav = MutableStateFlow(MainNavScreen.LOBBY)
    val currentNav: StateFlow<MainNavScreen> = _currentNav.asStateFlow()

    private val _activeTournament = MutableStateFlow<GameTournament?>(null)
    val activeTournament: StateFlow<GameTournament?> = _activeTournament.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.initializeDefaultsIfNeeded()
            } catch (e: Exception) {
                // Defensive initialization
            }
        }
    }

    fun setNav(nav: MainNavScreen) {
        _currentNav.value = nav
    }

    fun startTournament(tournament: GameTournament, onInsufficientBalance: () -> Unit) {
        viewModelScope.launch {
            val result = repository.payEntryFee(tournament.entryFee, tournament.gameTitle)
            if (result.isSuccess) {
                _activeTournament.value = tournament
                _currentNav.value = MainNavScreen.GAMEPLAY
            } else {
                _snackBarMessage.value = result.exceptionOrNull()?.message ?: "Insufficient balance"
                onInsufficientBalance()
            }
        }
    }

    fun completeGame(playerScore: Int, opponentScore: Int, isWon: Boolean) {
        val tournament = _activeTournament.value ?: return
        viewModelScope.launch {
            if (isWon) {
                repository.creditGameWin(
                    gameType = tournament.gameType,
                    gameTitle = tournament.gameTitle,
                    entryFee = tournament.entryFee,
                    prizeAmount = tournament.prizePool,
                    playerScore = playerScore,
                    opponentScore = opponentScore
                )
            } else {
                repository.recordGameLoss(
                    gameType = tournament.gameType,
                    gameTitle = tournament.gameTitle,
                    entryFee = tournament.entryFee,
                    playerScore = playerScore,
                    opponentScore = opponentScore
                )
            }
            _activeTournament.value = null
            _currentNav.value = MainNavScreen.LOBBY
        }
    }

    fun exitGameEarly() {
        _activeTournament.value = null
        _currentNav.value = MainNavScreen.LOBBY
    }

    fun addDeposit(amount: Double, paymentMode: String, upiRef: String) {
        viewModelScope.launch {
            repository.addDeposit(amount, paymentMode, upiRef)
            _snackBarMessage.value = "₹${amount.toInt()} Added Successfully via $paymentMode!"
        }
    }

    fun requestWithdrawal(amount: Double, destType: String, destAddress: String) {
        viewModelScope.launch {
            val res = repository.requestWithdrawal(amount, destType, destAddress)
            if (res.isSuccess) {
                _snackBarMessage.value = "Withdrawal of ₹${amount.toInt()} sent to $destType!"
            } else {
                _snackBarMessage.value = res.exceptionOrNull()?.message ?: "Withdrawal failed"
            }
        }
    }

    fun clearSnackbar() {
        _snackBarMessage.value = null
    }
}
