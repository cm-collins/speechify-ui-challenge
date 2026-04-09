package com.speechify.composeuichallenge.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speechify.composeuichallenge.data.Book
import com.speechify.composeuichallenge.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allBooks: List<Book> = emptyList()

    init {
        loadBooks()
    }

    fun onSearchQueryChanged(query: String) {
        val filteredBooks = if (query.isBlank()) {
            allBooks
        } else {
            allBooks.filter { it.name.contains(query, ignoreCase = true) }
        }
        _uiState.value = HomeUiState.Success(
            books = filteredBooks,
            searchQuery = query
        )
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                allBooks = repository.getBooks()
                _uiState.value = HomeUiState.Success(books = allBooks)
            } catch (e: IOException) {
                _uiState.value = HomeUiState.Error(e.message.orEmpty())
            }
        }
    }

}