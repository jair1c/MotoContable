package com.motocontable.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.motocontable.app.viewmodel.HistorialViewModel

// ═══════════════════════════════════════════════════════════════════
@Composable
fun PantallaHistorial(
    viewModel: HistorialViewModel = hiltViewModel(),
) {
    val semanas by viewModel.semanas.collectAsState()
    val config  by viewModel.configuracion.collectAsState()

    if (semanas.isEmpty()) {
        EstadoVacioHistorial()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "Historial de semanas",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary,
        )

        semanas.forEach { semana ->
            CardSemanaHistorial(semana = semana, config = config)
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════
// Estado vacio
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun EstadoVacioHistorial() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Sin registros aun",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Empieza marcando la asistencia de hoy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Tarjeta de una semana
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun CardSemanaHistorial(
    semana: HistorialViewModel.SemanaHistorial,
    config: Configuracion,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            // ── Header: titulo + total ─────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = semana.titulo,
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${semana.diasConRegistro} de 5 dias registrados",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    )
                }
                Text(
                    text       = semana.total.formatoSoles(),
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.secondary,
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            )

            // ── Dias de la semana ──────────────────────────────────
            FechaUtil.diasDeSemana(semana.lunesISO).forEach { fecha ->
                val registro = semana.registros.firstOrNull { it.fecha == fecha }
                val extDia   = semana.extras.filter { it.fecha == fecha }
                FilaDiaMini(
                    fecha    = fecha,
                    registro = registro,
                    extras   = extDia,
                    config   = config,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
// Fila compacta de un dia en el historial
// ═══════════════════════════════════════════════════════════════════
@Composable
private fun FilaDiaMini(
    fecha: String,
    registro: RegistroDiario?,
    extras: List<Extra>,
    config: Configuracion,
) {
    val esHoy     = FechaUtil.esHoy(fecha)
    val sinDatos  = registro == null

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Nombre del dia (3 letras)
        Row(
            modifier = Modifier.width(44.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text       = FechaUtil.nombreDiaCorto(fecha),
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = if (esHoy) FontWeight.Bold else FontWeight.Normal,
                color      = if (esHoy) MaterialTheme.colorScheme.primary
                             else MaterialTheme.colorScheme.onBackground,
            )
            if (esHoy) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(3.dp),
                ) {
                    Text(
                        "H",
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }

        if (sinDatos) {
            Text(
                "Sin registro",
                modifier = Modifier.weight(1f),
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            )
        } else {
            // Indicadores de alumnos: I V por persona, compacto
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                (0..3).forEach { idx ->
                    MiniIndicadorPersona(
                        ida    = registro!!.alumnoIda(idx),
                        vuelta = registro.alumnoVuelta(idx),
                        nombre = config.nombresAlumnos()[idx].take(3),
                    )
                }
                Spacer(Modifier.width(4.dp))
                MiniIndicadorPersona(
                    ida    = registro!!.profesorIda,
                    vuelta = registro.profesorVuelta,
                    nombre = "Prof",
                )
            }

            // Total del dia
            Column(horizontalAlignment = Alignment.End) {
                val totalPersonas = (0..3).sumOf { idx ->
                    (if (registro!!.alumnoIda(idx)) config.precioAlumnoViaje else 0.0) +
                    (if (registro.alumnoVuelta(idx)) config.precioAlumnoViaje else 0.0)
                } + (if (registro!!.profesorIda) config.precioProfesorViaje else 0.0) +
                    (if (registro.profesorVuelta) config.precioProfesorViaje else 0.0)
                val totalExtras = extras.sumOf { it.monto }
                val totalDiaVal = totalPersonas + totalExtras

                Text(
                    text       = totalDiaVal.formatoSoles(),
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.secondary,
                )
                if (extras.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            Icons.Default.DirectionsBike,
                            null,
                            Modifier.size(9.dp),
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                        )
                        Text(
                            "+${extras.size}",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniIndicadorPersona(ida: Boolean, vuelta: Boolean, nombre: String) {
    val activo = ida || vuelta
    Surface(
        color = if (activo) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(3.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                nombre,
                fontSize   = 8.sp,
                fontWeight = FontWeight.Medium,
                color      = if (activo) MaterialTheme.colorScheme.secondary
                             else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                maxLines   = 1,
                overflow   = TextOverflow.Clip,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    "I",
                    fontSize = 7.sp,
                    color    = if (ida) MaterialTheme.colorScheme.secondary
                               else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    fontWeight = if (ida) FontWeight.Bold else FontWeight.Normal,
                )
                Text(
                    "V",
                    fontSize = 7.sp,
                    color    = if (vuelta) MaterialTheme.colorScheme.secondary
                               else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    fontWeight = if (vuelta) FontWeight.Bold else FontWeight.Normal,
                )
            }
        }
    }
}
