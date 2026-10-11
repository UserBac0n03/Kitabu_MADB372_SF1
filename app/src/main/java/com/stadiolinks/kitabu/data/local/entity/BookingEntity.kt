package com.stadiolinks.kitabu.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A reservation of one book by one user. One row in the `bookings` table.
 *
 * [bookOwnerId] points at [BookEntity.bookId]. CASCADE means deleting a book
 * also deletes its bookings, so no booking can reference a missing book.
 */
@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookOwnerId")]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val bookingId: Long = 0,
    val bookOwnerId: Long,
    val userName: String,
    val bookingDate: Long,
    val returnDeadline: Long,
    val status: BookingStatus = BookingStatus.PENDING
)
