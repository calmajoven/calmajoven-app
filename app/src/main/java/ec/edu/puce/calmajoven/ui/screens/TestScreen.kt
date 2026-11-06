package ec.edu.puce.calmajoven.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ec.edu.puce.calmajoven.R
import ec.edu.puce.calmajoven.data.model.EvaluacionEmocional
import ec.edu.puce.calmajoven.data.model.NivelEstres
import ec.edu.puce.calmajoven.ui.components.etiquetaRes
import ec.edu.puce.calmajoven.ui.components.textoDiagnostico
import ec.edu.puce.calmajoven.ui.components.textoError
import ec.edu.puce.calmajoven.ui.components.textoRecomendacion
import ec.edu.puce.calmajoven.ui.state.DiagnosticoUiState
import ec.edu.puce.calmajoven.ui.state.OpcionRapida
import ec.edu.puce.calmajoven.ui.state.TestFormState
import ec.edu.puce.calmajoven.ui.theme.CalmaJovenTheme
import ec.edu.puce.calmajoven.ui.viewmodel.CalmaJovenViewModel

private val EaseOut = CubicBezierEasing(0.23f, 1f, 0.32f, 1f)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TestScreen(
    formulario: TestFormState,
    uiState: DiagnosticoUiState,
    onApodoChange: (String) -> Unit,
    onEdadChange: (String) -> Unit,
    onRespuestaChange: (String) -> Unit,
    onOpcionRapida: (OpcionRapida) -> Unit,
    onEvaluar: () -> Unit,
    onNuevoTest: () -> Unit,
    onIrAAyuda: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val resultadoRequester = remember { BringIntoViewRequester() }
    val evaluar = {
        focusManager.clearFocus()
        onEvaluar()
    }

    LaunchedEffect(uiState) {
        if (uiState !is DiagnosticoUiState.Idle) resultadoRequester.bringIntoView()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.titulo_test)) }) },
        bottomBar = bottomBar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BurbujaAsistente()

            OutlinedTextField(
                value = formulario.apodo,
                onValueChange = onApodoChange,
                label = { Text(stringResource(R.string.campo_apodo)) },
                supportingText = { Text(stringResource(R.string.campo_apodo_ayuda)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = formulario.edadTexto,
                onValueChange = onEdadChange,
                label = { Text(stringResource(R.string.campo_edad)) },
                supportingText = { Text(stringResource(R.string.campo_edad_ayuda)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = formulario.respuestaTexto,
                onValueChange = onRespuestaChange,
                label = { Text(stringResource(R.string.campo_respuesta)) },
                supportingText = { Text(stringResource(R.string.campo_respuesta_ayuda)) },
                isError = uiState is DiagnosticoUiState.Error,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { evaluar() }),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.opciones_rapidas),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BotonesPuntajeRapido(
                seleccionada = formulario.opcionRapida,
                onOpcionRapida = onOpcionRapida
            )

            Button(
                onClick = evaluar,
                enabled = uiState !is DiagnosticoUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Text(stringResource(R.string.boton_evaluar))
            }

            ResultadoDiagnostico(
                uiState = uiState,
                onNuevoTest = onNuevoTest,
                onIrAAyuda = onIrAAyuda,
                modifier = Modifier.bringIntoViewRequester(resultadoRequester)
            )
        }
    }
}

@Composable
private fun BurbujaAsistente() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Filled.Face,
                contentDescription = stringResource(R.string.cd_asistente),
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.asistente_nombre),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(R.string.asistente_saludo),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.pregunta_estres),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
            }
        }
    }
}

@Composable
private fun BotonesPuntajeRapido(
    seleccionada: OpcionRapida?,
    onOpcionRapida: (OpcionRapida) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup()
    ) {
        OpcionRapida.entries.forEach { opcion ->
            val activa = opcion == seleccionada
            val modificador = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .semantics {
                    selected = activa
                    role = Role.RadioButton
                }
            val contenido: @Composable () -> Unit = {
                if (activa) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                }
                Text(stringResource(opcion.etiquetaRes()), maxLines = 1)
            }
            if (activa) {
                Button(
                    onClick = { onOpcionRapida(opcion) },
                    modifier = modificador,
                    contentPadding = ButtonDefaults.TextButtonContentPadding
                ) { contenido() }
            } else {
                OutlinedButton(
                    onClick = { onOpcionRapida(opcion) },
                    modifier = modificador,
                    contentPadding = ButtonDefaults.TextButtonContentPadding
                ) { contenido() }
            }
        }
    }
}

