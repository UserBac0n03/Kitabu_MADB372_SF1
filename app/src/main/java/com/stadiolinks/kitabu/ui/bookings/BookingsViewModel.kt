package com.stadiolinks.kitabu.ui.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stadiolinks.kitabu.KitabuApplication
import com.stadiolinks.kitabu.data.local.model.BookingWithBook
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Holds the "My bookings" screen's state and handles its four buttons. */
class BookingsViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    /** Pending and active bookings, updated live from the database. */
    val bookings: StateFlow<List<BookingWithBook>> = repository.getBookings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun collectBooking(item: BookingWithBook) {
        viewModelScope.launch {
            repository.collectBooking(item.booking.bookingId)
        }
    }

    fun renewBooking(item: BookingWithBook) {
        viewModelScope.launch {
            repository.renewBooking(
                bookingId = item.booking.bookingId,
                bookingDate = item.booking.bookingDate,
                currentDeadline = item.booking.returnDeadline
            )
        }
    }

    fun returnBook(item: BookingWithBook) {
        viewModelScope.launch {
            repository.returnBook(item.booking.bookingId, item.booking.bookOwnerId)
        }
    }

    fun cancelBooking(item: BookingWithBook) {
        viewModelScope.launch {
            repository.cancelBooking(item.booking.bookingId, item.booking.bookOwnerId)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as KitabuApplication
                BookingsViewModel(app.container.libraryRepository)
            }
        }
    }
}
