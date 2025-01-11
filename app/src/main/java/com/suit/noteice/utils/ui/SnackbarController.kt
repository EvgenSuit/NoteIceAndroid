package com.suit.noteice.utils.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.suit.noteice.ui.theme.NoteIceTheme

val LocalSnackbarProvider = compositionLocalOf<SnackbarController> {
    error("No SnackbarController provided.")
}

class SnackbarController(
    private val context: Context,
    private val snackbarHostState: SnackbarHostState
) {
    suspend fun showSnackbar(uiText: UIText) {
        val message = uiText.toString(context)
        if (message.isNotBlank()) {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun CustomSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    Snackbar {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = message)
            IconButton(onClick = onDismiss) {
                val clearIcon = Icons.Filled.Clear
                Icon(imageVector = clearIcon, contentDescription = clearIcon.name)
            }
        }
    }
}

@Preview
@Composable
fun CustomSnackbarPreview() {
    NoteIceTheme {
        Surface {
            CustomSnackbar(
                message = "Some message",
                onDismiss = {})
        }
    }
}