package com.stadiolinks.kitabu.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stadiolinks.kitabu.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBooks(books: List<BookEntity>)

    // Read: each Flow re-emits whenever the books table changes
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = :isAvailable ORDER BY title ASC")
    fun getBooksByAvailability(isAvailable: Boolean): Flow<List<BookEntity>>

    @Query(
        """
        SELECT * FROM books
        WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'
        ORDER BY title ASC
        """
    )
    fun searchBooks(query: String): Flow<List<BookEntity>>

    // Delete: bookings for the book are removed too (CASCADE)
    @Delete
    suspend fun deleteBook(book: BookEntity)
}
