package com.tuempresa.Vanta.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VantaColorScheme = lightColorScheme(

    primary = PrimaryColor,
    onPrimary = Color.White,

    secondary = SecondaryColor,
    onSecondary = Color.Black,

    background = VantaBlack,
    onBackground = WhiteText,

    surface = VantaSurface,
    onSurface = WhiteText,

    surfaceVariant = VantaSurface,
    onSurfaceVariant = WhiteText
)

@Composable
fun VantaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = VantaColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = VantaBlack.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}