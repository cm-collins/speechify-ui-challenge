package com.speechify.composeuichallenge.ui.screens.HomeScreen

import androidx.lifecycle.ViewModel
import com.speechify.composeuichallenge.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BooksViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {
}