package com.rank.tempbox.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rank.tempbox.ui.theme.TempBoxColors
import com.rank.tempbox.ui.theme.TempBoxTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    emailAddress: String,
    isDark: Boolean,
    isLoading: Boolean = false,
    refreshCountdown: Int = 0,
    refreshProgress: Float = 0f,
    unreadCount: Int = 0,
    onRefreshEmail: () -> Unit,
    onCopyEmail: (String) -> Unit,
    onNavigateToInbox: () -> Unit,
    onRefreshInbox: () -> Unit,
) {
    val colors = TempBoxTheme.colors
    val scope = rememberCoroutineScope()
    var copied by remember { mutableStateOf(false) }
    var masked by remember { mutableStateOf(false) }
    var confirmNewAddress by remember { mutableStateOf(false) }
    val hasEmail = emailAddress.isNotBlank()

    val displayEmail = when {
        isLoading && !hasEmail -> "Generating..."
        !hasEmail -> "Waiting for connection..."
        masked && emailAddress.contains("@") -> {
            val parts = emailAddress.split("@")
            val local = parts[0]
            val maskedLocal = if (local.length > 3) local.take(3) + "•".repeat(local.length - 3) else local
            "$maskedLocal@${parts[1]}"
        }
        else -> emailAddress
    }

    fun copyAddress() {
        if (!hasEmail) {
            onCopyEmail("")
            return
        }
        onCopyEmail(emailAddress)
        copied = true
        scope.launch { delay(2000); copied = false }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.phoneBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Main Glass Card
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(colors.card.copy(alpha = 0.8f))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(32.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "YOUR TEMPORARY EMAIL",
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = colors.sub,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(Modifier.height(24.dp))
                
                // Address Display
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = hasEmail,
                            onClick = ::copyAddress,
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.outerBg.copy(alpha = 0.4f))
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            displayEmail,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasEmail) colors.text else colors.sub,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        
                        if (copied) {
                            Spacer(Modifier.height(12.dp))
                            Text("COPIED TO CLIPBOARD", fontSize = 10.sp, letterSpacing = 1.sp, color = colors.accent, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Modern Pill Actions
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    GlassPillAction(
                        icon = if (copied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
                        label = if (copied) "Copied" else "Copy",
                        enabled = hasEmail,
                        colors = colors,
                        onClick = ::copyAddress,
                    )
                    GlassPillAction(
                        icon = if (masked) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        label = if (masked) "Show" else "Hide",
                        enabled = hasEmail,
                        colors = colors,
                        onClick = { if (hasEmail) masked = !masked },
                    )
                    GlassPillAction(
                        icon = Icons.Rounded.Refresh,
                        label = "Change",
                        enabled = !isLoading,
                        colors = colors,
                        onClick = { if (hasEmail) confirmNewAddress = true },
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            
            // Countdown Bar
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("AUTO-REFRESH IN", fontSize = 10.sp, letterSpacing = 1.sp, color = colors.sub)
                Text(
                    if (isLoading) "SYNCING" else "${refreshCountdown.coerceAtLeast(0)}s",
                    fontSize = 12.sp,
                    color = colors.accent,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.card)
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(refreshProgress.coerceIn(0f, 1f))
                        .background(
                            Brush.horizontalGradient(
                                listOf(colors.accent.copy(alpha = 0.5f), colors.accent)
                            )
                        )
                )
            }

            Spacer(Modifier.height(32.dp))

            // High-Tech Inbox Button
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(colors.card, colors.card.copy(alpha = 0.5f))
                        )
                    )
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNavigateToInbox,
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (unreadCount > 0) colors.accent 
                                else colors.cardBorder
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.AllInbox, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("OPEN INBOX", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.text, letterSpacing = 1.sp)
                        Text(
                            if (unreadCount > 0) "$unreadCount New Messages" else "No messages yet",
                            fontSize = 13.sp,
                            color = if (unreadCount > 0) colors.accent else colors.sub,
                        )
                    }
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = colors.sub)
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                "Addresses expire after 7 days on mail.tm.",
                fontSize = 11.sp,
                color = colors.sub.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center,
            )
        }

        if (confirmNewAddress) {
            AlertDialog(
                onDismissRequest = { confirmNewAddress = false },
                title = { Text("Change Address?") },
                text = {
                    Text("The current email will be discarded. Messages on this address will no longer be available.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        confirmNewAddress = false
                        onRefreshEmail()
                    }) { Text("PROCEED", color = colors.accent) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmNewAddress = false }) { Text("CANCEL", color = colors.sub) }
                },
                containerColor = colors.card,
                titleContentColor = colors.text,
                textContentColor = colors.sub
            )
        }

        if (isLoading && !hasEmail) {
            Box(Modifier.fillMaxSize().background(colors.outerBg.copy(alpha = 0.8f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.accent, strokeWidth = 3.dp, modifier = Modifier.size(48.dp))
            }
        }
    }
}

@Composable
private fun LiveDot(accent: Color) {
    val alpha by rememberInfiniteTransition(label = "live").animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOut), RepeatMode.Reverse),
        label = "liveAlpha",
    )
    Box(
        Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = alpha)),
    )
}

@Composable
private fun GlassPillAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    colors: TempBoxColors,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .background(colors.cardBorder.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            icon,
            null,
            tint = if (enabled) colors.text else colors.sub.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp),
        )
        Text(
            label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = if (enabled) colors.text else colors.sub.copy(alpha = 0.4f),
        )
    }
}
