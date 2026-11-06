package ec.edu.puce.calmajoven.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Abre el marcador con el número escrito; no llama por sí solo, así que no requiere CALL_PHONE.
 * Devuelve false en dispositivos sin app de teléfono (algunas tablets) para avisar al usuario.
 */
fun abrirMarcador(context: Context, numero: String): Boolean {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero"))
    return try {
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
}
