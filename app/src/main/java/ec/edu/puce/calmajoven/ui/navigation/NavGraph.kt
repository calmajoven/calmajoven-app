package ec.edu.puce.calmajoven.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ec.edu.puce.calmajoven.ui.screens.GuidesScreen
import ec.edu.puce.calmajoven.ui.screens.SupportScreen
import ec.edu.puce.calmajoven.ui.screens.TestScreen
import ec.edu.puce.calmajoven.ui.viewmodel.CalmaJovenViewModel

@Composable
fun CalmaJovenNavGraph(
    navController: NavHostController = rememberNavController(),
    // Un único ViewModel a nivel de Activity: el historial que genera el Test se ve en Guías.
    viewModel: CalmaJovenViewModel = viewModel()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinoActual = backStackEntry?.destination
    val irAPestana: (PestanaPrincipal) -> Unit = { navController.navegarAPestana(it) }
    val bottomBar: @Composable () -> Unit = {
        CalmaJovenBottomBar(destinoActual = destinoActual, onPestanaClick = irAPestana)
    }

    NavHost(navController = navController, startDestination = TestRuta) {
        composable<TestRuta> {
            val formulario by viewModel.formulario.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            TestScreen(
                formulario = formulario,
                uiState = uiState,
                onApodoChange = viewModel::onApodoChange,
                onEdadChange = viewModel::onEdadChange,
                onRespuestaChange = viewModel::onRespuestaChange,
                onOpcionRapida = viewModel::onOpcionRapida,
                onEvaluar = viewModel::evaluar,
                onNuevoTest = viewModel::nuevoTest,
                onIrAAyuda = { irAPestana(PestanaPrincipal.AYUDA) },
                bottomBar = bottomBar
            )
        }
        composable<GuiasRuta> {
            val ejercicios by viewModel.ejercicios.collectAsStateWithLifecycle()
            val historial by viewModel.historial.collectAsStateWithLifecycle()
            GuidesScreen(ejercicios = ejercicios, historial = historial, bottomBar = bottomBar)
        }
        composable<AyudaRuta> {
            val psicologos by viewModel.psicologos.collectAsStateWithLifecycle()
            SupportScreen(psicologos = psicologos, bottomBar = bottomBar)
        }
    }
}

@Composable
fun CalmaJovenBottomBar(
    destinoActual: NavDestination?,
    onPestanaClick: (PestanaPrincipal) -> Unit
) {
    NavigationBar {
        PestanaPrincipal.entries.forEach { pestana ->
            val seleccionada = destinoActual?.hierarchy?.any { it.hasRoute(pestana.ruta::class) } == true
            NavigationBarItem(
                selected = seleccionada,
                onClick = { if (!seleccionada) onPestanaClick(pestana) },
                icon = { Icon(pestana.icono, contentDescription = null) },
                label = { Text(stringResource(pestana.etiqueta)) }
            )
        }
    }
}

/**
 * Las pestañas comparten la raíz: se vuelve al inicio del grafo guardando el estado de cada una,
 * así "Atrás" desde cualquier pestaña regresa al Test y luego sale de la app, sin pilas duplicadas.
 */
private fun NavHostController.navegarAPestana(pestana: PestanaPrincipal) {
    navigate(pestana.ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
