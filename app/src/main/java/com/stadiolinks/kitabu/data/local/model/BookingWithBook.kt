package com.stadiolinks.kitabu.data.local.model

import androidx.room.Embedded
import com.stadiolinks.kitabu.data.local.entity.BookingEntity

/** Result row of the bookings-to-books JOIN used by the dashboard. */
data class BookingWithBook(
    @Embedded val booking: BookingEntity,
    val title: String,
    val author: String,
    val category: String
)
