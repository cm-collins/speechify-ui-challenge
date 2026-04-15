package com.speechify.composeuichallenge.ui.screens.booklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speechify.composeuichallenge.R
import com.speechify.composeuichallenge.data.Book
import com.speechify.composeuichallenge.repository.BooksRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import android.content.Context
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * `@HiltViewModel` tells Hilt:
 * "You are allowed to create this ViewModel and provide its dependencies."
 *
 * `@Inject constructor(...)` means Hilt should call this constructor
 * and pass in the required objects automatically.
 */
@HiltViewModel
class BookListViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    /**
     * `_uiState` is mutable, but it stays private inside the ViewModel.
     *
     * `MutableStateFlow` is like an observable box that always holds
     * one current value. When we assign a new value, collectors get notified.
     *
     * The underscore naming convention (`_uiState`) is common in Kotlin:
     * private mutable backing property + public read-only version.
     */
    private val _uiState = MutableStateFlow(BookListUiState())

    /**
     * The UI reads this public version.
     *
     * `asStateFlow()` hides the mutable functions so the screen can observe
     * state but cannot accidentally change it.
     */
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    /**
     * This is regular Kotlin mutable state, but it is internal to the ViewModel.
     * The UI does not need to render `allBooks`, so it does not belong in `uiState`.
     */
    private var allBooks: List<Book> = emptyList()

    /**
     * `Job` represents a coroutine we may want to cancel later.
     *
     * We keep track of the current search so that if the user types quickly,
     * an older search does not finish after a newer one and overwrite the UI.
     */
    private var searchJob: Job? = null

    init {
        // `init` runs as soon as the class instance is created.
        // This is a good place to kick off the first load.
        loadBooks()
    }

    /**
     * Called when the user types into the search field.
     */
    fun onQueryChange(query: String) {
        _uiState.update { currentState ->
            currentState.copy(query = query)
        }

        if (_uiState.value.isLoading) {
            // During the first load, we remember the newest query text,
            // but we do not run search yet because the data is not ready.
            return
        }

        runSearch(query)
    }

    /**
     * Exposing `retry()` gives the screen a simple action to call if loading fails.
     */
    fun retry() {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                booksRepository.getBooks()
            }.onSuccess { books ->
                allBooks = books

                val currentQuery = _uiState.value.query
                val visibleBooks = searchBooks(currentQuery)

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        books = visibleBooks,
                        errorMessage = null
                    )
                }
            }.onFailure {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        books = emptyList(),
                        errorMessage = context.getString(R.string.books_load_error)
                    )
                }
            }
        }
    }

    private fun runSearch(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            val visibleBooks = runCatching {
                searchBooks(query)
            }.getOrElse {
                _uiState.update { currentState ->
                    currentState.copy(
                        books = emptyList(),
                        errorMessage = context.getString(R.string.books_search_error)
                    )
                }
                return@launch
            }

            _uiState.update { currentState ->
                currentState.copy(
                    books = visibleBooks,
                    errorMessage = null
                )
            }
        }
    }

    /**
     * `suspend` means this function can pause without blocking the main thread.
     *
     * That is one of the biggest ideas in coroutines:
     * "wait for async work, but do it without freezing the UI".
     */
    private suspend fun searchBooks(query: String): List<Book> {
        return if (query.isBlank()) {
            allBooks
        } else {
            booksRepository.searchBook(query)
        }
    }
}
