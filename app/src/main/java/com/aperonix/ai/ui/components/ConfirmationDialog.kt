package com.aperonix.ai.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationDialog(title: String, text: String, confirmLabel: String = "Yes", cancelLabel: String = "Cancel", onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = { Text(text) }, confirmButton = {
        Button(onClick = { onConfirm() }) { Text(confirmLabel) }
    }, dismissButton = {
        Button(onClick = { onDismiss() }) { Text(cancelLabel) }
    })
}
