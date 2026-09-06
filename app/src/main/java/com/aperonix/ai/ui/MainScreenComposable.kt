package com.aperonix.ai.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aperonix.ai.R
import com.aperonix.ai.ui.main.MainViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun MainScreenComposable(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0B)),
        contentAlignment = Alignment.Center
    ) {
        // Breathing pulse animation
        val infinite = rememberInfiniteTransition()
        val scale by infinite.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier
                    .size(260.dp)
                    .shadow(10.dp, CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF5B2EE0).copy(alpha = 0.12f), Color.Transparent))))

                Icon(
                    painter = painterResource(id = R.drawable.ic_aperonix_logo),
                    contentDescription = "Aperonix Logo",
                    modifier = Modifier
                        .size(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = when (uiState) {
                is com.aperonix.ai.ui.main.UiState.Idle -> "Aperonix is ready"
                is com.aperonix.ai.ui.main.UiState.Listening -> "Listening..."
                is com.aperonix.ai.ui.main.UiState.Thinking -> "Thinking..."
                is com.aperonix.ai.ui.main.UiState.Speaking -> "Speaking..."
                is com.aperonix.ai.ui.main.UiState.Error -> "Error"
            }, color = Color.White, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(20.dp))

            // Simple waveform when speaking
            if (uiState is com.aperonix.ai.ui.main.UiState.Speaking) {
                Waveform()
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Memory screen */ }) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_menu_save), contentDescription = "Memory", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Microphone button
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(84.dp)
                        .background(Color(0xFF6C4EEB), shape = CircleShape)
                        .clickable {
                            // Toggle listening
                            if (uiState is com.aperonix.ai.ui.main.UiState.Listening) {
                                viewModel.stopListening()
                            } else {
                                viewModel.startListening()
                            }
                        }) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_btn_speak_now), contentDescription = "Speak", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(24.dp))

                IconButton(onClick = { /* Settings */ }) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_menu_manage), contentDescription = "Settings", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun Waveform() {
    val infinite = rememberInfiniteTransition()
    val phases = List(5) { index -> infinite.animateFloat(initialValue = 0.2f, targetValue = 1.0f, animationSpec = infiniteRepeatable(animation = tween(800 + index * 200), repeatMode = RepeatMode.Reverse)) }
    Canvas(modifier = Modifier.size(160.dp)) {
        val center = size.width / 2
        val spacing = 12f
        val barWidth = 8f
        val startX = center - (2 * spacing + 2 * barWidth)
        for (i in 0 until 5) {
            val heightFactor = phases[i].value
            val barHeight = size.height * 0.5f * heightFactor
            val x = startX + i * (barWidth + spacing)
            drawLine(color = Color(0xFF6C4EEB), start = Offset(x, size.height / 2 - barHeight / 2), end = Offset(x, size.height / 2 + barHeight / 2), strokeWidth = barWidth, cap = StrokeCap.Round)
        }
    }
}
