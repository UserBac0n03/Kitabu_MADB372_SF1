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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Which books the catalog shows, picked with the chips under the search box. */
enum class AvailabilityFilter { ALL, AVAILABLE, BORROWED }

/** Holds the catalog screen's state and survives screen rotation. */
class CatalogViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    // The search text. Private and changeable in here, read-only to the screen.
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // The chosen availability chip
    private val _filter = MutableStateFlow(AvailabilityFilter.ALL)
    val filter: StateFlow<AvailabilityFilter> = _filter.asStateFlow()

    /**
     * The books to show. Re-queries the database whenever the search text or the chip changes.
     * With no search text, Room filters by availability directly (getBooksByAvailability);
     * while searching, the LIKE results are narrowed to the chosen availability.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<BookEntity>> = combine(_query, _filter) { text, chip -> text.trim() to chip }
        .flatMapLatest { (text, chip) ->
            if (text.isEmpty() && chip != AvailabilityFilter.ALL) {
                repository.getBooksByAvailability(isAvailable = chip == AvailabilityFilter.AVAILABLE)
            } else {
                repository.searchBooks(text).map { list ->
                    when (chip) {
                        AvailabilityFilter.ALL -> list
                        AvailabilityFilter.AVAILABLE -> list.filter { it.isAvailable }
                        AvailabilityFilter.BORROWED -> list.filterNot { it.isAvailable }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onQueryChange(text: String) {
        _query.value = text
    }

    fun onFilterChange(filter: AvailabilityFilter) {
        _filter.value = filter
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
