package com.stadiolinks.kitabu.di

import android.content.Context
import com.stadiolinks.kitabu.data.local.KitabuDatabase
import com.stadiolinks.kitabu.data.repository.LibraryRepository

/**
 * Manual dependency injection: builds the objects the app shares
 * (database, repository) once, so ViewModels can be handed them
 * instead of creating their own.
 */
class AppContainer(context: Context) {

    private val database by lazy { KitabuDatabase.getInstance(context) }

    val libraryRepository by lazy {
        LibraryRepository(database.bookDao(), database.bookingDao())
    }
}
