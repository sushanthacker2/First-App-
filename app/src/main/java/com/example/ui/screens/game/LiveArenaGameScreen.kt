package com.example.ui.screens.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameTournament
import com.example.data.model.GameType
import com.example.ui.theme.ArenaBlack
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaSurface
import com.example.ui.theme.ArenaSurfaceCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.RubyRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LiveArenaGameScreen(
    tournament: GameTournament,
    onGameComplete: (playerScore: Int, opponentScore: Int, isWon: Boolean) -> Unit,
    onClose: () -> Unit
) {
    var gameState by remember { mutableStateOf("MATCHMAKING") } // MATCHMAKING, PLAYING, RESULT
    var timeLeftSeconds by remember { mutableIntStateOf(tournament.durationSeconds) }
    var playerScore by remember { mutableIntStateOf(0) }
    var opponentScore by remember { mutableIntStateOf(0) }

    // Opponent details
    val opponentName = remember {
        val names = listOf("Karan_Pro99", "Rohit_Gamer", "Vikram_Sniper", "Aman_Ninja", "Pooja_Ace", "Deepak_Striker")
        names.random()
    }

    // Matchmaking animation countdown
    LaunchedEffect(Unit) {
        if (gameState == "MATCHMAKING") {
            delay(1500) // 1.5s ultra-fast matchmaking simulation
            gameState = "PLAYING"
        }
    }

    // Main Game Timer
    LaunchedEffect(gameState) {
        if (gameState == "PLAYING") {
            while (timeLeftSeconds > 0) {
                delay(1000)
                timeLeftSeconds--
                val increment = when (tournament.difficulty) {
                    "Beginner" -> Random.nextInt(0, 3)
                    "Normal" -> Random.nextInt(1, 4)
                    "Pro" -> Random.nextInt(2, 5)
                    else -> Random.nextInt(2, 6)
                }
                opponentScore += increment
            }
            gameState = "RESULT"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArenaBlack)
            .testTag("live_arena_game_container")
    ) {
        when (gameState) {
            "MATCHMAKING" -> {
                MatchmakingOverlay(
                    tournament = tournament,
                    opponentName = opponentName,
                    onCancel = onClose
                )
            }
            "PLAYING" -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    GameHeaderBar(
                        tournament = tournament,
                        timeLeft = timeLeftSeconds,
                        playerScore = playerScore,
                        opponentScore = opponentScore,
                        opponentName = opponentName,
                        onQuit = onClose
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (tournament.gameType) {
                            GameType.REFLEX_TAP -> {
                                ReflexTapGamePlay(
                                    onScore = { points -> playerScore += points }
                                )
                            }
                            GameType.MATH_NINJA -> {
                                MathNinjaGamePlay(
                                    onScore = { points -> playerScore += points }
                                )
                            }
                            GameType.COLOR_SWITCH -> {
                                ColorSwitchGamePlay(
                                    onScore = { points -> playerScore += points }
                                )
                            }
                            GameType.TARGET_STRIKE -> {
                                TargetStrikeGamePlay(
                                    onScore = { points -> playerScore += points }
                                )
                            }
                        }
                    }
                }
            }
            "RESULT" -> {
                val isWon = playerScore >= opponentScore
                GameResultOverlay(
                    tournament = tournament,
                    playerScore = playerScore,
                    opponentScore = opponentScore,
                    opponentName = opponentName,
                    isWon = isWon,
                    onClaimAndFinish = {
                        onGameComplete(playerScore, opponentScore, isWon)
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. MATCHMAKING SCREEN
// -------------------------------------------------------------
@Composable
private fun MatchmakingOverlay(
    tournament: GameTournament,
    opponentName: String,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
            border = BorderStroke(1.dp, ArenaBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "ENTRY: ₹${tournament.entryFee.toInt()} | PRIZE POOL: ₹${tournament.prizePool.toInt()}",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "YOU", fontWeight = FontWeight.Black, color = NeonGreen, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "You", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "Ping 12ms", color = NeonGreen, fontSize = 11.sp)
                    }

                    // VS Badge
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "VS", fontWeight = FontWeight.Black, color = ArenaBlack, fontSize = 16.sp)
                    }

                    // Opponent
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "OPP", fontWeight = FontWeight.Black, color = CyberCyan, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = opponentName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "India Server", color = CyberCyan, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonGreen,
                    trackColor = ArenaSurface
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Connecting to India 100% Low-Latency Server...",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "Syncing RNG match state & zero-lag canvas",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Cancel Match", color = TextMuted)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. LIVE GAME HEADER
// -------------------------------------------------------------
@Composable
private fun GameHeaderBar(
    tournament: GameTournament,
    timeLeft: Int,
    playerScore: Int,
    opponentScore: Int,
    opponentName: String,
    onQuit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ArenaSurface,
        border = BorderStroke(0.5.dp, ArenaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player Score
            Column {
                Text(text = "YOU", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "$playerScore",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.testTag("player_score_text")
                )
            }

            // Timer & Prize in Center
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (timeLeft <= 5) RubyRed.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (timeLeft <= 5) RubyRed else GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "${timeLeft}s",
                            fontWeight = FontWeight.Black,
                            color = if (timeLeft <= 5) RubyRed else GoldPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
                Text(
                    text = "Prize: ₹${tournament.prizePool.toInt()}",
                    color = GoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Opponent Score
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = opponentName.take(9),
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$opponentScore",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                IconButton(onClick = onQuit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Quit",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. GAMEPLAY IMPLEMENTATIONS
// -------------------------------------------------------------

// A. Reflex Tap Challenge
@Composable
private fun ReflexTapGamePlay(
    onScore: (Int) -> Unit
) {
    var targetXFraction by remember { mutableFloatStateOf(0.5f) }
    var targetYFraction by remember { mutableFloatStateOf(0.5f) }
    var targetColor by remember { mutableStateOf(NeonGreen) }
    var combo by remember { mutableIntStateOf(1) }

    val colors = listOf(NeonGreen, GoldPrimary, CyberCyan)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val width = maxWidth
        val height = maxHeight
        val targetSize = 90.dp

        Box(
            modifier = Modifier
                .offset(
                    x = ((width - targetSize) * targetXFraction),
                    y = ((height - targetSize) * targetYFraction)
                )
                .size(targetSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(targetColor, targetColor.copy(alpha = 0.4f))
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onScore(combo * 10)
                    combo = (combo % 5) + 1
                    targetXFraction = Random.nextFloat().coerceIn(0.1f, 0.9f)
                    targetYFraction = Random.nextFloat().coerceIn(0.1f, 0.9f)
                    targetColor = colors.random()
                }
                .testTag("reflex_target_orb"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = ArenaBlack,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "TAP!",
                    fontWeight = FontWeight.Black,
                    color = ArenaBlack,
                    fontSize = 14.sp
                )
            }
        }

        Text(
            text = "Tap the glowing orbs as fast as you can!\nCombo Multiplier: x$combo",
            color = TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

// B. Math Ninja Blitz Challenge
@Composable
private fun MathNinjaGamePlay(
    onScore: (Int) -> Unit
) {
    var numA by remember { mutableIntStateOf(Random.nextInt(5, 30)) }
    var numB by remember { mutableIntStateOf(Random.nextInt(2, 20)) }
    var operation by remember { mutableStateOf(if (Random.nextBoolean()) "+" else "x") }
    var correctAnswer by remember {
        mutableIntStateOf(if (operation == "+") numA + numB else numA * (numB % 10 + 1))
    }
    var options by remember { mutableStateOf(generateMathOptions(correctAnswer)) }

    fun nextQuestion() {
        val op = if (Random.nextBoolean()) "+" else "x"
        operation = op
        val a = if (op == "x") Random.nextInt(3, 12) else Random.nextInt(10, 50)
        val b = if (op == "x") Random.nextInt(2, 10) else Random.nextInt(10, 50)
        numA = a
        numB = b
        correctAnswer = if (op == "+") a + b else a * b
        options = generateMathOptions(correctAnswer)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
            border = BorderStroke(1.dp, ArenaBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SOLVE RAPIDLY",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "$numA  $operation  $numB = ?",
                    color = TextPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OptionButton(
                    modifier = Modifier.weight(1f),
                    text = "${options[0]}",
                    onClick = {
                        if (options[0] == correctAnswer) onScore(15)
                        nextQuestion()
                    }
                )
                OptionButton(
                    modifier = Modifier.weight(1f),
                    text = "${options[1]}",
                    onClick = {
                        if (options[1] == correctAnswer) onScore(15)
                        nextQuestion()
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OptionButton(
                    modifier = Modifier.weight(1f),
                    text = "${options[2]}",
                    onClick = {
                        if (options[2] == correctAnswer) onScore(15)
                        nextQuestion()
                    }
                )
                OptionButton(
                    modifier = Modifier.weight(1f),
                    text = "${options[3]}",
                    onClick = {
                        if (options[3] == correctAnswer) onScore(15)
                        nextQuestion()
                    }
                )
            }
        }
    }
}

private fun generateMathOptions(correct: Int): List<Int> {
    val list = mutableListOf(correct)
    while (list.size < 4) {
        val delta = listOf(-10, -2, -1, 1, 2, 5, 10).random()
        val cand = (correct + delta).coerceAtLeast(1)
        if (cand !in list) list.add(cand)
    }
    return list.shuffled()
}

@Composable
private fun OptionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ArenaSurfaceCard),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            color = GoldLight,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// C. Color Switch Challenge
@Composable
private fun ColorSwitchGamePlay(
    onScore: (Int) -> Unit
) {
    val colorNames = listOf("RED", "GREEN", "BLUE", "YELLOW")
    val colorValues = listOf(RubyRed, NeonGreen, CyberCyan, GoldPrimary)

    var displayedWordIndex by remember { mutableIntStateOf(Random.nextInt(colorNames.size)) }
    var fontColorIndex by remember { mutableIntStateOf(Random.nextInt(colorValues.size)) }
    var isMatch by remember { mutableStateOf(displayedWordIndex == fontColorIndex) }

    fun nextTurn() {
        displayedWordIndex = Random.nextInt(colorNames.size)
        fontColorIndex = if (Random.nextBoolean()) {
            displayedWordIndex
        } else {
            (displayedWordIndex + Random.nextInt(1, 3)) % colorNames.size
        }
        isMatch = (displayedWordIndex == fontColorIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Does the WORD match the FONT COLOR?",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.size(240.dp, 160.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
            border = BorderStroke(1.dp, ArenaBorder)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = colorNames[displayedWordIndex],
                    color = colorValues[fontColorIndex],
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (isMatch) onScore(12) else onScore(-5)
                    nextTurn()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Text(text = "YES (MATCH)", color = ArenaBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Button(
                onClick = {
                    if (!isMatch) onScore(12) else onScore(-5)
                    nextTurn()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
            ) {
                Text(text = "NO (DIFFERENT)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// D. Target Strike Challenge
@Composable
private fun TargetStrikeGamePlay(
    onScore: (Int) -> Unit
) {
    val barProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            barProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850)
            )
            barProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 850)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TIMING PRECISION",
            color = GoldPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Hit STRIKE when the cyan indicator is inside the GOLD ZONE!",
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(ArenaSurfaceCard)
                .border(1.dp, ArenaBorder, RoundedCornerShape(24.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f)
                    .align(Alignment.Center)
                    .background(GoldPrimary.copy(alpha = 0.35f))
            )

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .background(GoldPrimary)
            )

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val pointerOffset = maxWidth * barProgress.value
                Box(
                    modifier = Modifier
                        .offset(x = pointerOffset - 8.dp)
                        .size(16.dp, 48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberCyan)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                val current = barProgress.value
                val diff = kotlin.math.abs(current - 0.5f)
                when {
                    diff < 0.06f -> onScore(30)
                    diff < 0.13f -> onScore(15)
                    else -> onScore(2)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .testTag("target_strike_button"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
        ) {
            Text(
                text = "⚡ STRIKE NOW!",
                color = ArenaBlack,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
    }
}

// -------------------------------------------------------------
// 4. GAME OVER & PRIZE CLAIM OVERLAY
// -------------------------------------------------------------
@Composable
private fun GameResultOverlay(
    tournament: GameTournament,
    playerScore: Int,
    opponentScore: Int,
    opponentName: String,
    isWon: Boolean,
    onClaimAndFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ArenaBlack.copy(alpha = 0.95f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
            border = BorderStroke(
                2.dp,
                if (isWon) GoldPrimary else RubyRed.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isWon) "VICTORY! 🏆" else "MATCH OVER",
                    color = if (isWon) GoldPrimary else RubyRed,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isWon)
                        "You won ₹${tournament.prizePool.toInt()}!"
                    else
                        "Hard luck! Better luck next match",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "YOUR SCORE", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$playerScore", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    }

                    Text(text = "VS", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = opponentName.take(9), color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "$opponentScore", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (isWon) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Instant Credit to Winnings",
                                color = GoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "+ ₹${tournament.prizePool.toInt()}",
                                color = GoldPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Eligible for 24x7 UPI / Bank Withdrawal",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onClaimAndFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("claim_and_continue_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWon) GoldPrimary else ArenaSurface
                    )
                ) {
                    Text(
                        text = if (isWon) "Claim Winnings & Back to Lobby" else "Back to Lobby",
                        color = if (isWon) ArenaBlack else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
