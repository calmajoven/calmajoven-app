package ec.edu.puce.calmajoven.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val EsquemaClaro = lightColorScheme(
    primary = MentaPrimario,
    onPrimary = Color.White,
    primaryContainer = MentaContenedor,
    onPrimaryContainer = MentaSobreContenedor,
    secondary = AzulSereno,
    onSecondary = Color.White,
    secondaryContainer = AzulContenedor,
    onSecondaryContainer = AzulSobreContenedor,
    tertiary = Arena,
    onTertiary = Color.White,
    tertiaryContainer = ArenaContenedor,
    onTertiaryContainer = ArenaSobreContenedor,
    error = ErrorClaro,
    onError = Color.White,
    errorContainer = ErrorContenedorClaro,
    onErrorContainer = SobreErrorContenedorClaro,
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = FondoClaro,
    onSurface = TextoClaro,
    surfaceVariant = VarianteClaro,
    onSurfaceVariant = SobreVarianteClaro,
    surfaceTint = MentaPrimario,
    outline = ContornoClaro,
    outlineVariant = ContornoSuaveClaro,
    surfaceContainerLowest = ContenedorMasBajoClaro,
    surfaceContainerLow = ContenedorBajoClaro,
    surfaceContainer = ContenedorClaro,
    surfaceContainerHigh = ContenedorAltoClaro,
    surfaceContainerHighest = ContenedorMasAltoClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = MentaPrimarioOscuro,
    onPrimary = SobreMentaOscuro,
    primaryContainer = MentaContenedorOscuro,
    onPrimaryContainer = MentaContenedor,
    secondary = AzulSerenoOscuro,
    onSecondary = SobreAzulOscuro,
    secondaryContainer = AzulContenedorOscuro,
    onSecondaryContainer = AzulContenedor,
    tertiary = ArenaOscuro,
    onTertiary = SobreArenaOscuro,
    tertiaryContainer = ArenaContenedorOscuro,
    onTertiaryContainer = ArenaContenedor,
    error = ErrorOscuro,
    onError = SobreErrorOscuro,
    errorContainer = ErrorContenedorOscuro,
    onErrorContainer = ErrorContenedorClaro,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = FondoOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = VarianteOscuro,
    onSurfaceVariant = SobreVarianteOscuro,
    surfaceTint = MentaPrimarioOscuro,
    outline = ContornoOscuro,
    outlineVariant = ContornoSuaveOscuro,
    surfaceContainerLowest = ContenedorMasBajoOscuro,
    surfaceContainerLow = ContenedorBajoOscuro,
    surfaceContainer = ContenedorOscuro,
    surfaceContainerHigh = ContenedorAltoOscuro,
    surfaceContainerHighest = ContenedorMasAltoOscuro
)

/** Color semántico del SOS: fijo, para que la emergencia se reconozca aunque cambie el tema. */
@Immutable
data class ColoresSos(val contenedor: Color, val contenido: Color)

val LocalColoresSos = staticCompositionLocalOf {
    ColoresSos(contenedor = CoralSos, contenido = SobreCoralSos)
}

/**
 * El modo oscuro sigue al sistema. `dynamicColor` (Material You, API 31+) queda desactivado por
 * defecto para conservar la paleta de calma de la marca; basta pasar `true` para activarlo.
 */
@Composable
fun CalmaJovenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }
    val coloresSos = if (darkTheme) {
        ColoresSos(contenedor = CoralSosOscuro, contenido = SobreCoralSosOscuro)
    } else {
        ColoresSos(contenedor = CoralSos, contenido = SobreCoralSos)
    }

    CompositionLocalProvider(LocalColoresSos provides coloresSos) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
