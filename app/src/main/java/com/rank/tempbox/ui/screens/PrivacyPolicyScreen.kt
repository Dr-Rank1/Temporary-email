package com.rank.tempbox.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rank.tempbox.R
import com.rank.tempbox.ui.theme.TempBoxTheme

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val colors = TempBoxTheme.colors
    val context = LocalContext.current
    val publicUrl = stringResource(R.string.privacy_policy_url)

    BackHandler(onBack = onBack)

    Column(
        Modifier
            .fillMaxSize()
            .background(colors.phoneBg),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(colors.card)
                .border(width = 0.5.dp, color = colors.cardBorder)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    )
                    .padding(8.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.sub,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Text("Privacy Policy", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.text)
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text("Last updated: Just now", fontSize = 12.sp, color = colors.sub)
            Spacer(Modifier.height(16.dp))
            PolicySection("No Personal Data Collection", "TempBox does not require your name, real email address, phone number, or any other personally identifying information. The app is designed entirely for anonymity.")
            PolicySection("Local Device Storage", "Your generated temporary mailbox address, password, and active session tokens are stored exclusively on your device within encrypted local preferences. We do not maintain any central database of our users.")
            PolicySection("Mail Hosting (mail.tm)", "Your temporary emails are securely hosted by our partner API, mail.tm, and are automatically purged from their servers after a short period (typically up to 7 days). Generating a new address in the app will prompt mail.tm to instantly delete your previous account.")
            PolicySection("Advertising Partners", "TempBox is supported by Unity Ads. The Unity Ads SDK may collect non-identifying diagnostic and device data to serve relevant advertisements, in accordance with Unity's privacy guidelines.")
            PolicySection("Contact", "For questions or account deletion requests, please open an issue at: github.com/Dr-Rank1/Temporary-email")
            Spacer(Modifier.height(8.dp))
            Text(
                "Open in browser",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.accent,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(publicUrl)))
                    }
                    .padding(vertical = 8.dp),
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PolicySection(title: String, body: String) {
    val colors = TempBoxTheme.colors
    Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.text)
    Spacer(Modifier.height(6.dp))
    Text(body, fontSize = 13.sp, color = colors.sub, lineHeight = 20.sp)
    Spacer(Modifier.height(16.dp))
}
