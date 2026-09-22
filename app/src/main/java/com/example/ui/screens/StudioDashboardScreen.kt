package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CreationItem
import com.example.ui.StudioScreen
import com.example.ui.components.CreationCard
import com.example.ui.components.NpuStatusBar
import com.example.ui.theme.CopilotCyan
import com.example.ui.theme.CreativeViolet
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioRose

@Composable
fun StudioDashboardScreen(
    creations: List<CreationItem>,
    isLocalNpuOnly: Boolean,
    npuTops: Int,
    latencyMs: Int,
    onToggleNpuMode: () -> Unit,
    onNavigateToScreen: (StudioScreen) -> Unit,
    onToggleFavorite: (CreationItem) -> Unit,
    onDeleteCreation: (CreationItem) -> Unit,
    onPlayTts: (String) -> Unit,
    isPlayingTts: Boolean,
    onStopTts: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top NPU Live Telemetry
        item {
            NpuStatusBar(
                isLocalNpuOnly = isLocalNpuOnly,
                npuTops = npuTops,
                latencyMs = latencyMs,
                onToggleMode = onToggleNpuMode
            )
        }

        // Hero Workstation Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_creator_studio),
                        contentDescription = "HP Copilot Creator Studio Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xFF0B0F19).copy(alpha = 0.85f),
                                        Color(0xFF0B0F19)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CopilotCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "HP COPILOT LOCAL ENGINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CopilotCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Next-Gen Local Creator Suite",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )

                        Text(
                            text = "Instant video scripts, concept visuals, & neural voiceovers.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Quick Creator Tool Stations
        item {
            Text(
                text = "Creator Workstations",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudioToolCard(
                        title = "Visual Studio",
                        subtitle = "Concept art & storyboards",
                        icon = Icons.Default.Image,
                        accentColor = CopilotCyan,
                        bgColor = Color(0xFF0C2436),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToScreen(StudioScreen.VISUAL) }
                    )

                    StudioToolCard(
                        title = "Script Copilot",
                        subtitle = "YouTube & Shorts writer",
                        icon = Icons.Default.Videocam,
                        accentColor = CreativeViolet,
                        bgColor = Color(0xFF281545),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToScreen(StudioScreen.SCRIPTS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudioToolCard(
                        title = "Voice & Audio",
                        subtitle = "Speech & BGM pacing",
                        icon = Icons.Default.GraphicEq,
                        accentColor = StudioAmber,
                        bgColor = Color(0xFF33200B),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToScreen(StudioScreen.AUDIO) }
                    )

                    StudioToolCard(
                        title = "Content Library",
                        subtitle = "Saved drafts & exports",
                        icon = Icons.Default.Campaign,
                        accentColor = StudioRose,
                        bgColor = Color(0xFF3B0D1D),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToScreen(StudioScreen.LIBRARY) }
                    )
                }
            }
        }

        // On-Device AI Architecture specs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Copilot+ Silicon Engine",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CopilotCyan.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "NPU ACCELERATED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CopilotCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecItem(label = "Compute", value = "$npuTops TOPS", icon = Icons.Default.Speed)
                        SpecItem(label = "Latency", value = "${latencyMs}ms", icon = Icons.Default.Bolt)
                        SpecItem(label = "Privacy", value = "100% Local", icon = Icons.Default.Lock)
                    }
                }
            }
        }

        // Recent Studio Projects
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Creations (${creations.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp
                    )
                )

                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CopilotCyan,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable { onNavigateToScreen(StudioScreen.LIBRARY) }
                )
            }
        }

        items(creations.take(4), key = { it.id }) { item ->
            CreationCard(
                item = item,
                onToggleFavorite = { onToggleFavorite(item) },
                onDelete = { onDeleteCreation(item) },
                onPlayTts = onPlayTts,
                isPlayingTts = isPlayingTts,
                onStopTts = onStopTts
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StudioToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("tool_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(accentColor.copy(alpha = 0.4f), Color.Transparent)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun SpecItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = CopilotCyan,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        )
    }
}
