package com.speechify.composeuichallenge.ui.screens.bookdetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speechify.composeuichallenge.R
import com.speechify.composeuichallenge.repository.BooksRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Book Details screen.
 *
 * Responsibilities:
 * - Load a book by id from the repository (suspending work).
 * - Expose a *read-only* StateFlow that the UI collects.
 *
 * Non-responsibilities:
 * - Drawing UI (that belongs in composables).
 * - Navigation (the screen decides when to call onNavigateBack).
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    // We keep track of the current bookId so:
    // - recomposition doesn't re-trigger loads unnecessarily
    // - retry() knows what to reload
    private var currentBookId: String? = null

    fun load(bookId: String) {
        // Avoid re-loading the same id if the UI recomposes.
        if (currentBookId == bookId && _uiState.value.book != null) return

        currentBookId = bookId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching { booksRepository.getBook(bookId) }
                .onSuccess { book ->
                    if (book == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                book = null,
                                errorMessage = context.getString(R.string.book_not_found)
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                book = book,
                                errorMessage = null
                            )
                        }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            book = null,
                            errorMessage = context.getString(R.string.book_detail_load_error)
                        )
                    }
                }
        }
    }

    fun retry() {
        val id = currentBookId ?: return
        load(id)
    }
}
