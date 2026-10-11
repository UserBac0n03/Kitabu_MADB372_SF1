package com.stadiolinks.kitabu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.stadiolinks.kitabu.data.local.entity.BookingEntity
import com.stadiolinks.kitabu.data.local.entity.BookingStatus
import com.stadiolinks.kitabu.data.local.model.BookingWithBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    // Create
    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    // Read: bookings joined with their book, soonest deadline first
    @Query(
        """
        SELECT bookings.*, books.title AS title, books.author AS author, books.category AS category
        FROM bookings
        INNER JOIN books ON books.bookId = bookings.bookOwnerId
        WHERE bookings.status != :excluded
        ORDER BY bookings.returnDeadline ASC
        """
    )
    fun getBookingsWithBook(
        excluded: BookingStatus = BookingStatus.RETURNED
    ): Flow<List<BookingWithBook>>

    // Update
    @Query("UPDATE bookings SET returnDeadline = :newDeadline WHERE bookingId = :bookingId")
    suspend fun updateReturnDeadline(bookingId: Long, newDeadline: Long)

    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateStatus(bookingId: Long, status: BookingStatus)

    /** Returns the number of rows changed: 0 means the book was not in the expected state. */
    @Query(
        """
        UPDATE books SET isAvailable = :isAvailable
        WHERE bookId = :bookId AND isAvailable != :isAvailable
        """
    )
    suspend fun setBookAvailability(bookId: Long, isAvailable: Boolean): Int

    // Delete: only a booking that has not been collected yet can be removed
    @Query("DELETE FROM bookings WHERE bookingId = :bookingId AND status = :status")
    suspend fun deleteBookingWithStatus(
        bookingId: Long,
        status: BookingStatus = BookingStatus.PENDING
    ): Int

    /*
     * The three operations below each touch both tables. @Transaction makes
     * them all-or-nothing, so a book can never be left out of step with its booking.
     */

    /** Reserves a book. Returns false if someone else already holds it. */
    @Transaction
    suspend fun reserveBook(booking: BookingEntity): Boolean {
        val claimed = setBookAvailability(booking.bookOwnerId, isAvailable = false)
        if (claimed == 0) return false
        insertBooking(booking)
        return true
    }

    @Transaction
    suspend fun returnBook(bookingId: Long, bookId: Long) {
        updateStatus(bookingId, BookingStatus.RETURNED)
        setBookAvailability(bookId, isAvailable = true)
    }

    @Transaction
    suspend fun cancelBooking(bookingId: Long, bookId: Long) {
        val deleted = deleteBookingWithStatus(bookingId)
        if (deleted > 0) setBookAvailability(bookId, isAvailable = true)
    }
}
