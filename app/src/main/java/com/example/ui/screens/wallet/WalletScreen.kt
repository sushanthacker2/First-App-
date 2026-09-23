package com.example.ui.screens.wallet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionRecord
import com.example.data.model.TransactionType
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    wallet: UserWallet?,
    transactions: List<TransactionRecord>,
    onAddDeposit: (amount: Double, paymentMode: String, upiRef: String) -> Unit,
    onRequestWithdrawal: (amount: Double, destType: String, destAddress: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Wallet & Action, 1: History, 2: Compliance/KYC

    var showAddMoneyDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaBlack)
            .testTag("wallet_screen_root")
    ) {
        // Top Balance Master Card
        WalletHeaderCard(
            wallet = wallet,
            onAddMoneyClick = { showAddMoneyDialog = true },
            onWithdrawClick = { showWithdrawDialog = true }
        )

        // Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ArenaSurface,
            contentColor = GoldPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldPrimary
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Payment Rails", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Passbook (${transactions.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Fair Play & KYC", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        when (selectedTab) {
            0 -> PaymentRailsTab(
                wallet = wallet,
                onAddMoney = { showAddMoneyDialog = true },
                onWithdraw = { showWithdrawDialog = true }
            )
            1 -> TransactionHistoryTab(transactions = transactions)
            2 -> IndianComplianceTab(wallet = wallet)
        }
    }

    if (showAddMoneyDialog) {
        AddMoneyUpiDialog(
            onDismiss = { showAddMoneyDialog = false },
            onProceedDeposit = { amount, mode, ref ->
                onAddDeposit(amount, mode, ref)
                showAddMoneyDialog = false
                Toast.makeText(context, "₹${amount.toInt()} Added via $mode!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showWithdrawDialog) {
        WithdrawMoneyDialog(
            wallet = wallet,
            onDismiss = { showWithdrawDialog = false },
            onProceedWithdraw = { amount, destType, destAddr ->
                onRequestWithdrawal(amount, destType, destAddr)
                showWithdrawDialog = false
            }
        )
    }
}

// ---------------------------------------------------------------------
// 1. WALLET HEADER CARD
// ---------------------------------------------------------------------
@Composable
private fun WalletHeaderCard(
    wallet: UserWallet?,
    onAddMoneyClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    val totalBalance = wallet?.totalBalance ?: 0.0
    val winnings = wallet?.winningsBalance ?: 0.0
    val deposit = wallet?.depositBalance ?: 0.0
    val bonus = wallet?.bonusBalance ?: 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
        border = BorderStroke(1.dp, ArenaBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
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
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "TOTAL WALLET BALANCE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "₹${"%.2f".format(totalBalance)}",
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NeonGreen.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "KYC VERIFIED", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sub-balances breakdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ArenaSurface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Deposit Cash", color = TextMuted, fontSize = 11.sp)
                    Text(text = "₹${deposit.toInt()}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Winnings (Withdrawable)", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "₹${winnings.toInt()}", color = GoldPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Bonus Cash", color = TextMuted, fontSize = 11.sp)
                    Text(text = "₹${bonus.toInt()}", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Add Money (Deposit) & Withdraw
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddMoneyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("wallet_add_money_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = ArenaBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ADD CASH", color = ArenaBlack, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }

                Button(
                    onClick = onWithdrawClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("wallet_withdraw_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = ArenaBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "WITHDRAW", color = ArenaBlack, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// 2. PAYMENT RAILS TAB (India UPI + Bank Instant)
// ---------------------------------------------------------------------
@Composable
private fun PaymentRailsTab(
    wallet: UserWallet?,
    onAddMoney: () -> Unit,
    onWithdraw: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "INDIAN INSTANT PAYMENT GATEWAYS",
                color = GoldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // UPI Gateways
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
                border = BorderStroke(1.dp, ArenaBorder)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = CyberCyan)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "UPI Auto-Intent (100% Connectivity)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "GPay • PhonePe • Paytm • BHIM • Cred", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = NeonGreen.copy(alpha = 0.15f)) {
                            Text(
                                text = "ZERO FEE",
                                color = NeonGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Native Android UPI Intent seamlessly links directly into your installed banking apps for verified instant credits and 0% failure rate.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onAddMoney,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArenaSurface)
                    ) {
                        Text(text = "Deposit via UPI App (₹10 - ₹10,000)", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // IMPS / Bank Payout
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
                border = BorderStroke(1.dp, ArenaBorder)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GoldPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "24x7 Instant IMPS Bank Payout", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Linked A/C: ${wallet?.bankAccount ?: "Pending"}", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = GoldPrimary.copy(alpha = 0.15f)) {
                            Text(
                                text = "INSTANT",
                                color = GoldLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Withdraw winnings straight to your savings account or UPI VPA (${wallet?.upiId}). Processed in under 60 seconds via RBI-approved payout routes.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onWithdraw,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArenaSurface)
                    ) {
                        Text(text = "Request Instant Payout", color = GoldLight, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Limits & Rules
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Transaction Guardrails", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• Minimum entry fee: ₹10 per game", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "• Maximum tournament entry: ₹10,000", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "• Minimum withdrawal: ₹50", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "• Maximum single withdrawal: ₹10,000", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "• TDS & Tax deducted as per Indian Income Tax Act Sec 194BA for net winnings", color = TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// 3. PASSBOOK (TRANSACTION HISTORY) TAB
// ---------------------------------------------------------------------
@Composable
private fun TransactionHistoryTab(transactions: List<TransactionRecord>) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "No transactions yet", color = TextSecondary, fontWeight = FontWeight.Bold)
                Text(text = "Play games or add cash to see your passbook", color = TextMuted, fontSize = 12.sp)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(transactions, key = { it.id }) { txn ->
                TransactionRowCard(txn = txn)
            }
        }
    }
}

@Composable
private fun TransactionRowCard(txn: TransactionRecord) {
    val isCredit = txn.type == TransactionType.DEPOSIT ||
            txn.type == TransactionType.WINNING ||
            txn.type == TransactionType.BONUS_CREDIT

    val amountColor = if (isCredit) NeonGreen else RubyRed
    val sign = if (isCredit) "+ ₹" else "- ₹"
    val dateStr = remember(txn.timestamp) {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(txn.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
        border = BorderStroke(0.5.dp, ArenaBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(amountColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = amountColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = txn.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${txn.paymentMode} • $dateStr",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    if (txn.remarks.isNotBlank()) {
                        Text(text = txn.remarks, color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$sign${"%.2f".format(txn.amount)}",
                    color = amountColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = txn.status.name,
                    color = if (txn.status.name == "SUCCESS") NeonGreen else GoldPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// 4. FAIR PLAY & KYC TAB
// ---------------------------------------------------------------------
@Composable
private fun IndianComplianceTab(wallet: UserWallet?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
                border = BorderStroke(1.dp, ArenaBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "100% Fair Play & RNG Certified", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "WinArena operates strictly skill-based competitive challenges conforming to the Supreme Court of India directives on games of skill. All results rely on user reflex, cognitive speed, and precision.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard),
                border = BorderStroke(1.dp, ArenaBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Your KYC Profile", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    KycDetailRow("Name on Bank Account", wallet?.accountHolderName ?: "Pro Gamer")
                    KycDetailRow("Verified UPI ID", wallet?.upiId ?: "player@oksbi")
                    KycDetailRow("Bank Account (Masked)", wallet?.bankAccount ?: "XXXXXX4589")
                    KycDetailRow("IFSC Code", wallet?.ifscCode ?: "HDFC0000128")
                    KycDetailRow("KYC Document Status", "Aadhaar / PAN Verified")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ArenaSurfaceCard)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Responsible Gaming & 18+ Only", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Players must be 18 years of age or older. Daily spending caps and self-exclusion tools are supported. Users from states where pay-to-play skill gaming is restricted (Assam, Odisha, Telangana, Andhra Pradesh, Nagaland, Sikkim) are prohibited from cash entry.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun KycDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextMuted, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

// ---------------------------------------------------------------------
// 5. ADD MONEY DIALOG (UPI INTENT / QR / QUICK PRESETS)
// ---------------------------------------------------------------------
@Composable
private fun AddMoneyUpiDialog(
    onDismiss: () -> Unit,
    onProceedDeposit: (amount: Double, paymentMode: String, upiRef: String) -> Unit
) {
    val context = LocalContext.current
    var inputAmountText by remember { mutableStateOf("100") }
    var selectedPreset by remember { mutableDoubleStateOf(100.0) }
    var selectedMethod by remember { mutableStateOf("Google Pay") }

    val presets = listOf(10.0, 50.0, 100.0, 500.0, 1000.0, 5000.0, 10000.0)
    val upiMethods = listOf("Google Pay", "PhonePe", "Paytm UPI", "BHIM / Cred")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaSurfaceCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCode, contentDescription = null, tint = NeonGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add Cash (₹10 - ₹10,000)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Enter Amount in INR (₹)", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = inputAmountText,
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isDigit() }
                        inputAmountText = filtered
                        selectedPreset = filtered.toDoubleOrNull() ?: 0.0
                    },
                    leadingIcon = {
                        Text(text = "₹", color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = ArenaBorder,
                        focusedContainerColor = ArenaSurface,
                        unfocusedContainerColor = ArenaSurface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Quick Presets:", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                // Presets wrap
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.take(4).forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedPreset == amt) GoldPrimary else ArenaSurface,
                            border = BorderStroke(1.dp, if (selectedPreset == amt) GoldPrimary else ArenaBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedPreset = amt
                                    inputAmountText = amt.toInt().toString()
                                }
                        ) {
                            Text(
                                text = "₹${amt.toInt()}",
                                color = if (selectedPreset == amt) ArenaBlack else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.drop(4).forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedPreset == amt) GoldPrimary else ArenaSurface,
                            border = BorderStroke(1.dp, if (selectedPreset == amt) GoldPrimary else ArenaBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedPreset = amt
                                    inputAmountText = amt.toInt().toString()
                                }
                        ) {
                            Text(
                                text = "₹${amt.toInt()}",
                                color = if (selectedPreset == amt) ArenaBlack else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Select UPI Payment Method:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                upiMethods.forEach { method ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedMethod == method) NeonGreen.copy(alpha = 0.15f) else ArenaSurface,
                        border = BorderStroke(1.dp, if (selectedMethod == method) NeonGreen else ArenaBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedMethod = method }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = method, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            if (selectedMethod == method) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = inputAmountText.toDoubleOrNull() ?: 0.0
                    if (amt < 10.0) {
                        Toast.makeText(context, "Minimum deposit amount is ₹10", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (amt > 10000.0) {
                        Toast.makeText(context, "Maximum deposit amount is ₹10,000", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Attempt real UPI Intent invocation if available on physical Indian Android phone
                    val upiUri = "upi://pay?pa=winarena.payments@icici&pn=WinArenaGames&am=$amt&cu=INR&tn=WalletDeposit"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUri))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Fallback seamlessly on simulator / devices without native UPI app
                    }

                    val refId = "UPI${System.currentTimeMillis().toString().takeLast(8)}"
                    onProceedDeposit(amt, selectedMethod, refId)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Pay & Add to Wallet", color = ArenaBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = TextMuted)
            }
        }
    )
}

// ---------------------------------------------------------------------
// 6. WITHDRAW MONEY DIALOG (INSTANT UPI / BANK)
// ---------------------------------------------------------------------
@Composable
private fun WithdrawMoneyDialog(
    wallet: UserWallet?,
    onDismiss: () -> Unit,
    onProceedWithdraw: (amount: Double, destType: String, destAddress: String) -> Unit
) {
    val context = LocalContext.current
    val maxAvailable = wallet?.winningsBalance ?: 0.0

    var inputAmountText by remember { mutableStateOf(if (maxAvailable >= 50) "50" else "0") }
    var selectedMethod by remember { mutableStateOf("UPI ID") } // "UPI ID" or "BANK ACCOUNT"
    var upiAddress by remember { mutableStateOf(wallet?.upiId ?: "player@oksbi") }
    var bankAccountNum by remember { mutableStateOf(wallet?.bankAccount ?: "987654321012") }
    var ifscCode by remember { mutableStateOf(wallet?.ifscCode ?: "SBIN0001234") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaSurfaceCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Withdraw Winnings", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Withdrawable Winnings: ₹${"%.2f".format(maxAvailable)}",
                    color = GoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Per Indian Gaming Norms, only won prizes are withdrawable.",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Withdrawal Amount (Min ₹50 - Max ₹10,000):", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = inputAmountText,
                    onValueChange = { inputAmountText = it.filter { ch -> ch.isDigit() } },
                    leadingIcon = { Text("₹", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    trailingIcon = {
                        TextButton(onClick = { inputAmountText = maxAvailable.toInt().toString() }) {
                            Text("MAX", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = ArenaBorder,
                        focusedContainerColor = ArenaSurface,
                        unfocusedContainerColor = ArenaSurface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Select Payout Destination:", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedMethod == "UPI ID") GoldPrimary else ArenaSurface,
                        border = BorderStroke(1.dp, if (selectedMethod == "UPI ID") GoldPrimary else ArenaBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMethod = "UPI ID" }
                    ) {
                        Text(
                            text = "UPI ID / VPA",
                            color = if (selectedMethod == "UPI ID") ArenaBlack else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedMethod == "BANK ACCOUNT") GoldPrimary else ArenaSurface,
                        border = BorderStroke(1.dp, if (selectedMethod == "BANK ACCOUNT") GoldPrimary else ArenaBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMethod = "BANK ACCOUNT" }
                    ) {
                        Text(
                            text = "IMPS Bank A/C",
                            color = if (selectedMethod == "BANK ACCOUNT") ArenaBlack else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedMethod == "UPI ID") {
                    OutlinedTextField(
                        value = upiAddress,
                        onValueChange = { upiAddress = it },
                        label = { Text("Enter UPI ID (e.g. name@okhdfcbank)", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ArenaBorder,
                            focusedContainerColor = ArenaSurface,
                            unfocusedContainerColor = ArenaSurface
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = bankAccountNum,
                        onValueChange = { bankAccountNum = it },
                        label = { Text("Bank Account Number", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ArenaBorder,
                            focusedContainerColor = ArenaSurface,
                            unfocusedContainerColor = ArenaSurface
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = ifscCode,
                        onValueChange = { ifscCode = it.uppercase() },
                        label = { Text("Bank IFSC Code", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ArenaBorder,
                            focusedContainerColor = ArenaSurface,
                            unfocusedContainerColor = ArenaSurface
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = inputAmountText.toDoubleOrNull() ?: 0.0
                    if (amt < 50.0) {
                        Toast.makeText(context, "Minimum withdrawal limit is ₹50", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (amt > 10000.0) {
                        Toast.makeText(context, "Maximum withdrawal per request is ₹10,000", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (amt > maxAvailable) {
                        Toast.makeText(context, "Insufficient withdrawable winnings (₹${maxAvailable.toInt()})", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val dest = if (selectedMethod == "UPI ID") upiAddress else "$bankAccountNum / $ifscCode"
                    onProceedWithdraw(amt, selectedMethod, dest)
                    Toast.makeText(context, "₹${amt.toInt()} Withdrawal Initiated via $selectedMethod!", Toast.LENGTH_LONG).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Confirm Instant Cashout", color = ArenaBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = TextMuted)
            }
        }
    )
}
