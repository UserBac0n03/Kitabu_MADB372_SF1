package com.stadiolinks.kitabu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A single library title. One row in the `books` table. */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId: Long = 0,
    val title: String,
    val author: String,
    val category: String,
    val isAvailable: Boolean = true
)
