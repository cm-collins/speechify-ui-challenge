package com.speechify.composeuichallenge.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.speechify.composeuichallenge.ui.screens.details.DetailsScreen
import com.speechify.composeuichallenge.ui.screens.home.HomeScreen


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(navController, startDestination = Home) {

            composable<Home> {
                HomeScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onNavigate = { bookId ->
                        navController.navigate(Details(bookId))
                    }
                )
            }

            composable<Details> {
                DetailsScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}