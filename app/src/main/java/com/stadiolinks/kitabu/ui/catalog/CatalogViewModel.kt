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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Which books to show. Set by the chips under the search box. */
enum class AvailabilityFilter { ALL, AVAILABLE, BORROWED }

/** What happened when you tried to reserve. The screen turns it into the pop-up. */
sealed interface ReserveResult {
    data class Reserved(val title: String, val days: Int) : ReserveResult
    data object Failed : ReserveResult
}

/** Holds the catalog screen's state and survives screen rotation. */
class CatalogViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    // The search text. Private and changeable in here, read-only to the screen.
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Which chip is selected
    private val _filter = MutableStateFlow(AvailabilityFilter.ALL)
    val filter: StateFlow<AvailabilityFilter> = _filter.asStateFlow()

    /**
     * Books to show. Re-queries whenever the search text or chip changes.
     * No search text: Room filters by availability (getBooksByAvailability).
     * Searching: take the LIKE results and filter them by the chip.
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

    // One-off pop-up messages. Channel so each one only gets delivered once,
    // otherwise it'd show again after a rotation.
    private val _reserveResults = Channel<ReserveResult>(Channel.BUFFERED)
    val reserveResults: Flow<ReserveResult> = _reserveResults.receiveAsFlow()

    fun reserveBook(book: BookEntity, userName: String, days: Int) {
        // launch runs the database write in the background, off the main thread
        viewModelScope.launch {
            val reserved = repository.reserveBook(book.bookId, userName, days)
            // Send the real result, reserveBook returns false if someone else got it first
            _reserveResults.send(
                if (reserved) ReserveResult.Reserved(book.title, days) else ReserveResult.Failed
            )
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
