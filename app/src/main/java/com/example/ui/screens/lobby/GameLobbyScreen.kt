package com.example.ui.screens.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameTournament
import com.example.data.model.GameType
import com.example.data.model.UserWallet
import com.example.ui.theme.ArenaBlack
import com.example.ui.theme.ArenaBorder
import com.example.ui.theme.ArenaSurface
import com.example.ui.theme.ArenaSurfaceCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.RubyRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GameLobbyScreen(
    wallet: UserWallet?,
    tournaments: List<GameTournament>,
    onSelectTournament: (GameTournament) -> Unit,
    onNavigateWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTierFilter by remember { mutableStateOf("ALL") }

    val feeTiers = listOf("ALL", "₹10", "₹50", "₹100", "₹500", "₹1000", "₹5000", "₹10000")

    val filteredTournaments = remember(tournaments, selectedTierFilter) {
        if (selectedTierFilter == "ALL") tournaments
        else {
            val tierAmt = selectedTierFilter.replace("₹", "").toDoubleOrNull() ?: 0.0
            tournaments.filter { it.entryFee == tierAmt }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaBlack)
            .padding(horizontal = 16.dp)
            .testTag("game_lobby_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Hero Tournament Banner
        item {
            LobbyHeroBanner(
                wallet = wallet,
                onAddCash = onNavigateWallet
            )
        }

        // Live Ticker & Server Connectivity Status
        item {
            ConnectivityStatusBar()
        }

        // Filter by Entry Fee Tiers (₹10 min to ₹10,000 max)
        item {
            Column {
                Text(
                    text = "SELECT TOURNAMENT ENTRY TIER",
                    color = GoldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(feeTiers) { tier ->
                        val isSelected = selectedTierFilter == tier
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldPrimary else ArenaSurfaceCard,
                            border = BorderStroke(1.dp, if (isSelected) GoldPrimary else ArenaBorder),
                            modifier = Modifier
                                .clickable { selectedTierFilter = tier }
                                .testTag("filter_tier_${tier.replace("₹", "")}")
                        ) {
                            Text(
                                text = tier,
                                color = if (isSelected) ArenaBlack else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // List of Active Real Money Tournaments
        item {
            Text(
                text = "LIVE BATTLES & POOLS (${filteredTournaments.size})",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(filteredTournaments, key = { it.id }) { tournament ->
            TournamentCard(
                tournament = tournament,
                walletBalance = wallet?.totalBalance ?: 0.0,
                onPlayNow = { onSelectTournament(tournament) }
            )
        }

        item {
            FairPlayFooter()
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun LobbyHeroBanner(
    wallet: UserWallet?,
    onAddCash: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GoldContainer.copy(alpha = 0.4f),
                            ArenaSurfaceCard
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldPrimary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "LIVE INDIA LEAGUE", color = GoldLight, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clickable { onAddCash() }
                            .clip(RoundedCornerShape(12.dp))
                            .background(ArenaSurface)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${"%.2f".format(wallet?.totalBalance ?: 0.0)}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Play 1v1 Skill Battles.\nWin Real Cash via UPI.",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Compete starting from ₹10 up to ₹10,000 mega pots with instant 24x7 payouts.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureBadge("₹10 Min Entry", Icons.Default.TrendingUp, NeonGreen)
                    FeatureBadge("100% Zero-Lag", Icons.Default.Bolt, CyberCyan)
                    FeatureBadge("Instant UPI", Icons.Default.Security, GoldPrimary)
                }
            }
        }
    }
}

@Composable
private fun FeatureBadge(text: String, icon: ImageVector, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ConnectivityStatusBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ArenaSurfaceCard,
        border = BorderStroke(0.5.dp, ArenaBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FiberManualRecord,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "India Server 100% Connected (Mumbai Rail)",
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            Text(
                text = "12ms Ping",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TournamentCard(
    tournament: GameTournament,
    walletBalance: Double,
    onPlayNow: () -> Unit
) {
    val gameIcon = when (tournament.gameType) {
        GameType.REFLEX_TAP -> Icons.Default.Bolt
        GameType.MATH_NINJA -> Icons.Default.Calculate
        GameType.COLOR_SWITCH -> Icons.Default.ColorLens
        GameType.TARGET_STRIKE -> Icons.Default.TrackChanges
    }

    val themeColor = when (tournament.gameType) {
        GameType.REFLEX_TAP -> NeonGreen
        GameType.MATH_NINJA -> CyberCyan
        GameType.COLOR_SWITCH -> GoldPrimary
        GameType.TARGET_STRIKE -> RubyRed
    }

    val canAfford = walletBalance >= tournament.entryFee

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tournament_card_${tournament.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
        border = BorderStroke(1.dp, ArenaBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(gameIcon, contentDescription = null, tint = themeColor, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = tournament.gameTitle,
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "1v1 Battle • ${tournament.durationSeconds}s match",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = themeColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = tournament.difficulty,
                                    color = themeColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "PRIZE POOL", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "₹${tournament.prizePool.toInt()}",
                        color = GoldPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaSurface)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Entry Fee: ", color = TextMuted, fontSize = 12.sp)
                    Text(
                        text = "₹${tournament.entryFee.toInt()}",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = onPlayNow,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) themeColor else ArenaBorder
                    ),
                    modifier = Modifier.testTag("play_button_${tournament.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (canAfford) ArenaBlack else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (canAfford) "PLAY NOW" else "ADD CASH",
                        color = if (canAfford) ArenaBlack else TextMuted,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FairPlayFooter() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Safe & Legally Compliant in India", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "WinArena offers 100% skill-based games with anti-fraud RNG match pairing. Instant UPI & IMPS payouts supported for SBI, HDFC, ICICI, Paytm, PhonePe, GPay.",
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
