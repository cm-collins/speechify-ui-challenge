package com.speechify.composeuichallenge.ui.screens.details

import com.speechify.composeuichallenge.data.Book

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(val book: Book) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

