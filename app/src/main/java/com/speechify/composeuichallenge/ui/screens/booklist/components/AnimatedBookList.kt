package com.speechify.composeuichallenge.ui.screens.booklist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.speechify.composeuichallenge.data.Book
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A "UI-only" component that renders a list of books with a simple enter/exit animation.
 *
 * Why is this a separate file/component?
 * - BookListScreen should stay focused on: "given uiState, what do we show?"
 * - This component focuses on: "how do we animate list items as they appear/disappear?"
 */
@Composable
fun AnimatedBookList(
    books: List<Book>,
    onDetailsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    itemSpacing: Dp = 12.dp,
) {
    // Keeping scroll state inside this component is fine if the screen doesn't need
    // to control it. If you ever need "scroll-to-top on new search", you can hoist
    // this state to the screen and pass it in as a parameter instead.
    val listState = rememberLazyListState()

    // We keep an "animated list" that can temporarily hold removed items
    // so we can play an exit animation when search filters them out.
    val animatedBooks = rememberAnimatedBooks(books)

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        modifier = modifier
    ) {
        /**
         * Compose tip: use stable keys.
         *
         * When we provide a stable key (`book.id`), Compose can keep per-item state and
         * correctly associate animations with the right row as the list changes.
         */
        items(
            items = animatedBooks,
            key = { it.book.id }
        ) { entry ->
            AnimatedVisibility(
                visibleState = entry.visibility,
                enter = fadeIn(animationSpec = tween(180)) +
                    expandVertically(animationSpec = tween(220)),
                exit = fadeOut(animationSpec = tween(180)) +
                    shrinkVertically(animationSpec = tween(220))
            ) {
                BookListItem(
                    book = entry.book,
                    onDetailsClick = onDetailsClick,
                )
            }
        }
    }
}

/**
 * A tiny wrapper object that holds:
 * - the `Book` data to show
 * - a `MutableTransitionState<Boolean>` that drives AnimatedVisibility
 *
 * `@Stable` is a hint to Compose that:
 * "instances of this type have stable behavior for recomposition".
 */
@Stable
private class AnimatedBookEntry(
    val book: Book,
    val visibility: MutableTransitionState<Boolean>
)

/**
 * Takes the latest "source of truth" list (coming from ViewModel)
 * and produces a list that can animate removals.
 *
 * Why do we need this at all?
 * If we rendered `books` directly, disappearing items would be removed immediately,
 * and Compose would dispose their composables right away (no time for exit animation).
 *
 * Our approach:
 * 1. When an item disappears, set `targetState = false` (plays exit animation).
 * 2. After the animation duration, remove the item from our backing list.
 */
@Composable
private fun rememberAnimatedBooks(books: List<Book>): List<AnimatedBookEntry> {
    // `mutableStateListOf` is a special Compose state container:
    // adding/removing items triggers recomposition of readers.
    val entries = remember { mutableStateListOf<AnimatedBookEntry>() }

    // Tracks "scheduled removals" so if an item is removed and quickly re-added,
    // we cancel the pending removal and just animate it back in.
    val removalJobs = remember { mutableStateMapOf<String, Job>() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(books) {
        val incomingIds = books.map { it.id }.toSet()

        // 1) Animate out items that disappeared from the source list.
        entries
            .filter { it.book.id !in incomingIds }
            .forEach { entry ->
                val id = entry.book.id

                // Trigger exit animation (AnimatedVisibility reads targetState).
                entry.visibility.targetState = false

                // If there was already a pending removal, replace it.
                removalJobs.remove(id)?.cancel()

                removalJobs[id] = scope.launch {
                    // Match the exit animation duration (with a tiny buffer).
                    delay(260)
                    if (entry in entries && entry.visibility.targetState == false) {
                        entries.remove(entry)
                    }
                    removalJobs.remove(id)
                }
            }

        // 2) Insert / move items to match the incoming order.
        // This "move into place" approach is usually enough for search filtering.
        books.forEachIndexed { index, book ->
            val existingIndex = entries.indexOfFirst { it.book.id == book.id }

            if (existingIndex == -1) {
                // New item: start invisible, then target visible (enter animation).
                val state = MutableTransitionState(false).apply { targetState = true }
                val entry = AnimatedBookEntry(book = book, visibility = state)

                // If it was previously scheduled for removal, cancel that.
                removalJobs.remove(book.id)?.cancel()

                entries.add(index.coerceIn(0, entries.size), entry)
            } else {
                // Existing item: ensure it is visible and placed at the right index.
                val entry = entries[existingIndex]
                entry.visibility.targetState = true

                // Cancel a pending removal if the item reappeared.
                removalJobs.remove(book.id)?.cancel()

                if (existingIndex != index) {
                    entries.removeAt(existingIndex)
                    entries.add(index.coerceIn(0, entries.size), entry)
                }
            }
        }
    }

    return entries
}

