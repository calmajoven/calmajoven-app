package ec.edu.puce.calmajoven.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import ec.edu.puce.calmajoven.data.model.NivelEstres
import ec.edu.puce.calmajoven.ui.components.FechaRelativa
import ec.edu.puce.calmajoven.ui.components.fechaRelativa
import ec.edu.puce.calmajoven.ui.state.CodigosError
import ec.edu.puce.calmajoven.ui.state.DiagnosticoUiState
import ec.edu.puce.calmajoven.ui.state.OpcionRapida
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class CalmaJovenViewModelTest {

    @Test
    fun clasificaLosTresRangos() {
        assertEquals(NivelEstres.CALMA, NivelEstres.desdePuntaje(1))
        assertEquals(NivelEstres.CALMA, NivelEstres.desdePuntaje(3))
        assertEquals(NivelEstres.MODERADO, NivelEstres.desdePuntaje(4))
        assertEquals(NivelEstres.MODERADO, NivelEstres.desdePuntaje(7))
        assertEquals(NivelEstres.ALTO, NivelEstres.desdePuntaje(8))
        assertEquals(NivelEstres.ALTO, NivelEstres.desdePuntaje(10))
    }

    @Test
    fun opcionesRapidasCaenEnSuRango() {
        assertEquals(NivelEstres.CALMA, NivelEstres.desdePuntaje(OpcionRapida.BAJO.valor))
        assertEquals(NivelEstres.MODERADO, NivelEstres.desdePuntaje(OpcionRapida.MEDIO.valor))
        assertEquals(NivelEstres.ALTO, NivelEstres.desdePuntaje(OpcionRapida.ALTO.valor))
    }

    @Test
    fun sinRespuestaEmiteErrorSinCrash() {
        val vm = CalmaJovenViewModel(SavedStateHandle())
        vm.evaluar()
        assertEquals(DiagnosticoUiState.Error(CodigosError.NIVEL_REQUERIDO), vm.uiState.value)
    }

    @Test
    fun respuestaFueraDeRangoEmiteError() {
        val vm = CalmaJovenViewModel(SavedStateHandle())
        vm.onRespuestaChange("11")
        vm.evaluar()
        assertEquals(DiagnosticoUiState.Error(CodigosError.NIVEL_RANGO), vm.uiState.value)
    }

    @Test
    fun edadInvalidaEmiteError() {
        val vm = CalmaJovenViewModel(SavedStateHandle())
        vm.onRespuestaChange("5")
        vm.onEdadChange("5")
        vm.evaluar()
        assertEquals(DiagnosticoUiState.Error(CodigosError.EDAD_INVALIDA), vm.uiState.value)
    }

    @Test
    fun entradasNoNumericasSeDescartan() {
        val vm = CalmaJovenViewModel(SavedStateHandle())
        vm.onRespuestaChange("a7b")
        vm.onEdadChange("1x8y9")
        assertEquals("7", vm.formulario.value.respuestaTexto)
        assertEquals("18", vm.formulario.value.edadTexto)
    }

    @Test
    fun corregirLaEntradaLimpiaElError() {
        val vm = CalmaJovenViewModel(SavedStateHandle())
        vm.evaluar()
        vm.onOpcionRapida(OpcionRapida.MEDIO)
        assertEquals(DiagnosticoUiState.Idle, vm.uiState.value)
        assertEquals("5", vm.formulario.value.respuestaTexto)
    }

    @Test
    fun formularioSobreviveALaMuerteDelProceso() {
        val handle = SavedStateHandle()
        CalmaJovenViewModel(handle).apply {
            onApodoChange("Sol")
            onEdadChange("17")
            onOpcionRapida(OpcionRapida.ALTO)
        }
        val restaurado = CalmaJovenViewModel(handle).formulario.value
        assertEquals("Sol", restaurado.apodo)
        assertEquals("17", restaurado.edadTexto)
        assertEquals("9", restaurado.respuestaTexto)
        assertEquals(OpcionRapida.ALTO, restaurado.opcionRapida)
    }

    @Test
    fun resultadoGuardadoSeRestauraComoSuccess() {
        val handle = SavedStateHandle(
            mapOf(
                "resultado_id" to "abc",
                "resultado_apodo" to "",
                "resultado_edad" to -1,
                "resultado_puntaje" to 8,
                "resultado_fecha" to "2026-11-05"
            )
        )
        val estado = CalmaJovenViewModel(handle).uiState.value as DiagnosticoUiState.Success
        assertEquals(NivelEstres.ALTO.codigoDiagnostico, estado.evaluacion.diagnostico)
        assertNull(estado.evaluacion.edad)
    }

    @Test
    fun fechaRelativaReconoceHoyYAyer() {
        val hoy = Calendar.getInstance().apply { set(2026, Calendar.NOVEMBER, 5) }
        assertEquals(FechaRelativa.Hoy, fechaRelativa("2026-11-05", hoy))
        assertEquals(FechaRelativa.Ayer, fechaRelativa("2026-11-04", hoy))
        assertEquals(FechaRelativa.Otra("fecha-rota"), fechaRelativa("fecha-rota", hoy))
    }
}
