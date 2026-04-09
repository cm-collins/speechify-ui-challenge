package com.speechify.composeuichallenge.ui.screens.home

import com.speechify.composeuichallenge.data.Book

sealed interface HomeUiState {

    data object Loading : HomeUiState
    data class Success(
        val books: List<Book>,
        val searchQuery: String = ""
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}