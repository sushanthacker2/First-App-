package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creations")
data class CreationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // SCRIPT, VISUAL, AUDIO, SOCIAL
    val prompt: String,
    val content: String,
    val style: String = "Default",
    val aspectRatio: String = "16:9",
    val drawableName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val tags: String = ""
)
