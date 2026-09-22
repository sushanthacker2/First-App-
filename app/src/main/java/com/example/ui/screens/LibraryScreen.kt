package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CreationItem
import com.example.ui.components.CreationCard
import com.example.ui.theme.CopilotCyan
import com.example.ui.theme.CreativeViolet
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioRose

@Composable
fun LibraryScreen(
    creations: List<CreationItem>,
    selectedCategory: String,
    searchQuery: String,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavorite: (CreationItem) -> Unit,
    onDeleteCreation: (CreationItem) -> Unit,
    onPlayTts: (String) -> Unit,
    isPlayingTts: Boolean,
    onStopTts: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyFavorites by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "SCRIPT", "VISUAL", "AUDIO", "SOCIAL")

    val displayedCreations = creations.filter {
        if (showOnlyFavorites) it.isFavorite else true
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("library_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Library Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(StudioRose.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = StudioRose,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Studio Asset Library",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = "${creations.size} local creations saved on device",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Favorites Filter Toggle
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (showOnlyFavorites) StudioRose.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showOnlyFavorites = !showOnlyFavorites }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (showOnlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Filter favorites",
                                    tint = if (showOnlyFavorites) StudioRose else Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Favorites",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (showOnlyFavorites) StudioRose else Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_input"),
                placeholder = { Text("Search creations, prompts, tags, or scripts...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CopilotCyan
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CopilotCyan,
                    unfocusedBorderColor = Color(0xFF2E3B52),
                    focusedContainerColor = Color(0xFF0F1524),
                    unfocusedContainerColor = Color(0xFF0F1524),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFFE2E8F0)
                )
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    val chipColor = when (cat) {
                        "SCRIPT" -> CreativeViolet
                        "VISUAL" -> CopilotCyan
                        "AUDIO" -> StudioAmber
                        "SOCIAL" -> StudioRose
                        else -> CopilotCyan
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) chipColor else Color(0xFF1E293B),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectCategory(cat) }
                            .testTag("filter_chip_$cat")
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) Color(0xFF0A0E17) else Color(0xFFE2E8F0),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Empty State or List
        if (displayedCreations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No creations found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing your search or generate a new concept in Visual Studio or Script Copilot.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                }
            }
        } else {
            items(displayedCreations, key = { it.id }) { item ->
                CreationCard(
                    item = item,
                    onToggleFavorite = { onToggleFavorite(item) },
                    onDelete = { onDeleteCreation(item) },
                    onPlayTts = onPlayTts,
                    isPlayingTts = isPlayingTts,
                    onStopTts = onStopTts
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
