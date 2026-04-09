package com.speechify.composeuichallenge.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Details(
    val bookId: String
)