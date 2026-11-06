package ec.edu.puce.calmajoven.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ec.edu.puce.calmajoven.R
import ec.edu.puce.calmajoven.data.model.PsicologoVoluntario
import ec.edu.puce.calmajoven.data.source.FuenteDatosLocal
import ec.edu.puce.calmajoven.ui.components.abrirMarcador
import ec.edu.puce.calmajoven.ui.theme.CalmaJovenTheme
import ec.edu.puce.calmajoven.ui.theme.LocalColoresSos
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    psicologos: List<PsicologoVoluntario>,
    bottomBar: @Composable () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val avisar: (String) -> Unit = { mensaje ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(mensaje)
        }
    }
    val sinMarcador911 = stringResource(R.string.error_sin_marcador, FuenteDatosLocal.TELEFONO_ECU911)
    val sinMarcador171 = stringResource(R.string.error_sin_marcador, FuenteDatosLocal.TELEFONO_MSP)

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.titulo_ayuda)) }) },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonSos(
                onClick = {
                    if (!abrirMarcador(context, FuenteDatosLocal.TELEFONO_ECU911)) avisar(sinMarcador911)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = stringResource(R.string.sos_descripcion),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            OutlinedButton(
                onClick = {
                    if (!abrirMarcador(context, FuenteDatosLocal.TELEFONO_MSP)) avisar(sinMarcador171)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.msp_boton))
            }

            Text(
                text = stringResource(R.string.titulo_psicologos),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                    .semantics { heading() }
            )
            if (psicologos.isEmpty()) {
                Text(
                    text = stringResource(R.string.psicologos_vacio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = psicologos, key = { it.id }) { psicologo ->
                        TarjetaPsicologo(
                            psicologo = psicologo,
                            onIrAlChat = {
                                avisar(context.getString(R.string.chat_proximamente, psicologo.nombre))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BotonSos(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colores = LocalColoresSos.current
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colores.contenedor,
            contentColor = colores.contenido
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Phone,
            contentDescription = stringResource(R.string.cd_telefono),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(stringResource(R.string.sos_boton), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun TarjetaPsicologo(psicologo: PsicologoVoluntario, onIrAlChat: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = stringResource(R.string.cd_psicologo),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(psicologo.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = psicologo.especialidad,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(psicologo.horario, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.width(8.dp))
            FilledTonalButton(onClick = onIrAlChat, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.boton_chat))
            }
        }
    }
}

@Preview(name = "Ayuda · claro", showBackground = true)
@Preview(name = "Ayuda · oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SupportScreenPreview() {
    CalmaJovenTheme {
        SupportScreen(psicologos = FuenteDatosLocal.psicologos, bottomBar = {})
    }
}
