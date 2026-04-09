package com.speechify.composeuichallenge.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.speechify.composeuichallenge.navigation.Details
import com.speechify.composeuichallenge.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    private val bookId: String = savedStateHandle.toRoute<Details>().bookId

    init {
        loadBook()
    }

    private fun loadBook() {
        viewModelScope.launch {
            try {
                val book = repository.getBook(bookId)
                _uiState.value = if (book == null) {
                    DetailsUiState.Error("Book not found")
                } else {
                    DetailsUiState.Success(book)
                }
            } catch (e: IOException) {
                _uiState.value = DetailsUiState.Error(e.message.orEmpty())
            }
        }
    }
}

