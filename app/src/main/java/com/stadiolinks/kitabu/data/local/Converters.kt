package com.stadiolinks.kitabu.data.local

import androidx.room.TypeConverter
import com.stadiolinks.kitabu.data.local.entity.BookingStatus
import java.util.Date

/**
 * Maps types SQLite cannot store directly onto ones it can.
 * [BookingStatus] is stored as its name (TEXT); a [Date] as epoch milliseconds (INTEGER).
 */
class Converters {

    @TypeConverter
    fun fromStatus(status: BookingStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): BookingStatus = BookingStatus.valueOf(value)

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(millis: Long?): Date? = millis?.let { Date(it) }
}
