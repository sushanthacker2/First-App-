package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class UserWallet(
    @PrimaryKey val id: Int = 1,
    val depositBalance: Double = 50.0,    // Money added via UPI
    val winningsBalance: Double = 120.0,  // Winnings eligible for bank/UPI withdrawal
    val bonusBalance: Double = 25.0,     // Promotional bonus usable for tournament entry discounts
    val kycStatus: String = "VERIFIED",  // PENDING, VERIFIED, REJECTED
    val upiId: String = "player@oksbi",
    val bankAccount: String = "987654321012",
    val ifscCode: String = "SBIN0001234",
    val accountHolderName: String = "Pro Gamer",
    val totalGamesPlayed: Int = 14,
    val totalWonAmount: Double = 640.0
) {
    val totalBalance: Double
        get() = depositBalance + winningsBalance + bonusBalance
}

enum class TransactionType {
    DEPOSIT,      // Added money
    ENTRY_FEE,    // Paid entry fee
    WINNING,      // Won game prize
    WITHDRAWAL,   // Cashout to UPI/Bank
    BONUS_CREDIT  // Referral or daily rewards
}

enum class TransactionStatus {
    SUCCESS,
    PENDING,
    PROCESSING,
    FAILED
}

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: TransactionType,
    val amount: Double,
    val status: TransactionStatus,
    val paymentMode: String, // "UPI (GPay/PhonePe)", "Paytm Wallet", "IMPS Bank Transfer", "Game Winnings"
    val referenceId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val remarks: String = ""
)

@Entity(tableName = "game_tournaments")
data class GameTournament(
    @PrimaryKey val id: String,
    val gameTitle: String,
    val gameType: GameType,
    val entryFee: Double,        // ₹10, ₹50, ₹100, ₹500, ₹1000, ₹5000, ₹10000
    val prizePool: Double,       // e.g. ₹18 for ₹10, ₹90 for ₹50, etc.
    val maxPlayers: Int = 2,     // 1v1 Battle or Battle Royale
    val currentPlayers: Int = 1,
    val durationSeconds: Int = 30,
    val difficulty: String = "Normal",
    val isActive: Boolean = true
)

enum class GameType {
    REFLEX_TAP,       // Speed Reflex Tap challenge
    MATH_NINJA,       // High-speed mental math blitz
    COLOR_SWITCH,     // Stroop cognitive color challenge
    TARGET_STRIKE     // Precision timing knife / dart strike
}

@Entity(tableName = "game_history")
data class GameHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameType: GameType,
    val gameTitle: String,
    val entryFee: Double,
    val score: Int,
    val opponentScore: Int,
    val wonAmount: Double,
    val isWin: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
