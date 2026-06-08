package com.motocontable.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motocontable.app.viewmodel.ConfiguracionViewModel
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════
@Composable
fun PantallaConfiguracion(
    viewModel: ConfiguracionViewModel = hiltViewModel(),
) {
    val configActual by viewModel.configuracion.collectAsState()

    // Estado local editable
    var nombre1    by remember { mutableStateOf(configActual.nombreAlumno1) }
    var nombre2    by remember { mutableStateOf(configActual.nombreAlumno2) }
    var nombre3    by remember { mutableStateOf(configActual.nombreAlumno3) }
    var nombre4    by remember { mutableStateOf(configActual.nombreAlumno4) }
    var nombreProf by remember { mutableStateOf(configActual.nombreProfesor) }
    var precioA    by remember { mutableStateOf(configActual.precioAlumnoViaje.toString()) }
    var precioP    by remember { mutableStateOf(configActual.precioProfesorViaje.toString()) }

    var guardadoOk by remember { mutableStateOf(false) }
    var errPrecioA by remember { mutableStateOf(false) }
    var errPrecioP by remember { mutableStateOf(false) }

    // Sincronizar cuando carga desde DB
    LaunchedEffect(configActual) {
        nombre1    = configActual.nombreAlumno1
        nombre2    = configActual.nombreAlumno2
        nombre3    = configActual.nombreAlumno3
        nombre4    = configActual.nombreAlumno4
        nombreProf = configActual.nombreProfesor
        precioA    = configActual.precioAlumnoViaje.toString()
        precioP    = configActual.precioProfesorViaje.toString()
    }

    // Ocultar el mensaje de exito tras 2 segundos
    if (guardadoOk) {
        LaunchedEffect(guardadoOk) {
            delay(2_000)
            guardadoOk = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {

        Text(
            "Configuracion",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary,
        )

        // ── Nombres ────────────────────────────────────────────────
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                EtiquetaSeccion(texto = "NOMBRES")

                CampoNombre(
                    valor    = nombre1,
                    etiqueta = "Alumno 1",
                    onChange = { nombre1 = it },
                )
                CampoNombre(
                    valor    = nombre2,
                    etiqueta = "Alumno 2",
                    onChange = { nombre2 = it },
                )
                CampoNombre(
                    valor    = nombre3,
                    etiqueta = "Alumno 3",
                    onChange = { nombre3 = it },
                )
                CampoNombre(
                    valor    = nombre4,
                    etiqueta = "Alumno 4",
                    onChange = { nombre4 = it },
                )
                CampoNombre(
                    valor    = nombreProf,
                    etiqueta = "Profesor",
                    onChange = { nombreProf = it },
                )
            }
        }

        // ── Precios ────────────────────────────────────────────────
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                EtiquetaSeccion(texto = "PRECIOS POR TRAMO (IDA O VUELTA)")

                OutlinedTextField(
                    value         = precioA,
                    onValueChange = { precioA = it; errPrecioA = false },
                    label         = { Text("Precio alumno") },
                    prefix        = { Text("S/ ") },
                    supportingText = {
                        if (errPrecioA) Text("Monto invalido")
                        else Text("Ida: S/$precioA · Vuelta: S/$precioA · Max dia: S/${
                            (precioA.toDoubleOrNull()?.times(2) ?: 0.0).let {
                                "%.2f".format(it)
                            }
                        }")
                    },
                    isError        = errPrecioA,
                    modifier       = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine     = true,
                )

                OutlinedTextField(
                    value         = precioP,
                    onValueChange = { precioP = it; errPrecioP = false },
                    label         = { Text("Precio profesor") },
                    prefix        = { Text("S/ ") },
                    supportingText = {
                        if (errPrecioP) Text("Monto invalido")
                        else Text("Ida: S/$precioP · Vuelta: S/$precioP · Max dia: S/${
                            (precioP.toDoubleOrNull()?.times(2) ?: 0.0).let {
                                "%.2f".format(it)
                            }
                        }")
                    },
                    isError        = errPrecioP,
                    modifier       = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine     = true,
                )
            }
        }

        // ── Boton guardar + confirmacion ───────────────────────────
        Button(
            onClick = {
                val pa = precioA.trim().replace(",", ".").toDoubleOrNull()
                val pp = precioP.trim().replace(",", ".").toDoubleOrNull()
                errPrecioA = (pa == null || pa <= 0.0)
                errPrecioP = (pp == null || pp <= 0.0)
                if (!errPrecioA && !errPrecioP) {
                    viewModel.guardarNombres(nombre1, nombre2, nombre3, nombre4, nombreProf)
                    viewModel.guardarPrecios(pa!!, pp!!)
                    guardadoOk = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar cambios", fontSize = 16.sp)
        }

        AnimatedVisibility(
            visible = guardadoOk,
            enter   = fadeIn(),
            exit    = fadeOut(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Cambios guardados correctamente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════
// Componentes reutilizables
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun EtiquetaSeccion(texto: String) {
    Text(
        texto,
        style      = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun CampoNombre(
    valor: String,
    etiqueta: String,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value         = valor,
        onValueChange = onChange,
        label         = { Text(etiqueta) },
        leadingIcon   = { Icon(Icons.Default.Person, null, Modifier.size(18.dp)) },
        modifier      = Modifier.fillMaxWidth(),
        singleLine    = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
        ),
    )
}
