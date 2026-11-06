package ec.edu.puce.calmajoven.data.source

import ec.edu.puce.calmajoven.data.model.EjercicioRespiracion
import ec.edu.puce.calmajoven.data.model.EvaluacionEmocional
import ec.edu.puce.calmajoven.data.model.NivelEstres
import ec.edu.puce.calmajoven.data.model.PsicologoVoluntario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Datos de prueba en memoria con la misma forma que tendrá la respuesta del backend.
 * TODO Semana 11-12: reemplazar por un Repository que lea de Room (historial) y Retrofit
 * (ejercicios y voluntarios). TODO Semana 14-15: ML Kit para el análisis del texto libre.
 */
object FuenteDatosLocal {

    const val TELEFONO_ECU911 = "911"
    const val TELEFONO_MSP = "171"

    private const val FORMATO_FECHA = "yyyy-MM-dd"

    fun formatearFecha(calendario: Calendar): String =
        SimpleDateFormat(FORMATO_FECHA, Locale.US).format(calendario.time)

    fun historialPrueba(hoy: Calendar = Calendar.getInstance()): List<EvaluacionEmocional> {
        val ayer = (hoy.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        return listOf(
            evaluacionSemilla("semilla-hoy", puntaje = 5, fecha = formatearFecha(hoy)),
            evaluacionSemilla("semilla-ayer", puntaje = 2, fecha = formatearFecha(ayer))
        )
    }

    val ejercicios: List<EjercicioRespiracion> = listOf(
        EjercicioRespiracion(
            id = 1,
            titulo = "Respirar 4 Tiempos",
            descripcion = "Baja el ritmo del cuerpo en menos de dos minutos.",
            pasos = "1. Inhala por la nariz contando hasta 4.\n" +
                "2. Sostén el aire contando hasta 4.\n" +
                "3. Exhala por la boca contando hasta 4.\n" +
                "4. Espera 4 segundos y repite cuatro veces."
        ),
        EjercicioRespiracion(
            id = 2,
            titulo = "Técnica de Relajación",
            descripcion = "Suelta la tensión músculo por músculo.",
            pasos = "1. Siéntate cómodo y cierra los ojos.\n" +
                "2. Aprieta los puños 5 segundos y suéltalos.\n" +
                "3. Repite con hombros, cara y piernas.\n" +
                "4. Nota la diferencia entre tensión y calma."
        ),
        EjercicioRespiracion(
            id = 3,
            titulo = "Anclaje 5-4-3-2-1",
            descripcion = "Vuelve al presente cuando la mente se acelera.",
            pasos = "1. Nombra 5 cosas que ves.\n" +
                "2. Toca 4 cosas a tu alrededor.\n" +
                "3. Escucha 3 sonidos.\n" +
                "4. Identifica 2 olores y 1 sabor."
        )
    )

    val psicologos: List<PsicologoVoluntario> = listOf(
        PsicologoVoluntario(1, "Dra. Ana", "Psicología clínica juvenil", "Lun a Vie · 15:00–18:00"),
        PsicologoVoluntario(2, "Dr. Luis", "Manejo de ansiedad y estrés", "Sáb · 09:00–12:00")
    )

    private fun evaluacionSemilla(id: String, puntaje: Int, fecha: String): EvaluacionEmocional {
        val nivel = NivelEstres.desdePuntaje(puntaje)
        return EvaluacionEmocional(
            id = id,
            apodo = "",
            edad = null,
            puntaje = puntaje,
            diagnostico = nivel.codigoDiagnostico,
            recomendaciones = nivel.codigoRecomendacion,
            fecha = fecha
        )
    }
}
