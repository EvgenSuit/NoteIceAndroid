package com.suit.noteice.utils.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.foundation.layout.tappableElementIgnoringVisibility
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun CustomSnackbar(
    message: String?,
    onDismiss: () -> Unit
) {
    if (message != null) {
        Snackbar(
            action = {
                IconButton(onClick = onDismiss) {
                    val clearIcon = Icons.Filled.Clear
                    Icon(imageVector = clearIcon, contentDescription = clearIcon.name)
                }
            },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .consumeWindowInsets(WindowInsets.navigationBars)
                .imePadding()
        ) {
            Text(text = message,
                style = MaterialTheme.typography.labelSmall)
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