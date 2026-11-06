package ec.edu.puce.calmajoven.ui.state

import ec.edu.puce.calmajoven.data.model.EvaluacionEmocional

/**
 * Estados cerrados de la pantalla de Test. Al ser sellada, el `when` de la UI es exhaustivo:
 * si se agrega un estado y no se maneja, el proyecto no compila en lugar de fallar en ejecución.
 */
sealed class DiagnosticoUiState {
    object Idle : DiagnosticoUiState()
    object Loading : DiagnosticoUiState()
    data class Success(val evaluacion: EvaluacionEmocional) : DiagnosticoUiState()
    data class Error(val mensaje: String) : DiagnosticoUiState()
}

/** Códigos que viajan en [DiagnosticoUiState.Error.mensaje]; la UI los resuelve con strings.xml. */
object CodigosError {
    const val NIVEL_REQUERIDO = "ERROR_NIVEL_REQUERIDO"
    const val NIVEL_RANGO = "ERROR_NIVEL_RANGO"
    const val EDAD_INVALIDA = "ERROR_EDAD_INVALIDA"
}
