package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CopilotCyan
import com.example.ui.theme.CreativeViolet
import com.example.ui.theme.StudioGreen
import com.example.ui.theme.StudioMidnight

@Composable
fun NpuStatusBar(
    isLocalNpuOnly: Boolean,
    npuTops: Int,
    latencyMs: Int,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("npu_status_bar"),
        color = Color(0xFF131B2E),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: NPU specs & live status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(CopilotCyan.copy(alpha = 0.3f), CreativeViolet.copy(alpha = 0.3f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "NPU Status",
                        tint = CopilotCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(StudioGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HP NPU $npuTops TOPS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${latencyMs}ms",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CopilotCyan,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Right: Interactive On-Device / Cloud toggle pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isLocalNpuOnly) Color(0xFF003844) else Color(0xFF2E1A47)
                    )
                    .clickable { onToggleMode() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("npu_mode_toggle"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isLocalNpuOnly) Icons.Default.Security else Icons.Default.Bolt,
                    contentDescription = "Mode indicator",
                    tint = if (isLocalNpuOnly) CopilotCyan else CreativeViolet,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isLocalNpuOnly) "Local NPU" else "Hybrid Cloud",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isLocalNpuOnly) CopilotCyan else CreativeViolet,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
