package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CreationItem
import com.example.ui.theme.CopilotCyan
import com.example.ui.theme.CreativeViolet
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioRose

@Composable
fun CreationCard(
    item: CreationItem,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onPlayTts: (String) -> Unit = {},
    isPlayingTts: Boolean = false,
    onStopTts: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    val categoryColor = when (item.category) {
        "SCRIPT" -> CreativeViolet
        "VISUAL" -> CopilotCyan
        "AUDIO" -> StudioAmber
        "SOCIAL" -> StudioRose
        else -> MaterialTheme.colorScheme.primary
    }

    val categoryBg = when (item.category) {
        "SCRIPT" -> Color(0xFF3B166A)
        "VISUAL" -> Color(0xFF003844)
        "AUDIO" -> Color(0xFF422006)
        "SOCIAL" -> Color(0xFF4C0519)
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("creation_card_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF131B2E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Optional Image Preview if attached
            if (item.drawableName.isNotBlank()) {
                val drawableId = when (item.drawableName) {
                    "banner_creator_studio" -> R.drawable.banner_creator_studio
                    "sample_cyberpunk" -> R.drawable.sample_cyberpunk
                    "sample_landscape" -> R.drawable.sample_landscape
                    else -> 0
                }

                if (drawableId != 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    ) {
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Aspect Ratio badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = item.aspectRatio,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Category & Style Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = categoryBg
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = categoryColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (item.style.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Text(
                                    text = item.style,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Favorite Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("favorite_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle favorite",
                            tint = if (item.isFavorite) StudioRose else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Prompt description
                Text(
                    text = "Prompt: \"${item.prompt}\"",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    ),
                    maxLines = if (isExpanded) 10 else 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Main Content Preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F1524),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = item.content,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpanded) "Show less" else "Read full creation",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CopilotCyan,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Expand toggle",
                                tint = CopilotCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Bar: Copy, Voiceover Play, Share, Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // TTS Voiceover Play button (for scripts/audio)
                        if (item.category == "SCRIPT" || item.category == "AUDIO" || item.category == "SOCIAL") {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isPlayingTts) StudioAmber.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                modifier = Modifier
                                    .clickable {
                                        if (isPlayingTts) onStopTts() else onPlayTts(item.content)
                                    }
                                    .testTag("tts_button_${item.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingTts) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "Play voiceover",
                                        tint = if (isPlayingTts) StudioAmber else CopilotCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPlayingTts) "Stop" else "Listen",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isPlayingTts) StudioAmber else CopilotCyan,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // Copy Button
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Creator Studio", item.content)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("copy_button_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy text",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Share Button
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TITLE, item.title)
                                    putExtra(Intent.EXTRA_TEXT, "${item.title}\n\n${item.content}\n\nCreated with HP Copilot Local Creator Studio")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share creation")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("share_button_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("delete_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
