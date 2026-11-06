<div align="center">

# CalmaJoven EC
### Asistente Móvil de Bienestar y Apoyo Emocional Juvenil

**Pontificia Universidad Católica del Ecuador (PUCE)**
Facultad de Hábitat, Infraestructura y Creatividad
Carrera de Ingeniería en Sistemas de Información
Asignatura: Programación Móvil

| | |
|---|---|
| **Autores** | Ariel Chacha · Joan Santacruz · Anthony Rosero · Esteban García |
| **Docente** | Ing. Juan Francisco Chafla A., PhD. |
| **Entregable** | Semana 9: ViewModel, ciclo de vida y estado de UI |
| **Versión** | 0.9.0 |
| **Licencia** | [MIT](LICENSE) |

</div>

---

## 1. Problema y público objetivo

En Ecuador, la ansiedad y el estrés en adolescentes y jóvenes crecieron en los últimos años, y buscar ayuda todavía pesa: hay estigma, la atención profesional cuesta y muchos no saben a qué línea llamar. Hay esfuerzos públicos que van en la dirección correcta, como el programa municipal **¿Q+ve?** del Municipio de Quito, que acerca orientación en salud mental y prevención a la juventud. Lo que falta es una herramienta que el joven lleve en el bolsillo, que pueda abrir a cualquier hora y que lo conecte con esos recursos.

**CalmaJoven EC** responde a eso con tres funciones:

1. Un **test breve de autoevaluación** (1 a 10) con un diagnóstico orientativo y una recomendación.
2. **Guías de respiración y relajación** junto con el historial de resultados anteriores.
3. Una **red de apoyo en Ecuador**: botón SOS que abre el marcador hacia el ECU 911 o el Ministerio de Salud Pública (171), y psicólogos voluntarios.

**Público objetivo:** jóvenes de **13 a 25 años** con teléfonos Android de gama de entrada y media. Android tiene el **75.92 %** del mercado móvil del país, por eso la app apunta a `minSdk 24` y evita dependencias pesadas.

> CalmaJoven EC no reemplaza la atención profesional. Los resultados son orientativos.

---

## 2. Stack técnico

| Componente | Versión |
|---|---|
| Android Studio | Ladybug 2024.2 |
| Android Gradle Plugin | 8.7.0 (Gradle 8.10.2) |
| Kotlin | 2.0.21 (plugin de Compose y de Serialization) |
| Java | 21 (`JavaVersion.VERSION_21`, `jvmTarget = "21"`) |
| SDK | `compileSdk 36` · `targetSdk 36` · `minSdk 24` |
| Jetpack Compose BOM | 2024.12.01 (Material 3) |
| Lifecycle (ViewModel + Compose) | 2.8.7 |
| Navigation Compose | 2.8.5 (rutas tipadas) |
| kotlinx-serialization-json | 1.7.3 |

Todas las versiones viven en [`gradle/libs.versions.toml`](gradle/libs.versions.toml). El `build.gradle.kts` raíz solo declara `alias(libs.plugins...) apply false`, sin números de versión.

---

## 3. Arquitectura implementada hasta la Semana 9

