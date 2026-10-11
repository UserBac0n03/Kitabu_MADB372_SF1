package com.stadiolinks.kitabu.data.local

import androidx.room.TypeConverter
import com.stadiolinks.kitabu.data.local.entity.BookingStatus

/** SQLite has no enum type, so [BookingStatus] is stored as its name (TEXT). */
class Converters {

    @TypeConverter
    fun fromStatus(status: BookingStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): BookingStatus = BookingStatus.valueOf(value)
}
