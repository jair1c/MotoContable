package com.motocontable.app.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.entity.Extra
import com.motocontable.app.data.entity.RegistroDiario
import com.motocontable.app.util.FechaUtil
import com.motocontable.app.util.formatoSoles
import com.motocontable.app.viewmodel.RegistroViewModel

// ═══════════════════════════════════════════════════════════════════
// Pantalla principal
// ═══════════════════════════════════════════════════════════════════
@Composable
fun PantallaHoy(
    viewModel: RegistroViewModel = hiltViewModel(),
) {
    val registro       by viewModel.registro.collectAsState()
    val extras         by viewModel.extras.collectAsState()
    val config         by viewModel.configuracion.collectAsState()
    val totalHoy       by viewModel.totalHoy.collectAsState()
    val fecha          by viewModel.fecha.collectAsState()
    val guardado       by viewModel.guardado.collectAsState()

    var mostrarDialog  by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        // ── Encabezado con fecha ───────────────────────────────────
        EncabezadoFecha(
            fecha    = fecha,
            guardado = guardado,
            onAnterior  = { viewModel.setFecha(FechaUtil.diaLaboralAnterior(fecha)) },
            onSiguiente = {
                if (!FechaUtil.esHoy(fecha) && !FechaUtil.esFuturo(
                        FechaUtil.diaLaboralSiguiente(fecha))) {
                    viewModel.setFecha(FechaUtil.diaLaboralSiguiente(fecha))
                } else if (!FechaUtil.esHoy(fecha)) {
                    viewModel.irAHoy()
                }
            },
            puedeIrSiguiente = !FechaUtil.esHoy(fecha),
            onIrAHoy    = { viewModel.irAHoy() },
        )

        // ── Card: Alumnos ──────────────────────────────────────────
        CardPersonas(
            titulo  = "ALUMNOS",
            personas = (0..3).map { idx ->
                PersonaUI(
                    nombre   = config.nombresAlumnos()[idx],
                    ida      = registro.alumnoIda(idx),
                    vuelta   = registro.alumnoVuelta(idx),
                    precio   = config.precioAlumnoViaje,
                    onTogIda = { viewModel.toggleViajeYGuardar(idx, true) },
                    onTogVuelta = { viewModel.toggleViajeYGuardar(idx, false) },
                )
            },
        )

        // ── Card: Profesor ─────────────────────────────────────────
        CardPersonas(
            titulo  = "PROFESOR",
            personas = listOf(
                PersonaUI(
                    nombre   = config.nombreProfesor,
                    ida      = registro.profesorIda,
                    vuelta   = registro.profesorVuelta,
                    precio   = config.precioProfesorViaje,
                    onTogIda = { viewModel.toggleViajeYGuardar(4, true) },
                    onTogVuelta = { viewModel.toggleViajeYGuardar(4, false) },
                )
            ),
        )

        // ── Card: Extras ───────────────────────────────────────────
        CardExtras(
            extras      = extras,
            onEliminar  = { viewModel.eliminarExtra(it) },
            onAgregar   = { mostrarDialog = true },
        )

        // ── Total del día ──────────────────────────────────────────
        TotalDia(total = totalHoy)

        Spacer(Modifier.height(8.dp))
    }

    // ── Dialog agregar extra ───────────────────────────────────────
    if (mostrarDialog) {
        DialogAgregarExtra(
            onConfirmar = { desc, monto ->
                viewModel.agregarExtra(desc, monto)
                mostrarDialog = false
            },
            onDismiss = { mostrarDialog = false },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// Encabezado con navegación de fechas
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun EncabezadoFecha(
    fecha: String,
    guardado: Boolean,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    puedeIrSiguiente: Boolean,
    onIrAHoy: () -> Unit,
) {
    val esHoy = FechaUtil.esHoy(fecha)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onAnterior) {
                Icon(Icons.Default.ChevronLeft, "Dia anterior",
                    tint = MaterialTheme.colorScheme.primary)
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text  = if (esHoy) "HOY" else FechaUtil.nombreDia(fecha),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (esHoy) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary,
                    )
                    if (guardado) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Guardado",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
                Text(
                    text  = FechaUtil.formatoCorto(fecha),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            IconButton(
                onClick  = onSiguiente,
                enabled  = puedeIrSiguiente,
            ) {
                Icon(
                    Icons.Default.ChevronRight, "Dia siguiente",
                    tint = if (puedeIrSiguiente) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                )
            }
        }

        if (!esHoy) {
            TextButton(onClick = onIrAHoy) {
                Icon(Icons.Default.Today, null, Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Volver a hoy", fontSize = 12.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Card genérica para lista de personas
// ═══════════════════════════════════════════════════════════════════
private data class PersonaUI(
    val nombre: String,
    val ida: Boolean,
    val vuelta: Boolean,
    val precio: Double,
    val onTogIda: () -> Unit,
    val onTogVuelta: () -> Unit,
) {
    val subtotal get() = (if (ida) precio else 0.0) + (if (vuelta) precio else 0.0)
}

@Composable
private fun CardPersonas(titulo: String, personas: List<PersonaUI>) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))

            personas.forEachIndexed { i, p ->
                FilaPersona(persona = p)
                if (i < personas.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaPersona(persona: PersonaUI) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Fila 1: nombre + subtotal
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text  = persona.nombre,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text  = persona.subtotal.formatoSoles(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (persona.subtotal > 0) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
            )
        }
        // Fila 2: chips ida / vuelta
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipViaje(
                label     = "Ida",
                seleccionado = persona.ida,
                onClick   = persona.onTogIda,
            )
            ChipViaje(
                label     = "Vuelta",
                seleccionado = persona.vuelta,
                onClick   = persona.onTogVuelta,
            )
        }
    }
}

@Composable
private fun ChipViaje(label: String, seleccionado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado,
        onClick  = onClick,
        label    = { Text(label, fontSize = 12.sp) },
        leadingIcon = if (seleccionado) {
            { Icon(Icons.Default.Check, null, Modifier.size(14.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor    = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor        = MaterialTheme.colorScheme.secondary,
            selectedLeadingIconColor  = MaterialTheme.colorScheme.secondary,
        ),
    )
}

// ═══════════════════════════════════════════════════════════════════
// Card de Extras
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun CardExtras(
    extras: List<Extra>,
    onEliminar: (Extra) -> Unit,
    onAgregar: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "VIAJES EXTRA",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(2.dp))

            if (extras.isEmpty()) {
                Text(
                    "Sin viajes extra hoy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                )
            } else {
                extras.forEach { extra ->
                    FilaExtra(extra = extra, onEliminar = { onEliminar(extra) })
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    )
                }
            }

            OutlinedButton(
                onClick  = onAgregar,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Agregar viaje extra")
            }
        }
    }
}

