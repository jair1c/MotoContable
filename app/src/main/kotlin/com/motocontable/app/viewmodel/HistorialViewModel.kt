package com.motocontable.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.entity.Extra
import com.motocontable.app.data.entity.GastoSemana
import com.motocontable.app.data.entity.PagoSemana
import com.motocontable.app.data.entity.RegistroDiario
import com.motocontable.app.data.repository.RegistroRepository
import com.motocontable.app.util.FechaUtil
import com.motocontable.app.util.totalDia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val repo: RegistroRepository,
) : ViewModel() {

    val configuracion: StateFlow<Configuracion> = repo.observarConfiguracion()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Configuracion())

    data class SemanaHistorial(
        val titulo: String,           // "2 - 6 Jun 2026"
        val lunesISO: String,
        val viernesISO: String,
        val registros: List<RegistroDiario>,  // ordenados por fecha
        val extras: List<Extra>,
        val pagos: List<PagoSemana>,          // NUEVO: pagos de la semana
        val gasto: GastoSemana?,              // NUEVO: gasto de gasolina
        val total: Double,
        val totalCobrado: Double,             // NUEVO: suma de pagos
        val totalPendiente: Double,           // NUEVO: total - cobrado
        val gananciaNeta: Double,             // NUEVO: total - gasolina
        val diasConRegistro: Int,
    )

    /** Lista de semanas con datos, de la mas reciente a la mas antigua. */
    val semanas: StateFlow<List<SemanaHistorial>> = combine(
        repo.observarTodosRegistros(),
        repo.observarTodosExtras(),
        repo.observarTodosPagos(),  // NUEVO
        repo.observarTodosGastos(), // NUEVO
        configuracion,
    ) { registros, extras, pagos, gastos, config ->
        agruparPorSemana(registros, extras, pagos, gastos, config)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ─────────────────────────────────────────────────────────────
    private fun agruparPorSemana(
        todos: List<RegistroDiario>,
        todosExtras: List<Extra>,
        todosPagos: List<PagoSemana>,      // NUEVO
        todosGastos: List<GastoSemana>,    // NUEVO
        config: Configuracion,
    ): List<SemanaHistorial> {
        if (todos.isEmpty()) return emptyList()

        val fmt = DateTimeFormatter.ISO_LOCAL_DATE

        // Agrupar por el lunes de cada semana
        val porSemana = todos.groupBy { reg ->
            LocalDate.parse(reg.fecha, fmt).with(DayOfWeek.MONDAY).format(fmt)
        }

        return porSemana.entries
            .sortedByDescending { it.key }
            .map { (lunesISO, regs) ->
                val viernesISO = LocalDate.parse(lunesISO, fmt).plusDays(4).format(fmt)
                val extrasSem  = todosExtras.filter { it.fecha in lunesISO..viernesISO }
                val pagosSem   = todosPagos.filter { it.semanaISO == lunesISO }  // NUEVO
                val gastoSem   = todosGastos.firstOrNull { it.semanaISO == lunesISO }  // NUEVO
                
                val totalSem   = regs.sumOf { reg ->
                    totalDia(reg, todosExtras.filter { it.fecha == reg.fecha }, config)
                }
                val cobradoSem = pagosSem.sumOf { it.montoPagado }  // NUEVO
                val pendienteSem = totalSem - cobradoSem  // NUEVO
                val gananciaSem = totalSem - (gastoSem?.gasolina ?: 0.0)  // NUEVO
                
                SemanaHistorial(
                    titulo           = FechaUtil.rangoLegibleDesde(lunesISO),
                    lunesISO         = lunesISO,
                    viernesISO       = viernesISO,
                    registros        = regs.sortedBy { it.fecha },
                    extras           = extrasSem,
                    pagos            = pagosSem,           // NUEVO
                    gasto            = gastoSem,           // NUEVO
                    total            = totalSem,
                    totalCobrado     = cobradoSem,         // NUEVO
                    totalPendiente   = pendienteSem,       // NUEVO
                    gananciaNeta     = gananciaSem,        // NUEVO
                    diasConRegistro  = regs.size,
                )
            }
    }
}
