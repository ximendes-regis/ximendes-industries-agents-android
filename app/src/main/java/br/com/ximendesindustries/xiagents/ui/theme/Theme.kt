package br.com.ximendesindustries.xiagents.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val XiAgentsDarkColorScheme = darkColorScheme(
    primary = XiPrimary,
    onPrimary = XiWhite,
    secondary = XiSecondary,
    onSecondary = XiWhite,
    tertiary = XiAccent,
    onTertiary = XiWhite,
    background = XiBackground,
    onBackground = XiWhite,
    surface = XiSurface,
    onSurface = XiWhite,
    surfaceVariant = XiSurface, // Usando a mesma cor de surface base, ou poderia ser ligeiramente diferente
    onSurfaceVariant = XiTextSecondary,
    outline = XiTextMuted,
    outlineVariant = XiBorderSubtle,
    error = XiError,
    onError = XiWhite
)

@Composable
fun XiAgentsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Definido como false por padrão para garantir que a paleta da marca seja usada
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Usamos o tema escuro como padrão para a marca, mesmo se o sistema estiver em modo claro,
        // a menos que um tema claro específico seja implementado futuramente.
        else -> XiAgentsDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
