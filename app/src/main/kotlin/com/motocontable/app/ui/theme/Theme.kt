package com.motocontable.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary          = NaranjaClaro,
    onPrimary        = Blanco,
    primaryContainer = NaranjaSuave,
    onPrimaryContainer = NaranjaMotor,

    secondary        = VerdeIngreso,
    onSecondary      = Blanco,
    secondaryContainer = VerdeSuave,
    onSecondaryContainer = VerdeIngreso,

    error            = RojoAusencia,
    errorContainer   = RojoSuave,

    background       = GrisClaro,
    onBackground     = GrisOscuro,
    surface          = Blanco,
    onSurface        = GrisOscuro,
    surfaceVariant   = NaranjaSuave,
    onSurfaceVariant = GrisMedio,
)

@Composable
fun MotoContableTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
