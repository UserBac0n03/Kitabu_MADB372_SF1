package com.stadiolinks.kitabu.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stadiolinks.kitabu.KitabuApplication
import com.stadiolinks.kitabu.data.local.entity.BookEntity
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Holds the catalog screen's state and survives screen rotation. */
class CatalogViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    // The search text. Private and changeable in here, read-only to the screen.
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    /** The books matching the search text. Re-queries the database each time the text changes. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<BookEntity>> = _query
        .flatMapLatest { text -> repository.searchBooks(text.trim()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onQueryChange(text: String) {
        _query.value = text
    }

    fun reserveBook(bookId: Long, userName: String, days: Int) {
        // launch runs the database write in the background, off the main thread
        viewModelScope.launch {
            repository.reserveBook(bookId, userName, days)
        }
    }

    companion object {
        /** Tells Android how to build this ViewModel, handing it the shared repository. */
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as KitabuApplication
                CatalogViewModel(app.container.libraryRepository)
            }
        }
    }
}
