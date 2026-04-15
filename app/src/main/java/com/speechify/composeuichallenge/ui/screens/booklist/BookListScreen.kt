package com.speechify.composeuichallenge.ui.screens.booklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.speechify.composeuichallenge.R
import com.speechify.composeuichallenge.ui.screens.booklist.components.AnimatedBookList
import com.speechify.composeuichallenge.ui.screens.booklist.components.LoadingIndicator
import com.speechify.composeuichallenge.ui.screens.booklist.components.SearchBar

/**
 * This file is the "screen-level" composable for the Books list.
 *
 * In a typical Compose architecture:
 * - ViewModel owns state + business logic (loading, searching, errors).
 * - Screen composable collects state and decides what to draw.
 * - Small "components" draw reusable pieces (SearchBar, ListItem, etc).
 */
@Composable
fun BookListScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    // `hiltViewModel()` asks Hilt to provide an instance of BookListViewModel.
    // This works because BookListViewModel is annotated with `@HiltViewModel`
    // and its dependency (BooksRepository) is bound in BookModule.
    viewModel: BookListViewModel = hiltViewModel()
) {
    /**
     * `collectAsState()` bridges a Kotlin `StateFlow<T>` into Compose `State<T>`.
     *
     * Key idea:
     * - Flow is "push-based": it emits values over time.
     * - Compose UI is "state-driven": it redraws (recomposes) when state changes.
     *
     * Whenever `uiState` emits a new BookListUiState, Compose schedules a recomposition
     * of the parts of the UI that read `uiState`.
     */
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        when {
            /**
             * Loading state:
             * The ViewModel sets `isLoading = true` only during the initial fetch.
             * During search, it updates `books` without toggling `isLoading`,
             * so the loading indicator won't show while typing (requirement #6).
             */
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }

            /**
             * Error state:
             * A simple message + retry button.
             *
             * Notice that the ViewModel exposes `retry()`.
             * That keeps the "what to do" logic in the ViewModel, while the UI just
             * triggers it when the user taps.
             */
            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = viewModel::retry) {
                    Text(
                        text = stringResource(R.string.retry),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Content state: search field + list / empty state.
            else -> {
                SearchBar(
                    query = uiState.query,
                    onQueryChange = viewModel::onQueryChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.showEmptyState) {
                    Text(
                        text = stringResource(R.string.empty_books_for_query, uiState.query),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    AnimatedBookList(
                        books = uiState.books,
                        onDetailsClick = onNavigateToDetail,
                        contentPadding = PaddingValues(vertical = 8.dp),
                        itemSpacing = 12.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