```
┌───────────────────────────── UI (Jetpack Compose + Material 3) ──────────────────────────┐
│  MainActivity ─► CalmaJovenTheme ─► CalmaJovenNavGraph (NavHost único + NavController)    │
│                                        ├── TestScreen      (Pestaña Test)                 │
│                                        ├── GuidesScreen    (Pestaña Guías)                │
│                                        └── SupportScreen   (Pestaña Ayuda)                │
│               ▲ estado (StateFlow)                           │ eventos (lambdas)          │
└───────────────┼──────────────────────────────────────────────┼────────────────────────────┘
                │                                              ▼
┌─────────────── CalmaJovenViewModel (SavedStateHandle) ──────────────────────────────────┐
│  formulario: StateFlow<TestFormState>    uiState: StateFlow<DiagnosticoUiState>          │
│  historial / ejercicios / psicologos: StateFlow<List<...>>                               │
└───────────────┬─────────────────────────────────────────────────────────────────────────┘
                ▼
┌─────────────── Datos ───────────────────────────────────────────────────────────────────┐
│  data/model (data class inmutables)   data/source/FuenteDatosLocal (datos en memoria)    │
│  TODO Semana 11-12: Repository + Room (historial) + Retrofit (guías, voluntarios)        │
│  TODO Semana 14-15: ML Kit en el dispositivo                                             │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.1 UI declarativa con Jetpack Compose
No hay layouts XML. Cada pantalla es una función `@Composable` **sin estado propio de negocio**: recibe los datos y expone eventos como lambdas (*state hoisting*). Así se pueden previsualizar con `@Preview` en modo claro y oscuro sin levantar el ViewModel.

### 3.2 Material Design 3 y modo oscuro automático
`CalmaJovenTheme` define dos esquemas propios, claro y oscuro, con verde menta, azul sereno y un tono arena para el estrés moderado. El modo oscuro sigue al sistema (`isSystemInDarkTheme()`). El tema admite **color dinámico** (`dynamicLightColorScheme` / `dynamicDarkColorScheme`, API 31+) con el parámetro `dynamicColor`. Viene desactivado para conservar la identidad visual. El botón SOS usa un **rojo coral semántico fijo** (`LocalColoresSos`) con contraste ≥ 4.5:1 en ambos modos.

### 3.3 Navegación segura: un único `NavHost`
- Un solo `NavHost` con un solo `NavController` en [`NavGraph.kt`](app/src/main/java/ec/edu/puce/calmajoven/ui/navigation/NavGraph.kt).
- **Rutas tipadas** con `@Serializable` (`TestRuta`, `GuiasRuta`, `AyudaRuta`): navegar a un destino que no existe es un error de compilación, no un crash en ejecución.
- `NavigationBar` con tres pestañas. Al cambiar de pestaña se usa `popUpTo(startDestination) { saveState = true }`, `launchSingleTop = true` y `restoreState = true`. Así no se apilan copias de la misma pantalla y "Atrás" vuelve al Test y luego sale de la app.
- `android:enableOnBackInvokedCallback="true"` en el Manifest activa el **gesto Atrás predictivo** de Android 16 (API 36).

### 3.4 MVVM con flujo unidireccional de datos (UDF)
```
Usuario escribe/toca ─► evento (onRespuestaChange, evaluar…) ─► ViewModel
        ▲                                                         │
        └──────── la UI se recompone ◄── StateFlow emite estado ◄─┘
