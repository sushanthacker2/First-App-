package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CreationDao {
    @Query("SELECT * FROM creations ORDER BY timestamp DESC")
    fun getAllCreations(): Flow<List<CreationItem>>

    @Query("SELECT * FROM creations WHERE category = :category ORDER BY timestamp DESC")
    fun getCreationsByCategory(category: String): Flow<List<CreationItem>>

    @Query("SELECT * FROM creations WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteCreations(): Flow<List<CreationItem>>

    @Query("SELECT * FROM creations WHERE title LIKE '%' || :query || '%' OR prompt LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchCreations(query: String): Flow<List<CreationItem>>

    @Query("SELECT * FROM creations WHERE id = :id LIMIT 1")
    suspend fun getCreationById(id: Long): CreationItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(item: CreationItem): Long

    @Update
    suspend fun updateCreation(item: CreationItem)

    @Delete
    suspend fun deleteCreation(item: CreationItem)

    @Query("DELETE FROM creations WHERE id = :id")
    suspend fun deleteCreationById(id: Long)

    @Query("UPDATE creations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM creations")
    suspend fun getCount(): Int
}
