package com.speechify.composeuichallenge.ui.screens.HomeScreen

import com.speechify.composeuichallenge.data.Book

sealed  interface BookUiState  {
    object Loading : BookUiState
    data class  Succes (val books: List<Book>): BookUiState
    data class  Error (val message: String) : BookUiState
}