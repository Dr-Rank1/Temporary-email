package com.rank.tempbox

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.rank.tempbox.ui.components.AppLogo
import com.rank.tempbox.ui.components.AppLogoSize
import com.rank.tempbox.ui.theme.TempBoxTheme
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val prefs = remember { getSharedPreferences(PrefKeys.PREFS_NAME, MODE_PRIVATE) }
            val themeMode = remember { themeModeFrom(prefs) }
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }

            TempBoxTheme(isDark = isDark) {
                val colors = TempBoxTheme.colors

                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.95f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "logo_scale"
                )

                LaunchedEffect(Unit) {
                    delay(400)
                    val completed = prefs.getBoolean("onboarding_complete", false)
                    val intent = if (completed) {
                        Intent(this@SplashActivity, MainActivity::class.java)
                    } else {
                        Intent(this@SplashActivity, OnboardingActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.phoneBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .scale(scale)
                                .padding(24.dp)
                        ) {
                            AppLogo(size = AppLogoSize.Medium, showWordmark = true)
                        }
                    }
                }
            }
        }
    }
}
