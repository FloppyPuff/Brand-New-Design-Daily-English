package me.fujunhao.engstudy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2F6BFF),
    onPrimary = Color.White,
    background = Color(0xFFF4F5F7),
    onBackground = Color(0xFF171A1C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF171A1C),
    onSurfaceVariant = Color(0xFF5C6368),
    surfaceContainerHighest = Color(0xFFFFFFFF),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FA9FF),
    onPrimary = Color(0xFF00276B),
    background = Color(0xFF0E1113),
    onBackground = Color(0xFFE6E8EA),
    surface = Color(0xFF171A1E),
    onSurface = Color(0xFFE6E8EA),
    onSurfaceVariant = Color(0xFF9AA2A8),
    surfaceContainerHighest = Color(0xFF171A1E),
)

@Composable
fun EngStudyTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = Typography(),
        content = content,
    )
}
