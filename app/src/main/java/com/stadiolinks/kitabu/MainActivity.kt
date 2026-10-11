package com.stadiolinks.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.stadiolinks.kitabu.ui.navigation.KitabuApp
import com.stadiolinks.kitabu.ui.theme.KitabuTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** The app's only Activity. It just hands the screen over to Compose. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Hold the splash screen for about a second, so it is seen even on a fast phone.
        // installSplashScreen has to be called before super.onCreate.
        var keepSplash = true
        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(SPLASH_MILLIS)
            keepSplash = false
            // The condition is only re-checked when the screen redraws, so ask for a redraw
            window.decorView.invalidate()
        }
        // The app is dark, so ask for light clock and battery icons in the status bar
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            KitabuTheme {
                KitabuApp()
            }
        }
    }

    private companion object {
        const val SPLASH_MILLIS = 1_000L
    }
}
