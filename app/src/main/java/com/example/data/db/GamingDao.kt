package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GameHistory
import com.example.data.model.GameTournament
import com.example.data.model.TransactionRecord
import com.example.data.model.UserWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface GamingDao {

    // --- Wallet ---
    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    fun getWalletFlow(): Flow<UserWallet?>

    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    suspend fun getWallet(): UserWallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: UserWallet)

    @Update
    suspend fun updateWallet(wallet: UserWallet)

    // --- Transactions ---
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionRecord): Long

    // --- Tournaments ---
    @Query("SELECT * FROM game_tournaments WHERE isActive = 1 ORDER BY entryFee ASC")
    fun getAllTournamentsFlow(): Flow<List<GameTournament>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(tournaments: List<GameTournament>)

    @Query("SELECT COUNT(*) FROM game_tournaments")
    suspend fun getTournamentsCount(): Int

    // --- Game History ---
    @Query("SELECT * FROM game_history ORDER BY timestamp DESC LIMIT 30")
    fun getRecentGamesFlow(): Flow<List<GameHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameHistory(history: GameHistory): Long
}
