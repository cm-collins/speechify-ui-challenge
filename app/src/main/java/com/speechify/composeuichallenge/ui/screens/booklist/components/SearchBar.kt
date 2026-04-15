package com.speechify.composeuichallenge.ui.screens.booklist.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.speechify.composeuichallenge.R

/**
 * A reusable search field for the books list screen.
 *
 * This composable is intentionally "stateless":
 * it receives the current text (`query`) from its parent
 * and notifies the parent when the text changes.
 *
 * That makes it easier to test and reuse because the screen
 * owns the state, while this composable only draws the UI.
 */
@Composable
fun SearchBar(
    query: String,
    // `(String) -> Unit` means: "a function that receives a String and returns nothing".
    // In Kotlin, passing functions around like values is very common in Compose.
    onQueryChange: (String) -> Unit,
    // `modifier` has a default value, so callers can omit it when they don't need custom layout.
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = {
            Text(
                text = stringResource(R.string.search_placeholder),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}
