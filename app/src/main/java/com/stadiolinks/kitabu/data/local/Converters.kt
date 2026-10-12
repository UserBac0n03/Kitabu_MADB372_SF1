package com.stadiolinks.kitabu.data.local

import androidx.room.TypeConverter
import com.stadiolinks.kitabu.data.local.entity.BookingStatus
import java.util.Date

/**
 * SQLite can't store enums or dates, so these convert them.
 * BookingStatus goes in as its name (TEXT), Date as millis (INTEGER).
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
