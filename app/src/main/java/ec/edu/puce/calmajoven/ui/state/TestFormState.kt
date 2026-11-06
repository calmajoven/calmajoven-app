package ec.edu.puce.calmajoven.ui.state

/** Botones de puntaje rápido; cada uno equivale a un valor dentro de su rango. */
enum class OpcionRapida(val valor: Int) {
    BAJO(2),
    MEDIO(5),
    ALTO(9)
}

/** Entradas transitorias del test, tal como las escribe el usuario (texto sin convertir). */
data class TestFormState(
    val apodo: String = "",
    val edadTexto: String = "",
    val respuestaTexto: String = "",
    val opcionRapida: OpcionRapida? = null
)
