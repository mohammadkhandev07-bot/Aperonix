package com.aperonix.ai.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.aperonix.ai.ui.theme.AperonixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[com.aperonix.ai.ui.main.MainViewModel::class.java]
        setContent {
            AperonixTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val hasRecordPermission = remember { mutableStateOf(
                        ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    ) }

                    if (!hasRecordPermission.value) {
                        PermissionsScreen(onGranted = { hasRecordPermission.value = true }, onDenied = { /* show explanation in UI if needed */ })
                    } else {
                        MainScreenComposable(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
