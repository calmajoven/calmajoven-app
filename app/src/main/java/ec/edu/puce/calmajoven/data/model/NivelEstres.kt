package ec.edu.puce.calmajoven.data.model

/** Rangos orientativos del test: 1–3 calma, 4–7 estrés moderado, 8–10 ansiedad alta. */
enum class NivelEstres(val rango: IntRange) {
    CALMA(1..3),
    MODERADO(4..7),
    ALTO(8..10);

    val codigoDiagnostico: String get() = "DIAG_$name"
    val codigoRecomendacion: String get() = "REC_$name"

    companion object {
        val PUNTAJE_VALIDO: IntRange = 1..10

        fun desdePuntaje(puntaje: Int): NivelEstres = when {
            puntaje <= CALMA.rango.last -> CALMA
            puntaje <= MODERADO.rango.last -> MODERADO
            else -> ALTO
        }

        fun desdeCodigo(codigo: String): NivelEstres? =
            entries.firstOrNull { codigo == it.codigoDiagnostico || codigo == it.codigoRecomendacion }
    }
}
