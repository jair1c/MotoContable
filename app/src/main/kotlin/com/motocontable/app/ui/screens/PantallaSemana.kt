package com.motocontable.app.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.entity.Extra
import com.motocontable.app.data.entity.GastoSemana
import com.motocontable.app.data.entity.PagoSemana
import com.motocontable.app.data.entity.RegistroDiario
import com.motocontable.app.util.FechaUtil
import com.motocontable.app.util.formatoSoles
import com.motocontable.app.viewmodel.SemanaViewModel

// ═══════════════════════════════════════════════════════════════════
// Pantalla principal
// ═══════════════════════════════════════════════════════════════════
@Composable
fun PantallaSemana(
    viewModel: SemanaViewModel = hiltViewModel(),
) {
    val diasResumen  by viewModel.diasResumen.collectAsState()
    val totalSemana  by viewModel.totalSemana.collectAsState()
    val tituloSemana by viewModel.tituloSemana.collectAsState()
    val offset       by viewModel.offsetSemana.collectAsState()
    val config       by viewModel.configuracion.collectAsState()
    val pagosSemana  by viewModel.pagosSemana.collectAsState()
    val gastoSemana  by viewModel.gastoSemana.collectAsState()
    val resumenCobro by viewModel.resumenCobro.collectAsState()
    val gananciaNeta by viewModel.gananciaNeta.collectAsState()

    val diasConRegistro = diasResumen.count { it.registro != null }
    val esActual = offset == 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Encabezado con navegacion ──────────────────────────────
        EncabezadoSemana(
            titulo       = tituloSemana,
            esActual     = esActual,
            onAnterior   = { viewModel.semanaAnterior() },
            onSiguiente  = { viewModel.semanaSiguiente() },
            onIrActual   = { viewModel.irASemanaActual() },
        )

        // ── Tarjetas por dia ───────────────────────────────────────
        diasResumen.forEach { dia ->
            CardDia(
                dia = dia,
                config = config,
                viewModel = viewModel,
            )
        }

        // ── Total semanal ──────────────────────────────────────────
        if (diasResumen.isNotEmpty()) {
            TotalSemana(
                total            = totalSemana,
                diasConRegistro  = diasConRegistro,
            )
        }

        // ── Seccion de Pagos por Persona ───────────────────────────
        if (diasResumen.isNotEmpty()) {
            CardPagosPorPersona(
                pagosSemana = pagosSemana,
                totalGenerado = resumenCobro.totalGenerado,
                config = config,
                viewModel = viewModel,
                esActual = esActual,
            )
        }

        // ── Card de Gastos (Gasolina) ──────────────────────────────
        if (diasResumen.isNotEmpty()) {
            CardGastos(
                gastoSemana = gastoSemana,
                viewModel = viewModel,
                esActual = esActual,
            )
        }

        // ── Card de Balance Neto ───────────────────────────────────
        if (diasResumen.isNotEmpty()) {
            CardBalance(
                resumenCobro = resumenCobro,
                gananciaNeta = gananciaNeta,
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════
// Encabezado de semana con navegacion
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun EncabezadoSemana(
    titulo: String,
    esActual: Boolean,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    onIrActual: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onAnterior) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Semana anterior",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text       = titulo,
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text  = if (esActual) "Esta semana" else "Semana pasada",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            }
            IconButton(
                onClick  = onSiguiente,
                enabled  = !esActual,
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Semana siguiente",
                    tint = if (esActual)
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.primary,
                )
            }
        }

        if (!esActual) {
            TextButton(onClick = onIrActual) {
                Icon(Icons.Default.Today, null, Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Ir a esta semana", fontSize = 12.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Tarjeta de un dia
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun CardDia(
    dia: SemanaViewModel.DiaResumen,
    config: Configuracion,
    viewModel: SemanaViewModel,
) {
    val esHoy      = FechaUtil.esHoy(dia.fecha)
    val tieneDatos = dia.registro != null

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.elevatedCardColors(
            containerColor = if (esHoy)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {

            // ── Fila: dia + total ──────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text       = dia.nombreDia,
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        if (esHoy) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                Text(
                                    "HOY",
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    Text(
                        text  = FechaUtil.formatoCorto(dia.fecha),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    )
                }

                // Total del dia
                if (dia.total > 0.0) {
                    Text(
                        text       = dia.total.formatoSoles(),
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            // ── Sin datos ──────────────────────────────────────────
            if (!tieneDatos) {
                Text(
                    "Sin registro",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                )
                return@Column
            }

            val reg = dia.registro!!

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            )

            // ── Alumnos (2 por fila) ───────────────────────────────
            (0..3).chunked(2).forEach { par ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    par.forEach { idx ->
                        FilaPersonaSemana(
                            nombre  = config.nombresAlumnos()[idx],
                            ida     = reg.alumnoIda(idx),
                            vuelta  = reg.alumnoVuelta(idx),
                            precio  = config.precioAlumnoViaje,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    // Si el par tiene solo 1 elemento, rellenar espacio
                    if (par.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            // ── Profesor ───────────────────────────────────────────
            FilaPersonaSemana(
                nombre   = config.nombreProfesor,
                ida      = reg.profesorIda,
                vuelta   = reg.profesorVuelta,
                precio   = config.precioProfesorViaje,
                modifier = Modifier.fillMaxWidth(0.5f),
            )

            // ── Extras ─────────────────────────────────────────────
            if (dia.extras.isNotEmpty()) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                )
                dia.extras.forEach { extra ->
                    FilaExtraSemana(
                        extra = extra,
                        onTogglePagado = { pagado ->
                            viewModel.actualizarExtraPagado(extra, pagado)
                        }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Fila de persona en resumen semanal
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun FilaPersonaSemana(
    nombre: String,
    ida: Boolean,
    vuelta: Boolean,
    precio: Double,
    modifier: Modifier = Modifier,
) {
    val subtotal = (if (ida) precio else 0.0) + (if (vuelta) precio else 0.0)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text     = nombre,
            modifier = Modifier.weight(1f),
            style    = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color    = if (!ida && !vuelta)
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.onBackground,
        )
        IndicadorViaje(activo = ida,    label = "I")
        IndicadorViaje(activo = vuelta, label = "V")
    }
}

@Composable
private fun IndicadorViaje(activo: Boolean, label: String) {
    Surface(
        color = if (activo) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(3.dp),
    ) {
        Text(
            text       = label,
            modifier   = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
            fontSize   = 10.sp,
            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
            color      = if (activo) MaterialTheme.colorScheme.secondary
                         else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════
// Fila de extra en resumen semanal
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun FilaExtraSemana(
    extra: Extra,
    onTogglePagado: (Boolean) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            Icons.Default.DirectionsBike,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
        )
        Text(
            text     = extra.descripcion,
            modifier = Modifier.weight(1f),
            style    = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
        Text(
            text       = extra.monto.formatoSoles(),
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.secondary,
        )
        // Toggle de pagado/pendiente
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .clickable { onTogglePagado(!extra.pagado) },
            color = if (extra.pagado)
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
            else
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
        ) {
            Text(
                text       = if (extra.pagado) "✓" else "✗",
                modifier   = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                fontSize   = 10.sp,
                fontWeight = FontWeight.Bold,
                color      = if (extra.pagado)
                    MaterialTheme.colorScheme.onSecondary
                else
                    MaterialTheme.colorScheme.error,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Total de la semana
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun TotalSemana(total: Double, diasConRegistro: Int) {
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
                    "Total de la semana",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    "$diasConRegistro de 5 dias registrados",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.55f),
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
// Card de Pagos por Persona
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun CardPagosPorPersona(
    pagosSemana: List<PagoSemana>,
    totalGenerado: Double,
    config: Configuracion,
    viewModel: SemanaViewModel,
    esActual: Boolean,
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var personaSeleccionada by remember { mutableStateOf(0) }
    var montoPago by remember { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                "Pagos por Persona",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            // Filas de personas (alumnos + profesor)
            val personas = config.nombresAlumnos() + listOf(config.nombreProfesor)
            personas.forEachIndexed { idx, nombre ->
                val pago = pagosSemana.firstOrNull { it.personaIdx == idx }
                FilaPagoPersona(
                    nombre = nombre,
                    montoPagado = pago?.montoPagado ?: 0.0,
                    onClickRegistrar = {
                        personaSeleccionada = idx
                        montoPago = (pago?.montoPagado ?: 0.0).toString()
                        mostrarDialogo = true
                    }
                )
            }
        }
    }

    // Dialog para registrar pago
    if (mostrarDialogo && esActual) {
        DialogRegistrarPago(
            personaNombre = config.nombresAlumnos().getOrElse(personaSeleccionada) {
                config.nombreProfesor
            },
            montoActual = montoPago,
            onMontoChange = { montoPago = it },
            onConfirmar = {
                val monto = montoPago.toDoubleOrNull() ?: 0.0
                viewModel.registrarPago(personaSeleccionada, monto)
                mostrarDialogo = false
                montoPago = ""
            },
            onCancelar = {
                mostrarDialogo = false
                montoPago = ""
            }
        )
    }
}

@Composable
private fun FilaPagoPersona(
    nombre: String,
    montoPagado: Double,
    onClickRegistrar: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(enabled = true) { onClickRegistrar() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (montoPagado > 0.0) "Pagado: ${montoPagado.formatoSoles()}" else "Sin pago",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        }
        Surface(
            color = if (montoPagado > 0.0)
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
            else
                MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                "Editar",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (montoPagado > 0.0)
                    MaterialTheme.colorScheme.onSecondary
                else
                    MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun DialogRegistrarPago(
    personaNombre: String,
    montoActual: String,
    onMontoChange: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar pago: $personaNombre") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Ingrese el monto pagado (en soles):",
                    style = MaterialTheme.typography.bodySmall,
                )
                androidx.compose.material3.OutlinedTextField(
                    value = montoActual,
                    onValueChange = onMontoChange,
                    label = { Text("Monto") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirmar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

// ═══════════════════════════════════════════════════════════════════
// Card de Gastos (Gasolina)
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun CardGastos(
    gastoSemana: GastoSemana?,
    viewModel: SemanaViewModel,
    esActual: Boolean,
) {
    var montoGasolina by remember { mutableStateOf((gastoSemana?.gasolina ?: 0.0).toString()) }
    var editando by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Gastos de Gasolina",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Gasto acumulado semanal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    )
                }
                Text(
                    (gastoSemana?.gasolina ?: 0.0).formatoSoles(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (editando && esActual) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = montoGasolina,
                        onValueChange = { montoGasolina = it },
                        label = { Text("Monto") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodySmall,
                    )
                    androidx.compose.material3.TextButton(
                        onClick = {
                            val monto = montoGasolina.toDoubleOrNull() ?: 0.0
                            viewModel.actualizarGasolina(monto)
                            editando = false
                        },
                    ) {
                        Text("✓", fontSize = 16.sp)
                    }
                    androidx.compose.material3.TextButton(
                        onClick = {
                            editando = false
                            montoGasolina = (gastoSemana?.gasolina ?: 0.0).toString()
                        },
                    ) {
                        Text("✕", fontSize = 16.sp)
                    }
                }
            } else if (esActual) {
                androidx.compose.material3.TextButton(
                    onClick = { editando = true },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text("Editar", fontSize = 12.sp)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Card de Balance Neto
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun CardBalance(
    resumenCobro: SemanaViewModel.ResumenCobro,
    gananciaNeta: Double,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                "Balance Neto de la Semana",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            // Desglose
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Total generado:",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    resumenCobro.totalGenerado.formatoSoles(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Total cobrado:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    resumenCobro.totalCobrado.formatoSoles(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Total pendiente:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    resumenCobro.totalPendiente.formatoSoles(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Gasolina:",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    resumenCobro.totalGasolina.formatoSoles(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 4.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Ganancia Neta:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    gananciaNeta.formatoSoles(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (gananciaNeta >= 0)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
