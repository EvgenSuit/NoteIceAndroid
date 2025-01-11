package com.suit.noteice.features.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suit.noteice.features.auth.presentation.ui.AuthScreen
import com.suit.noteice.features.notes.presentation.ui.NotesScreen

sealed class Route(val name: String) {
    data object Notes: Route("Notes")
    data object Auth: Route("Auth")
}

@Composable
fun NavManager(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier
) {
    NavHost(
        startDestination = Route.Notes.name,
        navController = navController,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {
        composable(Route.Notes.name) {
            NotesScreen(
                onNavigateToAuth = {
                    navController.navigate(Route.Auth.name) {
                        popUpTo(navController.graph.id)
                    }
                }
            )
        }
        composable(Route.Auth.name) {
            AuthScreen()
        }
    }
}