package com.motocontable.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motocontable.app.data.entity.*
import com.motocontable.app.data.repository.RegistroRepository
import com.motocontable.app.util.FechaUtil
import com.motocontable.app.util.totalDia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SemanaViewModel @Inject constructor(
    private val repo: RegistroRepository,
) : ViewModel() {

    private val _offset = MutableStateFlow(0)
    val offsetSemana: StateFlow<Int> = _offset.asStateFlow()

    // Función auxiliar: obtener semana ISO (lunes) a partir de un offset
    private fun semanaISO(offset: Int = 0): String = FechaUtil.luneSemana(offset).format(
        java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
    )

    val configuracion: StateFlow<Configuracion> = repo.observarConfiguracion()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Configuracion())

    val registrosSemana: StateFlow<List<RegistroDiario>> = _offset
        .flatMapLatest { offset -> val (i,f) = FechaUtil.rangoSemana(offset); repo.observarRegistrosSemana(i,f) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val extrasSemana: StateFlow<List<Extra>> = _offset
        .flatMapLatest { offset -> val (i,f) = FechaUtil.rangoSemana(offset); repo.observarExtrasSemana(i,f) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Pagos por persona ────────────────────────────────────────
    val pagosSemana: StateFlow<List<PagoSemana>> = _offset
        .flatMapLatest { offset -> repo.observarPagosSemana(semanaISO(offset)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Gastos (gasolina) ────────────────────────────────────────
    val gastoSemana: StateFlow<GastoSemana?> = _offset
        .flatMapLatest { offset -> repo.observarGastoSemana(semanaISO(offset)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    data class DiaResumen(
        val fecha: String, val nombreDia: String,
        val registro: RegistroDiario?, val extras: List<Extra>, val total: Double
    )

    val diasResumen: StateFlow<List<DiaResumen>> = combine(
        _offset, registrosSemana, extrasSemana, configuracion
    ) { offset, registros, extras, config ->
        FechaUtil.diasLaboralesSemana(offset).map { fecha ->
            val reg = registros.firstOrNull { it.fecha == fecha }
            val ex = extras.filter { it.fecha == fecha }
            DiaResumen(fecha, FechaUtil.nombreDia(fecha), reg, ex,
                if (reg != null) totalDia(reg, ex, config) else 0.0)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalSemana: StateFlow<Double> = diasResumen
        .map { it.sumOf { d -> d.total } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    // ── Balance neto (total generado - gasolina) ──────────────────
    val gananciaNeta: StateFlow<Double> = combine(totalSemana, gastoSemana) { total, gasto ->
        total - (gasto?.gasolina ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    // ── Totales de cobro (pendiente vs cobrado) ──────────────────
    data class ResumenCobro(
        val totalGenerado: Double,      // suma de todos los viajes + extras
        val totalCobrado: Double,       // suma de pagos registrados
        val totalPendiente: Double,     // totalGenerado - totalCobrado
        val totalGasolina: Double,      // gastos de la semana
    )

    val resumenCobro: StateFlow<ResumenCobro> = combine(
        totalSemana, pagosSemana, gastoSemana
    ) { total, pagos, gasto ->
        val cobrado = pagos.sumOf { it.montoPagado }
        ResumenCobro(
            totalGenerado = total,
            totalCobrado = cobrado,
            totalPendiente = total - cobrado,
            totalGasolina = gasto?.gasolina ?: 0.0,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 
        ResumenCobro(0.0, 0.0, 0.0, 0.0)
    )

    val tituloSemana: StateFlow<String> = _offset
        .map { FechaUtil.rangoLegible(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FechaUtil.rangoLegible())

    // ── Funciones públicas ───────────────────────────────────────
    fun semanaAnterior() { _offset.value-- }
    fun semanaSiguiente() { if (_offset.value < 0) _offset.value++ }
    fun irASemanaActual() { _offset.value = 0 }

    // Registrar pago de una persona (alumno o profesor)
    fun registrarPago(personaIdx: Int, monto: Double) {
        viewModelScope.launch {
            val pago = PagoSemana(
                semanaISO = semanaISO(_offset.value),
                personaIdx = personaIdx,
                montoPagado = monto,
            )
            repo.guardarPago(pago)
        }
    }

    // Actualizar gasto de gasolina semanal
    fun actualizarGasolina(monto: Double) {
        viewModelScope.launch {
            val gasto = GastoSemana(
                semanaISO = semanaISO(_offset.value),
                gasolina = monto,
            )
            repo.guardarGasto(gasto)
        }
    }

    // Actualizar estado de pago de un extra
    fun actualizarExtraPagado(extra: Extra, pagado: Boolean) {
        viewModelScope.launch {
            repo.actualizarExtra(extra.copy(pagado = pagado))
        }
    }
}
