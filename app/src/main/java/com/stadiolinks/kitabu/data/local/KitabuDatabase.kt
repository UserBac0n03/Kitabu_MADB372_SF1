package com.stadiolinks.kitabu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.stadiolinks.kitabu.data.local.dao.BookDao
import com.stadiolinks.kitabu.data.local.dao.BookingDao
import com.stadiolinks.kitabu.data.local.entity.BookEntity
import com.stadiolinks.kitabu.data.local.entity.BookingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [BookEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KitabuDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun bookingDao(): BookingDao

    companion object {
        private const val DATABASE_NAME = "kitabu.db"

        // @Volatile: a write by one thread is immediately visible to all others
        @Volatile
        private var INSTANCE: KitabuDatabase? = null

        /** Returns the one shared database, creating it on first use. */
        fun getInstance(context: Context): KitabuDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KitabuDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(SeedCallback)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }

        /** Runs once, when the database file is first created, to add the sample books. */
        private object SeedCallback : Callback() {
            private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch {
                    INSTANCE?.bookDao()?.insertBooks(SampleBooks.all)
                }
            }
        }
    }
}
