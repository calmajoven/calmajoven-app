package ec.edu.puce.calmajoven.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ec.edu.puce.calmajoven.R
import ec.edu.puce.calmajoven.data.model.EjercicioRespiracion
import ec.edu.puce.calmajoven.data.model.EvaluacionEmocional
import ec.edu.puce.calmajoven.data.source.FuenteDatosLocal
import ec.edu.puce.calmajoven.ui.components.textoDiagnostico
import ec.edu.puce.calmajoven.ui.components.textoFecha
import ec.edu.puce.calmajoven.ui.theme.CalmaJovenTheme

private val EaseOut = CubicBezierEasing(0.23f, 1f, 0.32f, 1f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreen(
    ejercicios: List<EjercicioRespiracion>,
    historial: List<EvaluacionEmocional>,
    bottomBar: @Composable () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.titulo_guias)) }) },
        bottomBar = bottomBar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TituloSeccion(stringResource(R.string.titulo_ejercicios))
            if (ejercicios.isEmpty()) {
                TextoVacio(stringResource(R.string.ejercicios_vacio))
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = ejercicios, key = { it.id }) { ejercicio ->
                        TarjetaEjercicio(ejercicio)
                    }
                }
            }

            TituloSeccion(stringResource(R.string.titulo_historial))
            if (historial.isEmpty()) {
                TextoVacio(stringResource(R.string.historial_vacio))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = historial, key = { it.id }) { evaluacion ->
                        ItemHistorial(evaluacion)
                    }
                }
            }
        }
    }
}

@Composable
private fun TituloSeccion(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .semantics { heading() }
    )
}

@Composable
private fun TextoVacio(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun TarjetaEjercicio(ejercicio: EjercicioRespiracion) {
    // Divulgación progresiva: los pasos se muestran solo si el usuario los pide.
    var expandida by rememberSaveable(ejercicio.id) { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.width(260.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(ejercicio.titulo, style = MaterialTheme.typography.titleMedium)
            Text(ejercicio.descripcion, style = MaterialTheme.typography.bodyMedium)
            AnimatedVisibility(
                visible = expandida,
                enter = expandVertically(tween(220, easing = EaseOut)) + fadeIn(tween(220, easing = EaseOut)),
                exit = shrinkVertically(tween(180, easing = EaseOut)) + fadeOut(tween(150, easing = EaseOut))
            ) {
                Text(
                    text = ejercicio.pasos,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            TextButton(
                onClick = { expandida = !expandida },
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(
                    stringResource(
                        if (expandida) R.string.ejercicio_ocultar_pasos else R.string.ejercicio_ver_pasos
                    )
                )
                Icon(
                    imageVector = if (expandida) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ItemHistorial(evaluacion: EvaluacionEmocional) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(
                    R.string.historial_item,
                    textoFecha(evaluacion.fecha),
                    textoDiagnostico(evaluacion.diagnostico)
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.historial_puntaje, evaluacion.puntaje),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Guías · claro", showBackground = true)
@Preview(name = "Guías · oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GuidesScreenPreview() {
    CalmaJovenTheme {
        GuidesScreen(
            ejercicios = FuenteDatosLocal.ejercicios,
            historial = FuenteDatosLocal.historialPrueba(),
            bottomBar = {}
        )
    }
}
