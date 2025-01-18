package com.suit.noteice.utils.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule

fun ComposeContentTestRule.setCustomContent(
    uiContent: @Composable () -> Unit,
    testContent: ComposeContentTestRule.() -> Unit
) {
    setContent {
        val context = LocalContext.current
        val snackbarState = remember {
            SnackbarHostState()
        }
        val snackbarController = remember(snackbarState) {
            SnackbarController(
                context = context,
                snackbarHostState = snackbarState
            )
        }
        Scaffold(modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarState) {
                    snackbarState.currentSnackbarData?.visuals?.message?.let { 
                        CustomSnackbar(message = it,
                            onDismiss = { snackbarState.currentSnackbarData?.dismiss() })
                    }
                }
            }) { paddingValues ->
            CompositionLocalProvider(value = LocalSnackbarProvider provides snackbarController) {
                Box(modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()) {
                    uiContent()
                }
            }
        }
    }
    testContent()
}