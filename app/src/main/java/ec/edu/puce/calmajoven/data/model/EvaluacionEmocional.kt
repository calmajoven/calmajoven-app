package ec.edu.puce.calmajoven.data.model

/**
 * Resultado inmutable de un test. `diagnostico` y `recomendaciones` guardan códigos estables
 * (ver [NivelEstres]) y `fecha` usa ISO yyyy-MM-dd: la UI los traduce con strings.xml, así el
 * registro sigue siendo válido si cambia el idioma o cuando se persista en Room.
 */
data class EvaluacionEmocional(
    val id: String,
    val apodo: String,
    val edad: Int?,
    val puntaje: Int,
    val diagnostico: String,
    val recomendaciones: String,
    val fecha: String
)

data class EjercicioRespiracion(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val pasos: String
)

data class PsicologoVoluntario(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val horario: String
)
