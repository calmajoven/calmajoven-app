package ec.edu.puce.calmajoven.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ec.edu.puce.calmajoven.R
import ec.edu.puce.calmajoven.data.model.NivelEstres
import ec.edu.puce.calmajoven.ui.state.CodigosError
import ec.edu.puce.calmajoven.ui.state.OpcionRapida
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@StringRes
fun NivelEstres.diagnosticoRes(): Int = when (this) {
    NivelEstres.CALMA -> R.string.diagnostico_calma
    NivelEstres.MODERADO -> R.string.diagnostico_moderado
    NivelEstres.ALTO -> R.string.diagnostico_alto
}

@StringRes
fun NivelEstres.recomendacionRes(): Int = when (this) {
    NivelEstres.CALMA -> R.string.recomendacion_calma
    NivelEstres.MODERADO -> R.string.recomendacion_moderado
    NivelEstres.ALTO -> R.string.recomendacion_alto
}

@StringRes
fun OpcionRapida.etiquetaRes(): Int = when (this) {
    OpcionRapida.BAJO -> R.string.opcion_bajo
    OpcionRapida.MEDIO -> R.string.opcion_medio
    OpcionRapida.ALTO -> R.string.opcion_alto
}

/** Traduce un código de diagnóstico; si llega uno desconocido (p. ej. del backend) se muestra tal cual. */
@Composable
fun textoDiagnostico(codigo: String): String =
    NivelEstres.desdeCodigo(codigo)?.let { stringResource(it.diagnosticoRes()) } ?: codigo

@Composable
fun textoRecomendacion(codigo: String): String =
    NivelEstres.desdeCodigo(codigo)?.let { stringResource(it.recomendacionRes()) } ?: codigo

@Composable
fun textoError(codigo: String): String = stringResource(
    when (codigo) {
        CodigosError.NIVEL_REQUERIDO -> R.string.error_nivel_requerido
        CodigosError.NIVEL_RANGO -> R.string.error_nivel_rango
        CodigosError.EDAD_INVALIDA -> R.string.error_edad_invalida
        else -> R.string.error_desconocido
    }
)

sealed interface FechaRelativa {
    data object Hoy : FechaRelativa
    data object Ayer : FechaRelativa
    data class Otra(val texto: String) : FechaRelativa
}

/** Convierte una fecha ISO (yyyy-MM-dd) en Hoy / Ayer / dd/MM/yyyy. Función pura para poder probarla. */
fun fechaRelativa(fechaIso: String, hoy: Calendar = Calendar.getInstance()): FechaRelativa {
    val fecha = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }.parse(fechaIso)
    } catch (_: ParseException) {
        null
    } ?: return FechaRelativa.Otra(fechaIso)

    val dia = Calendar.getInstance().apply { time = fecha }
    val ayer = (hoy.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
    return when {
        mismoDia(dia, hoy) -> FechaRelativa.Hoy
        mismoDia(dia, ayer) -> FechaRelativa.Ayer
        else -> FechaRelativa.Otra(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha))
    }
}

@Composable
fun textoFecha(fechaIso: String): String = when (val relativa = fechaRelativa(fechaIso)) {
    FechaRelativa.Hoy -> stringResource(R.string.fecha_hoy)
    FechaRelativa.Ayer -> stringResource(R.string.fecha_ayer)
    is FechaRelativa.Otra -> relativa.texto
}

private fun mismoDia(a: Calendar, b: Calendar) =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
        a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
