package com.stadiolinks.kitabu.domain

import java.util.concurrent.TimeUnit
import kotlin.math.ceil

/** The library's rules about names and loan lengths, kept in one place. */
object BookingRules {

    /** Loan lengths the student can choose from when reserving. */
    val RENTAL_DURATIONS_DAYS = listOf(7, 14, 21)

    /** How many days one renewal adds. */
    const val RENEWAL_DAYS = 7

    /** The longest a book may be kept, counted from the day it was reserved. */
    const val MAX_LOAN_DAYS = 42

    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 40

    private val MILLIS_PER_DAY = TimeUnit.DAYS.toMillis(1)

    /** The timestamp [days] after [start]. */
    fun deadlineFrom(start: Long, days: Int): Long = start + days * MILLIS_PER_DAY

    /** Whole days left until [deadline], rounded up. Negative when overdue. */
    fun daysRemaining(deadline: Long, now: Long = System.currentTimeMillis()): Long =
        ceil((deadline - now) / MILLIS_PER_DAY.toDouble()).toLong()

    /** True if renewing would still return the book within [MAX_LOAN_DAYS] of [bookingDate]. */
    fun canRenew(bookingDate: Long, currentDeadline: Long): Boolean {
        val renewedDeadline = deadlineFrom(currentDeadline, RENEWAL_DAYS)
        return renewedDeadline <= deadlineFrom(bookingDate, MAX_LOAN_DAYS)
    }

    /** Applied while typing: keeps letters, spaces, hyphens and apostrophes, up to the length limit. */
    fun cleanNameInput(input: String): String =
        input.filter { it.isLetter() || it == ' ' || it == '-' || it == '\'' }
            .take(MAX_NAME_LENGTH)

    /** Applied before saving: removes outer spaces and squeezes repeated spaces into one. */
    fun sanitizeName(input: String): String =
        cleanNameInput(input).trim().replace(Regex("\\s+"), " ")

    fun isValidName(input: String): Boolean =
        sanitizeName(input).count { it.isLetter() } >= MIN_NAME_LENGTH

    fun isValidDuration(days: Int): Boolean = days in RENTAL_DURATIONS_DAYS
}
