package com.stadiolinks.kitabu.data.repository

import com.stadiolinks.kitabu.data.local.dao.BookDao
import com.stadiolinks.kitabu.data.local.dao.BookingDao
import com.stadiolinks.kitabu.data.local.entity.BookEntity
import com.stadiolinks.kitabu.data.local.entity.BookingEntity
import com.stadiolinks.kitabu.data.local.entity.BookingStatus
import com.stadiolinks.kitabu.data.local.model.BookingWithBook
import com.stadiolinks.kitabu.domain.BookingRules
import kotlinx.coroutines.flow.Flow

/**
 * The single place the ViewModels get data from.
 * It hides the DAOs and applies the booking rules before saving.
 */
class LibraryRepository(
    private val bookDao: BookDao,
    private val bookingDao: BookingDao
) {

    // Reads: live lists that update whenever the database changes

    /** Books whose title or author contains [query]. An empty query returns every book. */
    fun searchBooks(query: String): Flow<List<BookEntity>> = bookDao.searchBooks(query)

    /** Only available books (true) or only borrowed books (false). */
    fun getBooksByAvailability(isAvailable: Boolean): Flow<List<BookEntity>> =
        bookDao.getBooksByAvailability(isAvailable)

    /** Bookings that are not yet returned, each with its book's details. */
    fun getBookings(): Flow<List<BookingWithBook>> = bookingDao.getBookingsWithBook()

    // Writes: suspend functions, so they must be called from a coroutine

    /** Returns false if the input is invalid or the book was already taken. */
    suspend fun reserveBook(bookId: Long, userName: String, days: Int): Boolean {
        // The screen checks these too, but the repository never trusts its caller
        if (!BookingRules.isValidName(userName) || !BookingRules.isValidDuration(days)) return false

        val now = System.currentTimeMillis()
        val booking = BookingEntity(
            bookOwnerId = bookId,
            userName = BookingRules.sanitizeName(userName),
            bookingDate = now,
            returnDeadline = BookingRules.deadlineFrom(now, days)
        )
        return bookingDao.reserveBook(booking)
    }

    /** The student has picked the book up: PENDING becomes ACTIVE. */
    suspend fun collectBooking(bookingId: Long) {
        bookingDao.updateStatus(bookingId, BookingStatus.ACTIVE)
    }

    /** Returns false if renewing would pass the maximum loan length. */
    suspend fun renewBooking(bookingId: Long, bookingDate: Long, currentDeadline: Long): Boolean {
        if (!BookingRules.canRenew(bookingDate, currentDeadline)) return false

        val newDeadline = BookingRules.deadlineFrom(currentDeadline, BookingRules.RENEWAL_DAYS)
        bookingDao.updateReturnDeadline(bookingId, newDeadline)
        return true
    }

    suspend fun returnBook(bookingId: Long, bookId: Long) {
        bookingDao.returnBook(bookingId, bookId)
    }

    suspend fun cancelBooking(bookingId: Long, bookId: Long) {
        bookingDao.cancelBooking(bookingId, bookId)
    }
}
