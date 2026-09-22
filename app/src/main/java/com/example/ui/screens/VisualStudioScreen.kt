package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CreationItem
import com.example.ui.theme.CopilotCyan
import com.example.ui.theme.CreativeViolet
import com.example.ui.theme.StudioAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualStudioScreen(
    isGenerating: Boolean,
    statusMessage: String,
    onGenerate: (title: String, category: String, prompt: String, style: String, aspectRatio: String, drawableName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var promptText by remember { mutableStateOf("Futuristic holographic video editing console in a dark cyberpunk studio") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk") }
    var selectedRatio by remember { mutableStateOf("16:9") }
    var selectedLighting by remember { mutableStateOf("Volumetric Neon") }
    var generatedPreviewResult by remember { mutableStateOf<CreationItem?>(null) }

    val styles = listOf("Cyberpunk", "Cinematic 3D", "Fantasy / Anime", "Photoreal 8K", "Concept Sketch", "Vector Art")
    val aspectRatios = listOf("16:9", "9:16", "1:1", "4:3")
    val lightingPresets = listOf("Volumetric Neon", "Golden Hour Glow", "Studio Rim Light", "Film Noir")

    val promptIdeas = listOf(
        "Neon rain reflections on cyber alley",
        "Floating celestial island with crystal cascades",
        "3D stylized robot sculptor with digital stylus",
        "Minimalist architectural glass pavilion at sunset"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("visual_studio_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Title Card
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
                                .background(CopilotCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = CopilotCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Visual Concept Studio",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "Prompt to concept art, storyboards & visual assets",
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

        // Prompt Input Field
        item {
            Text(
                text = "Creative Prompt",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("visual_prompt_input"),
                placeholder = { Text("Describe the visual concept, scene, lighting, or storyboard shot...") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CopilotCyan,
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

            // Quick Prompt Ideas
            Text(
                text = "Inspiration Chords:",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
            )
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                promptIdeas.forEach { idea ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { promptText = idea }
                    ) {
                        Text(
                            text = "+ $idea",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CopilotCyan,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Style Selector
        item {
            Text(
                text = "Artistic Style",
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
                styles.forEach { style ->
                    val isSelected = selectedStyle == style
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) CopilotCyan else Color(0xFF1A2338),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedStyle = style }
                            .testTag("style_chip_$style")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF003844),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = style,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Color(0xFF003844) else Color(0xFFE2E8F0),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // Aspect Ratio & Lighting Controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Aspect Ratio Selector
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Aspect Ratio",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        aspectRatios.forEach { ratio ->
                            val isSelected = selectedRatio == ratio
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) CreativeViolet else Color(0xFF1E293B),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedRatio = ratio }
                            ) {
                                Text(
                                    text = ratio,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Lighting presets
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Atmosphere",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LightMode,
                                contentDescription = null,
                                tint = StudioAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedLighting,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Generate Button
        item {
            Button(
                onClick = {
                    if (promptText.isNotBlank()) {
                        val chosenDrawable = if (selectedStyle == "Cyberpunk") {
                            "sample_cyberpunk"
                        } else if (selectedStyle.contains("Fantasy") || selectedStyle.contains("Anime")) {
                            "sample_landscape"
                        } else {
                            "banner_creator_studio"
                        }

                        onGenerate(
                            "$selectedStyle: ${promptText.take(25)}...",
                            "VISUAL",
                            promptText,
                            selectedStyle,
                            selectedRatio,
                            chosenDrawable
                        )
                        Toast.makeText(context, "Generating visual concept with NPU...", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isGenerating && promptText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_visual_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CopilotCyan,
                    contentColor = Color(0xFF003844)
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF003844),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusMessage.ifBlank { "Rendering concept..." },
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
                        text = "Generate Visual Concept",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Concept Art Showcase
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Showcase Visual Concepts",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sample 1: Cyberpunk
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            promptText = "Futuristic neon cyberpunk city street at rainy dusk with glowing holographic billboards, reflections"
                            selectedStyle = "Cyberpunk"
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.sample_cyberpunk),
                            contentDescription = "Cyberpunk concept",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Cyberpunk Dusk",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Neon reflections & rain",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Sample 2: Floating Islands
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            promptText = "Breathtaking fantasy floating island with glowing crystalline waterfalls and warm sunset clouds"
                            selectedStyle = "Fantasy / Anime"
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.sample_landscape),
                            contentDescription = "Fantasy landscape concept",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Crystal Islands",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Golden hour clouds",
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

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