```
- La UI **nunca modifica el estado**. Solo llama funciones del ViewModel.
- El ViewModel expone `StateFlow` de solo lectura (`asStateFlow()`). Los `MutableStateFlow` son privados.
- La UI los recolecta con `collectAsStateWithLifecycle()`, que deja de escuchar cuando la app pasa a segundo plano.

### 3.5 Ciclo de vida: `ViewModel`, `SavedStateHandle` y `rememberSaveable`

| Escenario | Qué lo resuelve |
|---|---|
| **Rotación de pantalla** | El `ViewModel` sobrevive al cambio de configuración: el formulario, el resultado y el historial siguen ahí. |
| **Muerte del proceso** (Android cierra la app en segundo plano) | `SavedStateHandle` guarda el apodo, la edad, la respuesta, la opción rápida y el último resultado. Al volver, el usuario ve la misma pantalla. |
| **Estado puramente visual** | `rememberSaveable` guarda si una tarjeta de ejercicio tiene los pasos desplegados. |

---

## 4. Cómo el modelado de estado previene crashes

### 4.1 `data class EvaluacionEmocional` con `val`
```kotlin
data class EvaluacionEmocional(
    val id: String, val apodo: String, val edad: Int?, val puntaje: Int,
    val diagnostico: String, val recomendaciones: String, val fecha: String
)
```
- **Inmutable (`val`)**: una evaluación no cambia después de crearse. Si se necesita otra versión, se usa `copy()`. Ninguna pantalla puede alterar a medias un objeto que otra está dibujando, y Compose detecta los cambios de forma fiable.
- **`edad: Int?` explícito**: la edad es opcional por privacidad. El tipo anulable obliga al compilador a tratar el caso `null`, así que un `NullPointerException` no pasa desapercibido.
- **Entradas validadas sin excepciones**: el texto se convierte con `toIntOrNull()` y el operador Elvis (`?:`). Si falta la respuesta o está fuera de rango, el ViewModel emite `DiagnosticoUiState.Error` con un aviso amable. Nunca se lanza `NumberFormatException`.
  ```kotlin
  val puntaje = form.respuestaTexto.trim().toIntOrNull()
      ?: return emitirError(CodigosError.NIVEL_REQUERIDO)
  ```
- `diagnostico` y `recomendaciones` guardan **códigos estables** (`DIAG_MODERADO`, `REC_MODERADO`) y `fecha` se guarda en ISO `yyyy-MM-dd`. La UI los traduce con `strings.xml`. El registro sigue siendo válido si cambia el idioma y está listo para guardarse en Room.

### 4.2 `sealed class DiagnosticoUiState`
```kotlin
sealed class DiagnosticoUiState {
    object Idle : DiagnosticoUiState()
    object Loading : DiagnosticoUiState()
    data class Success(val evaluacion: EvaluacionEmocional) : DiagnosticoUiState()
    data class Error(val mensaje: String) : DiagnosticoUiState()
}
```
- La pantalla solo puede estar en **uno** de estos cuatro estados. No existen combinaciones imposibles como "cargando y con error a la vez".
- El `when` de `TestScreen` es **exhaustivo y sin `else`**: si mañana se agrega un estado, el proyecto **no compila** hasta manejarlo. El error aparece en Android Studio y no en el teléfono del usuario.
- `Success` **lleva consigo** la evaluación, así que la tarjeta de resultado no puede dibujarse sin datos.

---

## 5. Estructura de paquetes

```
app/src/main/java/ec/edu/puce/calmajoven/
├── MainActivity.kt                    # enableEdgeToEdge + CalmaJovenTheme + NavGraph
├── data/
│   ├── model/
│   │   ├── EvaluacionEmocional.kt     # + EjercicioRespiracion, PsicologoVoluntario
│   │   └── NivelEstres.kt             # Rangos 1–3 / 4–7 / 8–10 y códigos
│   └── source/
│       └── FuenteDatosLocal.kt        # Datos en memoria (TODO Room / Retrofit)
└── ui/
    ├── components/
    │   ├── Marcador.kt                # Intent.ACTION_DIAL seguro
    │   └── TextosUi.kt                # Códigos → strings.xml, fechas relativas
    ├── navigation/
    │   ├── NavGraph.kt                # NavHost único + NavigationBar
    │   └── Rutas.kt                   # Rutas @Serializable y pestañas
    ├── screens/
    │   ├── TestScreen.kt              # Pantalla 1: Chatbot / Test
    │   ├── GuidesScreen.kt            # Pantalla 2: Guías e historial
    │   └── SupportScreen.kt           # Pantalla 3: Red de apoyo
    ├── state/
    │   ├── DiagnosticoUiState.kt      # Sealed class + códigos de error
    │   └── TestFormState.kt           # Entradas del formulario + OpcionRapida
    ├── theme/                         # Color.kt, Theme.kt, Type.kt
    └── viewmodel/
        └── CalmaJovenViewModel.kt
