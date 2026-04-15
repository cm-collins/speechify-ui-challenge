package com.speechify.composeuichallenge.ui.screens.bookdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.speechify.composeuichallenge.ui.screens.booklist.components.LoadingIndicator
import com.speechify.composeuichallenge.ui.screens.bookdetail.components.BookDetailContent
import com.speechify.composeuichallenge.ui.screens.bookdetail.components.BookDetailErrorState

/**
 * Screen-level composable for Book Details.
 *
 * The pattern matches BookListScreen:
 * - collect state from the ViewModel
 * - translate that state into loading / error / content UI
 * - keep business logic out of the composable
 */
@Composable
fun BookDetailScreen(
    bookId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * `LaunchedEffect(bookId)` runs its block the first time this composable enters
     * the composition, and again only if `bookId` changes.
     *
     * That makes it a good place to kick off one-off work that depends on a parameter.
     */
    LaunchedEffect(bookId) {
        viewModel.load(bookId)
    }

    when {
        uiState.isLoading -> {
            LoadingIndicator(modifier = modifier.fillMaxSize())
        }

        uiState.errorMessage != null -> {
            BookDetailErrorState(
                message = uiState.errorMessage.orEmpty(),
                onNavigateBack = onNavigateBack,
                onRetry = viewModel::retry,
                modifier = modifier
            )
        }

        uiState.book != null -> {
            val book = uiState.book ?: return
            BookDetailContent(
                title = book.name,
                author = book.author,
                description = book.description,
                imageUrl = book.imageUrl,
                modifier = modifier
            )
        }
    }
}
