package com.speechify.composeuichallenge.ui.screens.bookdetail

import com.speechify.composeuichallenge.data.Book

/**
 * UI state for the Book Details screen.
 *
 * This is a pure "data holder" (no logic, no Android dependencies).
 *
 * Compose works best when the UI is a function of state:
 *   UI = f(state)
 *
 * So we put everything the screen needs to decide what to show here.
 */
data class BookDetailUiState(
    val isLoading: Boolean = true,
    val book: Book? = null,
    val errorMessage: String? = null
)

