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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.entity.Extra
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

    val diasConRegistro = diasResumen.count { it.registro != null }

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
            esActual     = offset == 0,
            onAnterior   = { viewModel.semanaAnterior() },
            onSiguiente  = { viewModel.semanaSiguiente() },
            onIrActual   = { viewModel.irASemanaActual() },
        )

        // ── Tarjetas por dia ───────────────────────────────────────
        diasResumen.forEach { dia ->
            CardDia(dia = dia, config = config)
        }

        // ── Total semanal ──────────────────────────────────────────
        if (diasResumen.isNotEmpty()) {
            TotalSemana(
                total            = totalSemana,
                diasConRegistro  = diasConRegistro,
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
                    FilaExtraSemana(extra = extra)
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
private fun FilaExtraSemana(extra: Extra) {
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
