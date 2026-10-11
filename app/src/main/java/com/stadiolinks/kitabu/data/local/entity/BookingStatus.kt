package com.stadiolinks.kitabu.data.local.entity

/** Lifecycle of a booking: reserved, collected, then handed back. */
enum class BookingStatus {
    PENDING,
    ACTIVE,
    RETURNED
}
