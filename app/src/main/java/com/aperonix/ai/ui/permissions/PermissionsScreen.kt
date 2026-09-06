package com.aperonix.ai.ui.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PermissionsScreen(onGranted: () -> Unit, onDenied: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) onGranted() else onDenied()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Aperonix needs microphone access to listen to you.")
            Text(text = "We only use the microphone when you tap the mic button. No continuous recording.", style = MaterialTheme.typography.bodySmall)

            Button(onClick = { launcher.launch(Manifest.permission.RECORD_AUDIO) }, modifier = Modifier.padding(top = 16.dp)) {
                Text(text = "Give Microphone Permission")
            }

            Button(onClick = {
                // Open app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    val uri = Uri.fromParts("package", (context as Activity).packageName, null)
                    data = uri
                }
                context.startActivity(intent)
            }, modifier = Modifier.padding(top = 12.dp)) {
                Text(text = "Open App Settings")
            }
        }
    }
}
