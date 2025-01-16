package com.suit.noteice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.suit.noteice.ui.theme.NoteIceTheme
import com.suit.noteice.features.navigation.NavManager
import com.suit.noteice.utils.ui.CustomSnackbar
import com.suit.noteice.utils.ui.LocalSnackbarProvider
import com.suit.noteice.utils.ui.SnackbarController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarState = remember {
                SnackbarHostState()
            }
            val snackbarController = remember(snackbarState) {
                SnackbarController(
                    context = applicationContext,
                    snackbarHostState = snackbarState
                )
            }
            NoteIceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarState) {
                                snackbarState.currentSnackbarData?.visuals?.message?.let {
                                    CustomSnackbar(message = it,
                                        onDismiss = { snackbarState.currentSnackbarData?.dismiss() })
                                }
                            }
                        }) { innerPadding ->
                        CompositionLocalProvider(LocalSnackbarProvider provides snackbarController) {
                            NavManager(modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                                .imePadding()
                            )
                        }
                    }
                }
            }
        }
    }
}
