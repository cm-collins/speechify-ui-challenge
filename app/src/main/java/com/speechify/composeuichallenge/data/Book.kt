package com.speechify.composeuichallenge.data

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val name: String,
    val author: String,
    val imageUrl: String,
    val description: String,
    val rating: Float,
    val reviewCount: Int
)
