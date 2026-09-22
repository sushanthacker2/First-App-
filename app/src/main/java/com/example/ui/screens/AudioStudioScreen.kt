package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun AudioStudioScreen(
    isGenerating: Boolean,
    statusMessage: String,
    isPlayingTts: Boolean,
    speed: Float,
    pitch: Float,
    onSetSpeed: (Float) -> Unit,
    onSetPitch: (Float) -> Unit,
    onPlayTts: (String) -> Unit,
    onStopTts: () -> Unit,
    onGenerate: (title: String, category: String, prompt: String, style: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var voiceScriptText by remember {
        mutableStateOf("Welcome to the future of creation. With on-device neural processing, your audio, scripts, and visuals align with zero latency. Let's build something extraordinary.")
    }
    var selectedSoundscape by remember { mutableStateOf("Cyberpunk Synthwave (118 BPM)") }

    val soundscapes = listOf(
        "Cyberpunk Synthwave (118 BPM)",
        "Lo-Fi Study Beats (85 BPM)",
        "Cinematic Epic Orchestral (92 BPM)",
        "Minimal Tech Ambient (110 BPM)",
        "Corporate Uplifting (124 BPM)"
    )

    // Animated wave visualizer
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 28f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 48f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w3"
    )
    val wave4 by infiniteTransition.animateFloat(
        initialValue = 36f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w4"
    )
    val wave5 by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w5"
    )

    // Word count & duration calculation
    val wordCount = voiceScriptText.split(Regex("\\s+")).count { it.isNotBlank() }
    val estimatedSeconds = ((wordCount / (145f * speed)) * 60).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("audio_studio_screen"),
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
                                .background(StudioAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = StudioAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Voiceover & Audio Studio",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "Live speech synthesizer, pacing meter & soundscape specs",
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

        // Live Voiceover Player & Waveform Visualizer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151D2E))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = StudioAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Neural Voice Engine",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        // Duration Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Text(
                                text = "$wordCount words • ~$estimatedSeconds sec",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CopilotCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simulated Audio Frequency Waveform
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0A0E17))
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val heights = if (isPlayingTts) {
                            listOf(wave1, wave2, wave3, wave4, wave5, wave2, wave1, wave4, wave3, wave5, wave1, wave3)
                        } else {
                            listOf(8f, 12f, 18f, 14f, 10f, 16f, 12f, 8f, 14f, 10f, 8f, 6f)
                        }

                        heights.forEachIndexed { index, h ->
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(h.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isPlayingTts) {
                                            Brush.verticalGradient(listOf(StudioAmber, CopilotCyan))
                                        } else {
                                            Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
                                        }
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Play / Stop Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isPlayingTts) {
                                    onStopTts()
                                } else {
                                    onPlayTts(voiceScriptText)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("tts_play_toggle_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingTts) StudioAmber else CopilotCyan,
                                contentColor = Color(0xFF003844)
                            )
                        ) {
                            Icon(
                                imageVector = if (isPlayingTts) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPlayingTts) "Stop Voiceover" else "Play Voiceover",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Voice Rate Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Speech Rate (${String.format("%.2f", speed)}x)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                        )
                    }
                    Slider(
                        value = speed,
                        onValueChange = { onSetSpeed(it) },
                        valueRange = 0.75f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = CopilotCyan,
                            activeTrackColor = CopilotCyan,
                            inactiveTrackColor = Color(0xFF2E3B52)
                        ),
                        modifier = Modifier.testTag("speech_rate_slider")
                    )

                    // Voice Pitch Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vocal Pitch (${String.format("%.2f", pitch)}x)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                        )
                    }
                    Slider(
                        value = pitch,
                        onValueChange = { onSetPitch(it) },
                        valueRange = 0.8f..1.3f,
                        colors = SliderDefaults.colors(
                            thumbColor = StudioAmber,
                            activeTrackColor = StudioAmber,
                            inactiveTrackColor = Color(0xFF2E3B52)
                        ),
                        modifier = Modifier.testTag("speech_pitch_slider")
                    )
                }
            }
        }

        // Voice Script Input
        item {
            Text(
                text = "Voiceover Script",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = voiceScriptText,
                onValueChange = { voiceScriptText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_script_input"),
                placeholder = { Text("Enter script for voiceover synthesis and pacing check...") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StudioAmber,
                    unfocusedBorderColor = Color(0xFF2E3B52),
                    focusedContainerColor = Color(0xFF0F1524),
                    unfocusedContainerColor = Color(0xFF0F1524),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFFE2E8F0)
                ),
                minLines = 3,
                maxLines = 6
            )
        }

        // Soundscape / BGM Director
        item {
            Text(
                text = "Background Soundscape & BPM",
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
                soundscapes.forEach { sscape ->
                    val isSelected = selectedSoundscape == sscape
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) StudioAmber else Color(0xFF1A2338),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedSoundscape = sscape }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF422006) else Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sscape,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color(0xFF422006) else Color(0xFFE2E8F0),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // Save & Generate Audio Prompt
        item {
            Button(
                onClick = {
                    if (voiceScriptText.isNotBlank()) {
                        onGenerate(
                            "Voiceover: ${voiceScriptText.take(25)}...",
                            "AUDIO",
                            voiceScriptText,
                            selectedSoundscape
                        )
                        Toast.makeText(context, "Saved audio voiceover & soundtrack spec", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isGenerating && voiceScriptText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_audio_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioAmber,
                    contentColor = Color(0xFF422006)
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF422006),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusMessage.ifBlank { "Compiling audio track..." },
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
                        text = "Save Voiceover & Audio Spec",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
