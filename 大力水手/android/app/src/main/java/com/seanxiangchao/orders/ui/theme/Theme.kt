package com.seanxiangchao.orders.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = NavyBlue,
    onPrimary = White,
    primaryContainer = NavyBlueLight,
    onPrimaryContainer = White,
    secondary = SpinachGreen,
    onSecondary = White,
    secondaryContainer = SpinachGreenLight,
    onSecondaryContainer = Color.Black,
    tertiary = AnchorGold,
    onTertiary = Color.Black,
    tertiaryContainer = AnchorGoldLight,
    onTertiaryContainer = Color.Black,
    background = Gray50,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    error = ErrorRed,
    onError = White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C)
)

private val DarkColors = darkColorScheme(
    primary = NavyBlueLight,
    onPrimary = White,
    primaryContainer = NavyBlue,
    onPrimaryContainer = White,
    secondary = SpinachGreenLight,
    onSecondary = Color.Black,
    secondaryContainer = SpinachGreen,
    onSecondaryContainer = White,
    tertiary = AnchorGoldLight,
    onTertiary = Color.Black,
    tertiaryContainer = AnchorGold,
    onTertiaryContainer = Color.Black,
    background = Gray900,
    onBackground = White,
    surface = Gray800,
    onSurface = White,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray200,
    error = Color(0xFFEF5350),
    onError = Color.Black,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFCDD2)
)

@Composable
fun 大力水手Theme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}