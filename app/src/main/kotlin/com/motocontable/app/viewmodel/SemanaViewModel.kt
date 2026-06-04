package com.motocontable.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.entity.Extra
import com.motocontable.app.data.entity.RegistroDiario
import com.motocontable.app.data.repository.RegistroRepository
import com.motocontable.app.util.FechaUtil
import com.motocontable.app.util.totalDia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SemanaViewModel @Inject constructor(
    private val repo: RegistroRepository,
) : ViewModel() {

    private val _offset = MutableStateFlow(0)
    val offsetSemana: StateFlow<Int> = _offset.asStateFlow()

    val configuracion: StateFlow<Configuracion> = repo.observarConfiguracion()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Configuracion())

    val registrosSemana: StateFlow<List<RegistroDiario>> = _offset
        .flatMapLatest { offset -> val (i,f) = FechaUtil.rangoSemana(offset); repo.observarRegistrosSemana(i,f) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val extrasSemana: StateFlow<List<Extra>> = _offset
        .flatMapLatest { offset -> val (i,f) = FechaUtil.rangoSemana(offset); repo.observarExtrasSemana(i,f) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    val tituloSemana: StateFlow<String> = _offset
        .map { FechaUtil.rangoLegible(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FechaUtil.rangoLegible())

    fun semanaAnterior() { _offset.value-- }
    fun semanaSiguiente() { if (_offset.value < 0) _offset.value++ }
    fun irASemanaActual() { _offset.value = 0 }
}
