package com.speechify.composeuichallenge.ui.navigation

/**
 * Defines all navigation destinations in the app as a sealed class.
 *
 * WHY A SEALED CLASS?
 * A sealed class restricts which classes can extend it — only the ones
 * declared inside this file. This means the compiler knows every possible
 * screen at compile time, so if you ever add a screen and forget to handle
 * it in the NavGraph, you get a compile error instead of a runtime crash.
 *
 * WHY NOT JUST USE STRINGS DIRECTLY?
 * Raw strings like "book_detail/123" scattered across composables are fragile.
 * If you rename a route, you'd have to find and update every reference manually.
 * This sealed class gives you one place to change things.
 */
sealed class Screen(val route: String) {

    /**
     * The books list screen — the app's start destination.
     *
     * This is an `object` (singleton) because it has no arguments.
     * There is only ever one "book list" route: "book_list".
     */
    data object BookList : Screen(route = "book_list")

    /**
     * The book detail screen — requires a bookId to know which book to load.
     *
     * HOW ARGUMENTS WORK IN COMPOSE NAVIGATION:
     * Arguments are embedded directly in the route string using curly braces:
     *   "book_detail/{bookId}"
     *
     * When you navigate, you replace the placeholder with the actual value:
     *   "book_detail/abc-123"
     *
     * The NavGraph then extracts "abc-123" back out and passes it
     * to the composable via the NavBackStackEntry's arguments bundle.
     *
     * ARGUMENT_KEY is a constant so you never mistype the key string
     * when extracting the argument later.
     */
    data object BookDetail : Screen(route = "book_detail/{bookId}") {

        const val ARGUMENT_KEY = "bookId"

        /**
         * Builds the actual navigation route string with the real bookId filled in.
         *
         * Usage:
         *   navController.navigate(Screen.BookDetail.createRoute(book.id))
         *
         * This produces:  "book_detail/0197cfb0-da30-70ed-a6f5-21331ea1b2cc"
         */
        fun createRoute(bookId: String): String = "book_detail/$bookId"
    }
}