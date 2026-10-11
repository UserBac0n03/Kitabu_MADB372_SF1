package com.stadiolinks.kitabu

import android.app.Application
import com.stadiolinks.kitabu.di.AppContainer

/** Created once when the app starts, before any screen. Holds the shared [AppContainer]. */
class KitabuApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
