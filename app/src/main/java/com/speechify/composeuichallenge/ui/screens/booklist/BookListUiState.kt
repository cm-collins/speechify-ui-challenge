package com.speechify.composeuichallenge.ui.screens.booklist

import com.speechify.composeuichallenge.data.Book

/**
 * `data class` is one of Kotlin's most useful features.
 *
 * When you mark a class as `data`, Kotlin automatically gives you:
 * - `copy(...)` so you can create a new version with only a few changed fields
 * - `equals()` / `hashCode()` for value-based comparison
 * - `toString()` for easier debugging
 *
 * That is perfect for UI state because the screen usually wants
 * "the current snapshot of everything it should render".
 */
data class BookListUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val books: List<Book> = emptyList(),
    val errorMessage: String? = null
) {
    /**
     * This is a computed property.
     *
     * It is not stored as a separate field in memory.
     * Kotlin calculates it every time someone reads it.
     *
     * We use this when a value can be derived from existing state.
     */
    val showEmptyState: Boolean
        get() = !isLoading && errorMessage == null && query.isNotBlank() && books.isEmpty()
}
