package ec.edu.puce.calmajoven.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.calmajoven.data.model.EjercicioRespiracion
import ec.edu.puce.calmajoven.data.model.EvaluacionEmocional
import ec.edu.puce.calmajoven.data.model.NivelEstres
import ec.edu.puce.calmajoven.data.model.PsicologoVoluntario
import ec.edu.puce.calmajoven.data.source.FuenteDatosLocal
import ec.edu.puce.calmajoven.ui.state.CodigosError
import ec.edu.puce.calmajoven.ui.state.DiagnosticoUiState
import ec.edu.puce.calmajoven.ui.state.OpcionRapida
import ec.edu.puce.calmajoven.ui.state.TestFormState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class CalmaJovenViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _formulario = MutableStateFlow(leerFormulario())
    val formulario: StateFlow<TestFormState> = _formulario.asStateFlow()

    private val _uiState = MutableStateFlow<DiagnosticoUiState>(DiagnosticoUiState.Idle)
    val uiState: StateFlow<DiagnosticoUiState> = _uiState.asStateFlow()

    // TODO Semana 11-12: historialRepository.observarHistorial() (Room) en lugar de la lista en memoria.
    private val _historial = MutableStateFlow(FuenteDatosLocal.historialPrueba())
    val historial: StateFlow<List<EvaluacionEmocional>> = _historial.asStateFlow()

    // TODO Semana 11-12: guiasRepository.obtenerEjercicios() (Retrofit, con caché en Room).
    private val _ejercicios = MutableStateFlow(FuenteDatosLocal.ejercicios)
    val ejercicios: StateFlow<List<EjercicioRespiracion>> = _ejercicios.asStateFlow()

    // TODO Semana 11-12: apoyoRepository.obtenerVoluntarios() (Retrofit).
    private val _psicologos = MutableStateFlow(FuenteDatosLocal.psicologos)
    val psicologos: StateFlow<List<PsicologoVoluntario>> = _psicologos.asStateFlow()

    init {
        restaurarResultado()
    }

    fun onApodoChange(valor: String) {
        actualizarFormulario { it.copy(apodo = valor.take(MAX_APODO)) }
    }

    fun onEdadChange(valor: String) {
        actualizarFormulario { it.copy(edadTexto = soloDigitos(valor)) }
    }

    fun onRespuestaChange(valor: String) {
        actualizarFormulario { it.copy(respuestaTexto = soloDigitos(valor), opcionRapida = null) }
    }

    fun onOpcionRapida(opcion: OpcionRapida) {
        actualizarFormulario {
            it.copy(respuestaTexto = opcion.valor.toString(), opcionRapida = opcion)
        }
    }

    fun evaluar() {
        if (_uiState.value is DiagnosticoUiState.Loading) return
        val form = _formulario.value

        val puntaje = form.respuestaTexto.trim().toIntOrNull()
            ?: return emitirError(CodigosError.NIVEL_REQUERIDO)
        if (puntaje !in NivelEstres.PUNTAJE_VALIDO) return emitirError(CodigosError.NIVEL_RANGO)

        // Edad opcional: vacía → null; con texto, debe ser un número dentro del rango.
        val edadTexto = form.edadTexto.trim()
        val edad: Int? = if (edadTexto.isEmpty()) {
            null
        } else {
            edadTexto.toIntOrNull()?.takeIf { it in RANGO_EDAD }
                ?: return emitirError(CodigosError.EDAD_INVALIDA)
        }

        val evaluacion = construirEvaluacion(
            id = UUID.randomUUID().toString(),
            apodo = form.apodo.trim(),
            edad = edad,
            puntaje = puntaje,
            fecha = FuenteDatosLocal.formatearFecha(Calendar.getInstance())
        )

        viewModelScope.launch {
            _uiState.value = DiagnosticoUiState.Loading
            // TODO Semana 11-12: aquí se guardará la evaluación con el Repository (Room) en Dispatchers.IO.
            delay(RETARDO_SIMULADO_MS)
            _historial.update { anteriores -> listOf(evaluacion) + anteriores }
            guardarResultado(evaluacion)
            _uiState.value = DiagnosticoUiState.Success(evaluacion)
        }
    }

    fun nuevoTest() {
        CLAVES_FORMULARIO.forEach { savedStateHandle.remove<Any>(it) }
        CLAVES_RESULTADO.forEach { savedStateHandle.remove<Any>(it) }
        _formulario.value = TestFormState()
        _uiState.value = DiagnosticoUiState.Idle
    }

    private fun emitirError(codigo: String) {
        _uiState.value = DiagnosticoUiState.Error(codigo)
    }

    private inline fun actualizarFormulario(transformar: (TestFormState) -> TestFormState) {
        val nuevo = transformar(_formulario.value)
        _formulario.value = nuevo
        savedStateHandle[KEY_APODO] = nuevo.apodo
        savedStateHandle[KEY_EDAD] = nuevo.edadTexto
        savedStateHandle[KEY_RESPUESTA] = nuevo.respuestaTexto
        savedStateHandle[KEY_OPCION] = nuevo.opcionRapida?.name
        // Corregir una entrada descarta el aviso de error anterior.
        if (_uiState.value is DiagnosticoUiState.Error) _uiState.value = DiagnosticoUiState.Idle
    }

    private fun leerFormulario() = TestFormState(
        apodo = savedStateHandle[KEY_APODO] ?: "",
        edadTexto = savedStateHandle[KEY_EDAD] ?: "",
        respuestaTexto = savedStateHandle[KEY_RESPUESTA] ?: "",
        opcionRapida = savedStateHandle.get<String>(KEY_OPCION)
            ?.let { nombre -> OpcionRapida.entries.firstOrNull { it.name == nombre } }
    )

    // El resultado se guarda como primitivos para que la tarjeta reaparezca tras la muerte del proceso.
    private fun guardarResultado(evaluacion: EvaluacionEmocional) {
        savedStateHandle[KEY_RES_ID] = evaluacion.id
        savedStateHandle[KEY_RES_APODO] = evaluacion.apodo
        savedStateHandle[KEY_RES_EDAD] = evaluacion.edad ?: SIN_EDAD
        savedStateHandle[KEY_RES_PUNTAJE] = evaluacion.puntaje
        savedStateHandle[KEY_RES_FECHA] = evaluacion.fecha
    }

    private fun restaurarResultado() {
        val id = savedStateHandle.get<String>(KEY_RES_ID) ?: return
        val puntaje = savedStateHandle.get<Int>(KEY_RES_PUNTAJE) ?: return
        val evaluacion = construirEvaluacion(
            id = id,
            apodo = savedStateHandle[KEY_RES_APODO] ?: "",
            edad = savedStateHandle.get<Int>(KEY_RES_EDAD)?.takeIf { it != SIN_EDAD },
            puntaje = puntaje,
            fecha = savedStateHandle[KEY_RES_FECHA] ?: FuenteDatosLocal.formatearFecha(Calendar.getInstance())
        )
        _historial.update { lista -> if (lista.any { it.id == id }) lista else listOf(evaluacion) + lista }
        _uiState.value = DiagnosticoUiState.Success(evaluacion)
    }

    companion object {
        private const val KEY_APODO = "form_apodo"
        private const val KEY_EDAD = "form_edad"
        private const val KEY_RESPUESTA = "form_respuesta"
        private const val KEY_OPCION = "form_opcion"
        private const val KEY_RES_ID = "resultado_id"
        private const val KEY_RES_APODO = "resultado_apodo"
        private const val KEY_RES_EDAD = "resultado_edad"
        private const val KEY_RES_PUNTAJE = "resultado_puntaje"
        private const val KEY_RES_FECHA = "resultado_fecha"

        private val CLAVES_FORMULARIO = listOf(KEY_APODO, KEY_EDAD, KEY_RESPUESTA, KEY_OPCION)
        private val CLAVES_RESULTADO =
            listOf(KEY_RES_ID, KEY_RES_APODO, KEY_RES_EDAD, KEY_RES_PUNTAJE, KEY_RES_FECHA)

        private const val SIN_EDAD = -1
        private const val MAX_APODO = 24
        private const val MAX_DIGITOS = 2
        private const val RETARDO_SIMULADO_MS = 600L
        val RANGO_EDAD = 10..99

        private fun soloDigitos(valor: String) = valor.filter(Char::isDigit).take(MAX_DIGITOS)

        fun construirEvaluacion(
            id: String,
            apodo: String,
            edad: Int?,
            puntaje: Int,
            fecha: String
        ): EvaluacionEmocional {
            val nivel = NivelEstres.desdePuntaje(puntaje)
            return EvaluacionEmocional(
                id = id,
                apodo = apodo,
                edad = edad,
                puntaje = puntaje,
                diagnostico = nivel.codigoDiagnostico,
                recomendaciones = nivel.codigoRecomendacion,
                fecha = fecha
            )
        }
    }
}
