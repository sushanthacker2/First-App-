package com.example.data

import com.example.data.db.GamingDao
import com.example.data.model.GameHistory
import com.example.data.model.GameTournament
import com.example.data.model.GameType
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserWallet
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GamingRepository(private val dao: GamingDao) {

    val walletFlow: Flow<UserWallet?> = dao.getWalletFlow()
    val transactionsFlow: Flow<List<TransactionRecord>> = dao.getAllTransactionsFlow()
    val tournamentsFlow: Flow<List<GameTournament>> = dao.getAllTournamentsFlow()
    val recentGamesFlow: Flow<List<GameHistory>> = dao.getRecentGamesFlow()

    suspend fun initializeDefaultsIfNeeded() {
        val currentWallet = dao.getWallet()
        if (currentWallet == null) {
            val initialWallet = UserWallet(
                id = 1,
                depositBalance = 100.0,
                winningsBalance = 250.0,
                bonusBalance = 50.0,
                kycStatus = "VERIFIED",
                upiId = "gamer@upi",
                bankAccount = "XXXXXX4589",
                ifscCode = "HDFC0000128",
                accountHolderName = "Champion Player",
                totalGamesPlayed = 8,
                totalWonAmount = 720.0
            )
            dao.insertOrUpdateWallet(initialWallet)

            // Seed initial transactions
            dao.insertTransaction(
                TransactionRecord(
                    title = "Welcome Cash Bonus",
                    type = TransactionType.BONUS_CREDIT,
                    amount = 50.0,
                    status = TransactionStatus.SUCCESS,
                    paymentMode = "WinArena Rewards",
                    referenceId = "WA-BONUS-9912",
                    timestamp = System.currentTimeMillis() - 86400000,
                    remarks = "Free Signup & Referral Bonus credited"
                )
            )
            dao.insertTransaction(
                TransactionRecord(
                    title = "UPI Deposit Added",
                    type = TransactionType.DEPOSIT,
                    amount = 100.0,
                    status = TransactionStatus.SUCCESS,
                    paymentMode = "UPI (Google Pay / PhonePe)",
                    referenceId = "UPI-TXN-827391823",
                    timestamp = System.currentTimeMillis() - 43200000,
                    remarks = "100% Connectivity Instant Wallet Topup"
                )
            )
            dao.insertTransaction(
                TransactionRecord(
                    title = "Reflex Tap Championship Won",
                    type = TransactionType.WINNING,
                    amount = 250.0,
                    status = TransactionStatus.SUCCESS,
                    paymentMode = "Tournament Winnings",
                    referenceId = "MATCH-WIN-18274",
                    timestamp = System.currentTimeMillis() - 10800000,
                    remarks = "1st Place 1v1 Battle victory!"
                )
            )
        }

        if (dao.getTournamentsCount() == 0) {
            val tournaments = listOf(
                // ₹10 Entry Fee Tier (Starting minimum requested by user)
                GameTournament(
                    id = "T-REFLEX-10",
                    gameTitle = "Reflex Tap Blitz",
                    gameType = GameType.REFLEX_TAP,
                    entryFee = 10.0,
                    prizePool = 18.0,
                    difficulty = "Beginner",
                    durationSeconds = 25
                ),
                GameTournament(
                    id = "T-MATH-10",
                    gameTitle = "Math Speed Battle",
                    gameType = GameType.MATH_NINJA,
                    entryFee = 10.0,
                    prizePool = 18.0,
                    difficulty = "Normal",
                    durationSeconds = 30
                ),
                // ₹50 Entry Fee Tier
                GameTournament(
                    id = "T-COLOR-50",
                    gameTitle = "Color Stroop Clash",
                    gameType = GameType.COLOR_SWITCH,
                    entryFee = 50.0,
                    prizePool = 90.0,
                    difficulty = "Normal",
                    durationSeconds = 25
                ),
                // ₹100 Entry Fee Tier
                GameTournament(
                    id = "T-TARGET-100",
                    gameTitle = "Target Strike Arena",
                    gameType = GameType.TARGET_STRIKE,
                    entryFee = 100.0,
                    prizePool = 180.0,
                    difficulty = "Pro",
                    durationSeconds = 30
                ),
                // ₹500 Tier
                GameTournament(
                    id = "T-REFLEX-500",
                    gameTitle = "Grand Master Reflex",
                    gameType = GameType.REFLEX_TAP,
                    entryFee = 500.0,
                    prizePool = 920.0,
                    difficulty = "Challenger",
                    durationSeconds = 30
                ),
                // ₹1000 Tier
                GameTournament(
                    id = "T-MATH-1000",
                    gameTitle = "Math Prodigy Cup",
                    gameType = GameType.MATH_NINJA,
                    entryFee = 1000.0,
                    prizePool = 1850.0,
                    difficulty = "Hard",
                    durationSeconds = 35
                ),
                // ₹5000 Tier
                GameTournament(
                    id = "T-TARGET-5000",
                    gameTitle = "High Roller Target Showdown",
                    gameType = GameType.TARGET_STRIKE,
                    entryFee = 5000.0,
                    prizePool = 9300.0,
                    difficulty = "Elite",
                    durationSeconds = 30
                ),
                // ₹10000 Tier (Maximum requested by user)
                GameTournament(
                    id = "T-CHAMPION-10000",
                    gameTitle = "Apex Champions Mega Pot",
                    gameType = GameType.REFLEX_TAP,
                    entryFee = 10000.0,
                    prizePool = 18800.0,
                    difficulty = "Legendary",
                    durationSeconds = 30
                )
            )
            dao.insertTournaments(tournaments)
        }
    }

    suspend fun addDeposit(amount: Double, paymentMode: String, upiRef: String): Result<UserWallet> {
        val current = dao.getWallet() ?: return Result.failure(Exception("Wallet not found"))
        val updated = current.copy(depositBalance = current.depositBalance + amount)
        dao.updateWallet(updated)

        dao.insertTransaction(
            TransactionRecord(
                title = "Money Added via $paymentMode",
                type = TransactionType.DEPOSIT,
                amount = amount,
                status = TransactionStatus.SUCCESS,
                paymentMode = paymentMode,
                referenceId = upiRef.ifBlank { "UPI-${System.currentTimeMillis()}" },
                remarks = "Deposit confirmed and credited to game wallet"
            )
        )
        return Result.success(updated)
    }

    suspend fun requestWithdrawal(
        amount: Double,
        destinationType: String, // "UPI" or "BANK"
        destinationAddress: String
    ): Result<String> {
        val current = dao.getWallet() ?: return Result.failure(Exception("Wallet not initialized"))

        if (amount < 50.0) {
            return Result.failure(Exception("Minimum withdrawal limit is ₹50"))
        }
        if (amount > 10000.0) {
            return Result.failure(Exception("Maximum withdrawal per transaction is ₹10,000"))
        }
        if (current.winningsBalance < amount) {
            return Result.failure(Exception("Insufficient Winnings balance! (Available: ₹${current.winningsBalance}) Note: Only game winnings can be withdrawn per Indian gaming regulations."))
        }

        val updated = current.copy(winningsBalance = current.winningsBalance - amount)
        dao.updateWallet(updated)

        val refId = "WD-INR-${UUID.randomUUID().toString().take(8).uppercase()}"
        dao.insertTransaction(
            TransactionRecord(
                title = "Instant Withdrawal to $destinationType",
                type = TransactionType.WITHDRAWAL,
                amount = amount,
                status = TransactionStatus.SUCCESS,
                paymentMode = "$destinationType ($destinationAddress)",
                referenceId = refId,
                remarks = "Processed instantly via 24x7 IMPS/UPI Payout Rail"
            )
        )

        return Result.success(refId)
    }

    suspend fun payEntryFee(fee: Double, gameTitle: String): Result<Boolean> {
        val current = dao.getWallet() ?: return Result.failure(Exception("Wallet not found"))
        if (current.totalBalance < fee) {
            return Result.failure(Exception("Insufficient total balance (₹${current.totalBalance}). Please add funds via UPI."))
        }

        // Deduct priority: Bonus (up to 10%), then Deposit, then Winnings
        var remaining = fee
        var newBonus = current.bonusBalance
        var newDeposit = current.depositBalance
        var newWinnings = current.winningsBalance

        val usableBonus = (fee * 0.10).coerceAtMost(newBonus)
        newBonus -= usableBonus
        remaining -= usableBonus

        if (newDeposit >= remaining) {
            newDeposit -= remaining
            remaining = 0.0
        } else {
            remaining -= newDeposit
            newDeposit = 0.0
            newWinnings -= remaining
        }

        val updated = current.copy(
            depositBalance = newDeposit,
            winningsBalance = newWinnings,
            bonusBalance = newBonus,
            totalGamesPlayed = current.totalGamesPlayed + 1
        )
        dao.updateWallet(updated)

        dao.insertTransaction(
            TransactionRecord(
                title = "Entry Fee: $gameTitle",
                type = TransactionType.ENTRY_FEE,
                amount = fee,
                status = TransactionStatus.SUCCESS,
                paymentMode = "Wallet Deduction",
                referenceId = "ENTRY-${System.currentTimeMillis().toString().takeLast(6)}",
                remarks = "Joined match tournament"
            )
        )
        return Result.success(true)
    }

    suspend fun creditGameWin(
        gameType: GameType,
        gameTitle: String,
        entryFee: Double,
        prizeAmount: Double,
        playerScore: Int,
        opponentScore: Int
    ) {
        val current = dao.getWallet() ?: return
        val updated = current.copy(
            winningsBalance = current.winningsBalance + prizeAmount,
            totalWonAmount = current.totalWonAmount + prizeAmount
        )
        dao.updateWallet(updated)

        dao.insertTransaction(
            TransactionRecord(
                title = "Match Victory: $gameTitle",
                type = TransactionType.WINNING,
                amount = prizeAmount,
                status = TransactionStatus.SUCCESS,
                paymentMode = "Game Prize Pool",
                referenceId = "WIN-${System.currentTimeMillis().toString().takeLast(6)}",
                remarks = "Scored $playerScore vs $opponentScore. Prize added to Winnings balance."
            )
        )

        dao.insertGameHistory(
            GameHistory(
                gameType = gameType,
                gameTitle = gameTitle,
                entryFee = entryFee,
                score = playerScore,
                opponentScore = opponentScore,
                wonAmount = prizeAmount,
                isWin = true
            )
        )
    }

    suspend fun recordGameLoss(
        gameType: GameType,
        gameTitle: String,
        entryFee: Double,
        playerScore: Int,
        opponentScore: Int
    ) {
        dao.insertGameHistory(
            GameHistory(
                gameType = gameType,
                gameTitle = gameTitle,
                entryFee = entryFee,
                score = playerScore,
                opponentScore = opponentScore,
                wonAmount = 0.0,
                isWin = false
            )
        )
    }

    suspend fun updateKyc(upi: String, bank: String, ifsc: String, name: String) {
        val current = dao.getWallet() ?: return
        val updated = current.copy(
            upiId = upi,
            bankAccount = bank,
            ifscCode = ifsc,
            accountHolderName = name,
            kycStatus = "VERIFIED"
        )
        dao.updateWallet(updated)
    }
}
