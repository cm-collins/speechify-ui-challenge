package com.speechify.composeuichallenge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.speechify.composeuichallenge.ui.screens.bookdetail.BookDetailScreen
import com.speechify.composeuichallenge.ui.screens.booklist.BookListScreen

/**
 * The app's navigation graph.
 *
 * @param modifier     Applied to the NavHost container itself.
 * @param navController The navigation controller driving this graph.
 */
@Composable
fun NAVGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.BookList.route,
        modifier = modifier
    ) {
        composable(route = Screen.BookList.route) {
            BookListScreen(
                onNavigateToDetail = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                }
            )
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(
                navArgument(Screen.BookDetail.ARGUMENT_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            BookDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()                }
            )
        }
    }
}