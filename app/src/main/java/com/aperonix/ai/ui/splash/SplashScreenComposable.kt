package com.aperonix.ai.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aperonix.ai.R

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale by animateFloatAsState(targetValue = 1f)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.ic_aperonix_logo), contentDescription = "Aperonix", modifier = Modifier.size(220.dp))
    }
    // Caller should handle timing and navigation; this composable displays the logo centered.
}
