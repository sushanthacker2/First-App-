package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.db.GamingDao
import com.example.data.model.GameHistory
import com.example.data.model.GameTournament
import com.example.data.model.TransactionRecord
import com.example.data.model.UserWallet

@Database(
    entities = [
        UserWallet::class,
        TransactionRecord::class,
        GameTournament::class,
        GameHistory::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gamingDao(): GamingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "winarena_gaming_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
