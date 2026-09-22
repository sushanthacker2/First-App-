package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CreatorStudioViewModel
import com.example.ui.StudioScreen
import com.example.ui.screens.AudioStudioScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ScriptCopilotScreen
import com.example.ui.screens.StudioDashboardScreen
import com.example.ui.screens.VisualStudioScreen
import com.example.ui.theme.CopilotCyan
import com.example.ui.theme.CreativeViolet
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioMidnight
import com.example.ui.theme.StudioSurface

class MainActivity : ComponentActivity() {

    private val viewModel: CreatorStudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CreatorStudioApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorStudioApp(
    viewModel: CreatorStudioViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val creations by viewModel.creations.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val statusMessage by viewModel.generationStatusMessage.collectAsStateWithLifecycle()
    val isLocalNpuOnly by viewModel.isLocalNpuOnly.collectAsStateWithLifecycle()
    val npuTops by viewModel.npuTops.collectAsStateWithLifecycle()
    val latencyMs by viewModel.npuLatencyMs.collectAsStateWithLifecycle()

    val isPlayingTts by viewModel.ttsManager.isPlaying.collectAsStateWithLifecycle()
    val ttsSpeed by viewModel.ttsManager.speed.collectAsStateWithLifecycle()
    val ttsPitch by viewModel.ttsManager.pitch.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("creator_studio_app_root"),
        containerColor = StudioMidnight,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(CopilotCyan, CreativeViolet)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF0B0F19),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "HP Copilot",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CopilotCyan.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "STUDIO",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CopilotCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Local Creator Engine",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { viewModel.toggleNpuMode() }
                            .testTag("top_npu_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isLocalNpuOnly) Color(0xFF10B981) else Color(0xFFA855F7))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isLocalNpuOnly) "NPU" else "Hybrid",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioMidnight,
                    scrolledContainerColor = StudioMidnight
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF101626),
                contentColor = Color.White,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationTabItem(
                    selected = currentScreen == StudioScreen.DASHBOARD,
                    onClick = { viewModel.setScreen(StudioScreen.DASHBOARD) },
                    icon = Icons.Default.AutoAwesome,
                    label = "Studio"
                )
                NavigationTabItem(
                    selected = currentScreen == StudioScreen.VISUAL,
                    onClick = { viewModel.setScreen(StudioScreen.VISUAL) },
                    icon = Icons.Default.Palette,
                    label = "Visual"
                )
                NavigationTabItem(
                    selected = currentScreen == StudioScreen.SCRIPTS,
                    onClick = { viewModel.setScreen(StudioScreen.SCRIPTS) },
                    icon = Icons.Default.Videocam,
                    label = "Scripts"
                )
                NavigationTabItem(
                    selected = currentScreen == StudioScreen.AUDIO,
                    onClick = { viewModel.setScreen(StudioScreen.AUDIO) },
                    icon = Icons.Default.GraphicEq,
                    label = "Audio"
                )
                NavigationTabItem(
                    selected = currentScreen == StudioScreen.LIBRARY,
                    onClick = { viewModel.setScreen(StudioScreen.LIBRARY) },
                    icon = Icons.Default.Folder,
                    label = "Library"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    StudioScreen.DASHBOARD -> StudioDashboardScreen(
                        creations = creations,
                        isLocalNpuOnly = isLocalNpuOnly,
                        npuTops = npuTops,
                        latencyMs = latencyMs,
                        onToggleNpuMode = { viewModel.toggleNpuMode() },
                        onNavigateToScreen = { viewModel.setScreen(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onDeleteCreation = { viewModel.deleteCreation(it) },
                        onPlayTts = { viewModel.ttsManager.speak(it) },
                        isPlayingTts = isPlayingTts,
                        onStopTts = { viewModel.ttsManager.stop() }
                    )

                    StudioScreen.VISUAL -> VisualStudioScreen(
                        isGenerating = isGenerating,
                        statusMessage = statusMessage,
                        onGenerate = { title, category, prompt, style, aspectRatio, drawableName ->
                            viewModel.generateAndSave(
                                title = title,
                                category = category,
                                prompt = prompt,
                                style = style,
                                aspectRatio = aspectRatio,
                                drawableName = drawableName,
                                onComplete = { viewModel.setScreen(StudioScreen.LIBRARY) }
                            )
                        }
                    )

                    StudioScreen.SCRIPTS -> ScriptCopilotScreen(
                        isGenerating = isGenerating,
                        statusMessage = statusMessage,
                        onGenerate = { title, category, prompt, style ->
                            viewModel.generateAndSave(
                                title = title,
                                category = category,
                                prompt = prompt,
                                style = style,
                                onComplete = { viewModel.setScreen(StudioScreen.LIBRARY) }
                            )
                        },
                        onPlayTts = { viewModel.ttsManager.speak(it) },
                        isPlayingTts = isPlayingTts,
                        onStopTts = { viewModel.ttsManager.stop() }
                    )

                    StudioScreen.AUDIO -> AudioStudioScreen(
                        isGenerating = isGenerating,
                        statusMessage = statusMessage,
                        isPlayingTts = isPlayingTts,
                        speed = ttsSpeed,
                        pitch = ttsPitch,
                        onSetSpeed = { viewModel.ttsManager.setSpeed(it) },
                        onSetPitch = { viewModel.ttsManager.setPitch(it) },
                        onPlayTts = { viewModel.ttsManager.speak(it) },
                        onStopTts = { viewModel.ttsManager.stop() },
                        onGenerate = { title, category, prompt, style ->
                            viewModel.generateAndSave(
                                title = title,
                                category = category,
                                prompt = prompt,
                                style = style,
                                onComplete = { viewModel.setScreen(StudioScreen.LIBRARY) }
                            )
                        }
                    )

                    StudioScreen.LIBRARY -> LibraryScreen(
                        creations = creations,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        onSelectCategory = { viewModel.setCategory(it) },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onDeleteCreation = { viewModel.deleteCreation(it) },
                        onPlayTts = { viewModel.ttsManager.speak(it) },
                        isPlayingTts = isPlayingTts,
                        onStopTts = { viewModel.ttsManager.stop() }
                    )
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NavigationTabItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF003844),
            selectedTextColor = CopilotCyan,
            indicatorColor = CopilotCyan,
            unselectedIconColor = Color(0xFF94A3B8),
            unselectedTextColor = Color(0xFF94A3B8)
        ),
        modifier = Modifier.testTag("nav_item_${label.lowercase()}")
    )
}

// Backwards-compatible Greeting function for Robolectric / screenshot tests
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