```
Todos los textos visibles están en [`res/values/strings.xml`](app/src/main/res/values/strings.xml). Las pantallas no tienen cadenas escritas a mano.

---

## 6. Compilar y ejecutar

1. Abrir la carpeta del proyecto en Android Studio Ladybug con el JDK 21 incluido (JBR).
2. Esperar la sincronización de Gradle.
3. Ejecutar en un emulador **API 36** o en un dispositivo con Android 7.0 o superior.

```bash
./gradlew :app:assembleDebug
```

```bash
./gradlew :app:testDebugUnitTest
```

Las pruebas unitarias cubren la clasificación por rangos, la validación sin crash, la restauración tras la muerte del proceso y el formato de fechas.

---

## 7. Guía de capturas para el informe PDF

| # | Captura | Cómo obtenerla |
|---|---|---|
| 1 | **Estructura de paquetes** | Panel *Project* en vista **Android**, con `ec.edu.puce.calmajoven` desplegado (`data`, `ui/*`). |
| 2 | **Catálogo de versiones y Gradle** | `libs.versions.toml` y `app/build.gradle.kts` mostrando `compileSdk 36`, `minSdk 24` y `JavaVersion.VERSION_21`. |
| 3 | **Estado `Idle`** | Abrir la app: pestaña Test con el texto "Cuando respondas, aquí verás tu resultado." |
| 4 | **Estado `Error`** | Tocar **Ver mi resultado** sin responder: aparece la tarjeta "Revisa tus respuestas". También sirve escribir edad `5`. |
| 5 | **Estado `Loading`** | Tocar **Ver mi resultado** con una respuesta válida y capturar el indicador "Revisando tus respuestas…" (dura unos 0.6 s). |
| 6 | **Estado `Success`** | Elegir **Medio**: tarjeta "Estrés Moderado" con la recomendación "Pausas activas y ejercicio de respiración guiada". Repetir con **Alto** para ver el botón hacia la red de apoyo. |
| 7 | **Prueba de rotación (API 36)** | Con apodo, edad y resultado en pantalla, rotar el emulador (`Ctrl + ←/→` o el botón de rotar). Capturar antes y después: los datos se mantienen. |
| 8 | **Prueba de muerte del proceso** | Llenar el formulario, enviar la app a segundo plano y ejecutar `adb shell am kill ec.edu.puce.calmajoven`. Volver desde Recientes: el formulario y el resultado siguen ahí. |
| 9 | **Guías e historial** | Pestaña Guías: `LazyRow` de ejercicios (uno con "Ver pasos" desplegado) y el historial "• Hoy: Estrés Moderado", "• Ayer: Estado de Calma". |
| 10 | **Red de apoyo** | Pestaña Ayuda: botón SOS coral y lista de voluntarios. Tocar SOS y capturar el marcador con `911`. |
| 11 | **Modo oscuro** | *Settings → Display → Dark theme* en el emulador y repetir las capturas 3, 9 y 10. |
| 12 | **Atrás predictivo** | Con la navegación por gestos activa, deslizar desde el borde en Guías y capturar la vista previa de regreso al Test. |

---

## 8. Hoja de ruta (Semanas 11–15)

| Semana | Entregable | Punto de integración ya preparado |
|---|---|---|
| 11–12 | **Room** para el historial y **Retrofit** para guías y voluntarios, detrás de un `Repository` | Comentarios `TODO Semana 11-12` en `CalmaJovenViewModel` y `FuenteDatosLocal`. Los modelos ya son inmutables y usan códigos y fechas ISO aptos para persistir. |
| 13 | Chat con voluntarios | Botón "Ir al Chat" en `SupportScreen`. |
| 14–15 | **ML Kit** en el dispositivo para analizar texto libre sin enviar datos al servidor | `TODO Semana 14-15` en `FuenteDatosLocal`. |

---

## 9. Privacidad

- La app no pide permisos: `ACTION_DIAL` solo abre el marcador, el usuario decide si llama.
- `android:allowBackup="false"` y reglas de respaldo que excluyen todos los datos, para que la información emocional no salga del dispositivo.
- El apodo y la edad son opcionales.

---

## Licencia

Distribuido bajo licencia MIT. Ver [LICENSE](LICENSE).
© 2026 Ariel Chacha, Joan Santacruz, Anthony Rosero y Esteban García, PUCE.