@Composable
private fun ResultadoDiagnostico(
    uiState: DiagnosticoUiState,
    onNuevoTest: () -> Unit,
    onIrAAyuda: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = uiState,
        contentKey = { it::class },
        transitionSpec = {
            fadeIn(tween(220, easing = EaseOut)) togetherWith fadeOut(tween(150, easing = EaseOut))
        },
        label = "resultado",
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite }
    ) { estado ->
        when (estado) {
            DiagnosticoUiState.Idle -> Text(
                text = stringResource(R.string.estado_idle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DiagnosticoUiState.Loading -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                Text(stringResource(R.string.estado_loading), style = MaterialTheme.typography.bodyLarge)
            }

            is DiagnosticoUiState.Success -> TarjetaResultado(
                evaluacion = estado.evaluacion,
                onNuevoTest = onNuevoTest,
                onIrAAyuda = onIrAAyuda
            )

            is DiagnosticoUiState.Error -> TarjetaError(mensaje = textoError(estado.mensaje))
        }
    }
}

@Composable
private fun TarjetaResultado(
    evaluacion: EvaluacionEmocional,
    onNuevoTest: () -> Unit,
    onIrAAyuda: () -> Unit
) {
    val nivel = NivelEstres.desdeCodigo(evaluacion.diagnostico)
        ?: NivelEstres.desdePuntaje(evaluacion.puntaje)
    val esquema = MaterialTheme.colorScheme
    val (fondo, texto) = when (nivel) {
        NivelEstres.CALMA -> esquema.primaryContainer to esquema.onPrimaryContainer
        NivelEstres.MODERADO -> esquema.tertiaryContainer to esquema.onTertiaryContainer
        NivelEstres.ALTO -> esquema.errorContainer to esquema.onErrorContainer
    }
    val saludo = if (evaluacion.apodo.isBlank()) {
        stringResource(R.string.resultado_saludo_anonimo)
    } else {
        stringResource(R.string.resultado_saludo, evaluacion.apodo)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = fondo, contentColor = texto),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource(R.string.resultado_titulo), style = MaterialTheme.typography.labelLarge)
            Text(saludo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = textoDiagnostico(evaluacion.diagnostico),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = stringResource(R.string.resultado_puntaje, evaluacion.puntaje),
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = { evaluacion.puntaje / NivelEstres.PUNTAJE_VALIDO.last.toFloat() },
                color = texto,
                trackColor = texto.copy(alpha = 0.18f),
                drawStopIndicator = {},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(4.dp))
            Text(stringResource(R.string.resultado_recomendacion), style = MaterialTheme.typography.labelLarge)
            Text(textoRecomendacion(evaluacion.recomendaciones), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(R.string.resultado_aviso),
                style = MaterialTheme.typography.bodyMedium,
                color = texto.copy(alpha = 0.85f)
            )
            if (nivel == NivelEstres.ALTO) {
                Button(
                    onClick = onIrAAyuda,
                    colors = ButtonDefaults.buttonColors(containerColor = texto, contentColor = fondo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                ) { Text(stringResource(R.string.boton_ir_ayuda)) }
            }
            TextButton(
                onClick = onNuevoTest,
                colors = ButtonDefaults.textButtonColors(contentColor = texto),
                modifier = Modifier.heightIn(min = 48.dp)
            ) { Text(stringResource(R.string.boton_nuevo_test)) }
        }
    }
}

@Composable
private fun TarjetaError(mensaje: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.Warning, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.error_titulo), style = MaterialTheme.typography.titleMedium)
                Text(mensaje, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(name = "Test · claro", showBackground = true)
@Preview(name = "Test · oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TestScreenPreview() {
    CalmaJovenTheme {
        TestScreen(
            formulario = TestFormState(apodo = "Sol", respuestaTexto = "5", opcionRapida = OpcionRapida.MEDIO),
            uiState = DiagnosticoUiState.Success(
                CalmaJovenViewModel.construirEvaluacion("demo", "Sol", 17, 5, "2026-11-05")
            ),
            onApodoChange = {},
            onEdadChange = {},
            onRespuestaChange = {},
            onOpcionRapida = {},
            onEvaluar = {},
            onNuevoTest = {},
            onIrAAyuda = {},
            bottomBar = {}
        )
    }
}