@Composable
private fun FilaExtra(extra: Extra, onEliminar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.DirectionsBike,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text     = extra.descripcion,
            modifier = Modifier.weight(1f),
            style    = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text       = extra.monto.formatoSoles(),
            style      = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.secondary,
        )
        IconButton(
            onClick  = onEliminar,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Eliminar",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Total del día
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun TotalDia(total: Double) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Total del día",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    "Auto-guardado al marcar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                )
            }
            Text(
                text       = total.formatoSoles(),
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Dialog: Agregar viaje extra
// ═══════════════════════════════════════════════════════════════════
@Composable
fun DialogAgregarExtra(
    onConfirmar: (descripcion: String, monto: Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var descripcion by remember { mutableStateOf("") }
    var montoStr    by remember { mutableStateOf("") }
    var errorMonto  by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Agregar viaje extra", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value            = descripcion,
                    onValueChange    = { descripcion = it },
                    label            = { Text("Descripcion") },
                    placeholder      = { Text("ej. Lleve a dona Rosa al mercado") },
                    modifier         = Modifier.fillMaxWidth(),
                    maxLines         = 2,
                )
                OutlinedTextField(
                    value         = montoStr,
                    onValueChange = { montoStr = it; errorMonto = false },
                    label         = { Text("Monto") },
                    prefix        = { Text("S/ ") },
                    placeholder   = { Text("0.00") },
                    modifier      = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError       = errorMonto,
                    supportingText = if (errorMonto) {
                        { Text("Ingresa un monto mayor a 0") }
                    } else null,
                    singleLine    = true,
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val monto = montoStr.trim().replace(",", ".").toDoubleOrNull()
                if (monto == null || monto <= 0.0) {
                    errorMonto = true
                } else {
                    onConfirmar(descripcion.trim().ifBlank { "Extra" }, monto)
                }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}
