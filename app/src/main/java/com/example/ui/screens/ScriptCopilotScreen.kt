package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CopilotCyan
import com.example.ui.theme.CreativeViolet
import com.example.ui.theme.StudioAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScriptCopilotScreen(
    isGenerating: Boolean,
    statusMessage: String,
    onGenerate: (title: String, category: String, prompt: String, style: String) -> Unit,
    onPlayTts: (String) -> Unit,
    isPlayingTts: Boolean,
    onStopTts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var topicText by remember { mutableStateOf("5 game-changing productivity habits for digital content creators") }
    var selectedFormat by remember { mutableStateOf("YouTube Longform") }
    var selectedTone by remember { mutableStateOf("Energetic & Punchy") }
    var isTeleprompterMode by remember { mutableStateOf(false) }
    var teleprompterFontSize by remember { mutableStateOf(16) }

    val formats = listOf("YouTube Longform", "Viral Reel / Short", "Podcast Talking Points", "Storytelling Arc", "Newsletter Draft")
    val tones = listOf("Energetic & Punchy", "Educational / Tech", "Storyteller Narrative", "Humorous & Fast", "Documentary")

    val topicInspirations = listOf(
        "Why 90% of video creators burn out (and the fix)",
        "How to film cinematic video with a budget smartphone",
        "The 3-second hook formula that doubled my retention",
        "Day in the life of an AI-powered solo studio"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("script_copilot_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CreativeViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = CreativeViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Script & Content Copilot",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "Video scripts, hooks, pacing specs & teleprompter mode",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Script Format Selection
        item {
            Text(
                text = "Format & Architecture",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formats.forEach { fmt ->
                    val isSelected = selectedFormat == fmt
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) CreativeViolet else Color(0xFF1A2338),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedFormat = fmt }
                            .testTag("format_chip_$fmt")
                    ) {
                        Text(
                            text = fmt,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Script Topic Input
        item {
            Text(
                text = "Video / Content Topic",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = topicText,
                onValueChange = { topicText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("script_topic_input"),
                placeholder = { Text("What is your video about? Core premise, audience, takeaways...") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CreativeViolet,
                    unfocusedBorderColor = Color(0xFF2E3B52),
                    focusedContainerColor = Color(0xFF0F1524),
                    unfocusedContainerColor = Color(0xFF0F1524),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFFE2E8F0)
                ),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Topic inspirations
            Text(
                text = "Quick Topic Templates:",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
            )
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                topicInspirations.forEach { insp ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { topicText = insp }
                    ) {
                        Text(
                            text = "+ $insp",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CreativeViolet,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Tone Selector & Pacing specs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Creator Tone",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = selectedTone,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "High retention vocal cadence",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Estimated Speaking Pacing
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Target Pacing",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = StudioAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "145 WPM",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "Optimal YouTube rate",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Generate Script Button
        item {
            Button(
                onClick = {
                    if (topicText.isNotBlank()) {
                        val category = if (selectedFormat.contains("Reel") || selectedFormat.contains("Short")) "SOCIAL" else "SCRIPT"
                        onGenerate(
                            "$selectedFormat: ${topicText.take(25)}...",
                            category,
                            topicText,
                            selectedFormat
                        )
                        Toast.makeText(context, "Writing script with Copilot...", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isGenerating && topicText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_script_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CreativeViolet,
                    contentColor = Color.White
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusMessage.ifBlank { "Crafting script structure..." },
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate Script with Copilot",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Teleprompter / Reader Tool Preview
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
                            text = "Live Teleprompter Studio",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (teleprompterFontSize < 24) teleprompterFontSize += 2
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text(
                                    text = "A+",
                                    fontWeight = FontWeight.Bold,
                                    color = CopilotCyan,
                                    fontSize = 13.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (teleprompterFontSize > 12) teleprompterFontSize -= 2
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text(
                                    text = "A-",
                                    fontWeight = FontWeight.Bold,
                                    color = CopilotCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0A0E17),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = """[HOOK - 0:00-0:15]
"What if you could turn a raw idea into a full storyboard, voiceover, and video draft in under 60 seconds?"

[INTRO - 0:15-0:35]
Welcome back! Today we are breaking down the top local AI workflow secrets for modern creators.

[POINT 1: LOCAL PROMPT CHORDS - 0:35-1:15]
Feed your thumbnail concept and script synopsis simultaneously into your HP NPU studio for instant multi-modal alignment.""",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFF1F5F9),
                                    fontSize = teleprompterFontSize.sp,
                                    lineHeight = (teleprompterFontSize * 1.4).sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Voice preview button
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isPlayingTts) StudioAmber.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                    modifier = Modifier
                                        .clickable {
                                            if (isPlayingTts) {
                                                onStopTts()
                                            } else {
                                                onPlayTts("What if you could turn a raw idea into a full storyboard, voiceover, and video draft in under 60 seconds? Welcome back! Today we are breaking down the top local AI workflow secrets for modern creators.")
                                            }
                                        }
                                        .testTag("teleprompter_tts_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingTts) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = if (isPlayingTts) StudioAmber else CopilotCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isPlayingTts) "Stop Voice" else "Listen to Prompter",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isPlayingTts) StudioAmber else CopilotCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Teleprompter Script", "What if you could turn a raw idea into a full storyboard, voiceover, and video draft in under 60 seconds?")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied teleprompter text", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy script",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
