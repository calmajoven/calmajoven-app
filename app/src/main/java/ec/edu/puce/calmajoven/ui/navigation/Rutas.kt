package ec.edu.puce.calmajoven.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector
import ec.edu.puce.calmajoven.R
import kotlinx.serialization.Serializable

// Rutas tipadas: el compilador impide navegar a destinos inexistentes o con argumentos erróneos.
@Serializable
data object TestRuta

@Serializable
data object GuiasRuta

@Serializable
data object AyudaRuta

enum class PestanaPrincipal(
    val ruta: Any,
    @StringRes val etiqueta: Int,
    val icono: ImageVector
) {
    TEST(TestRuta, R.string.nav_test, Icons.Filled.Face),
    GUIAS(GuiasRuta, R.string.nav_guias, Icons.Filled.Favorite),
    AYUDA(AyudaRuta, R.string.nav_ayuda, Icons.Filled.Phone)
}
