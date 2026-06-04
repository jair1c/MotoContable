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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val repo: RegistroRepository,
) : ViewModel() {

    val configuracion: StateFlow<Configuracion> = repo.observarConfiguracion()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Configuracion())

    private val _fecha = MutableStateFlow(FechaUtil.hoyISO())
    val fecha: StateFlow<String> = _fecha.asStateFlow()

    private val _registro = MutableStateFlow(RegistroDiario(fecha = FechaUtil.hoyISO()))
    val registro: StateFlow<RegistroDiario> = _registro.asStateFlow()

    val extras: StateFlow<List<Extra>> = _fecha
        .flatMapLatest { repo.observarExtrasPorFecha(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalHoy: StateFlow<Double> = combine(_registro, extras, configuracion) { reg, ex, cfg ->
        totalDia(reg, ex, cfg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    init { cargarRegistro(FechaUtil.hoyISO()) }

    private fun cargarRegistro(fecha: String) {
        viewModelScope.launch {
            val existente = repo.getRegistroPorFecha(fecha)
            _registro.value = existente ?: RegistroDiario(fecha = fecha)
            _guardado.value = existente != null
        }
    }

    fun toggleViaje(personaIdx: Int, esIda: Boolean) {
        val r = _registro.value
        _registro.value = when (personaIdx) {
            0 -> if (esIda) r.copy(alumno1Ida = !r.alumno1Ida) else r.copy(alumno1Vuelta = !r.alumno1Vuelta)
            1 -> if (esIda) r.copy(alumno2Ida = !r.alumno2Ida) else r.copy(alumno2Vuelta = !r.alumno2Vuelta)
            2 -> if (esIda) r.copy(alumno3Ida = !r.alumno3Ida) else r.copy(alumno3Vuelta = !r.alumno3Vuelta)
            3 -> if (esIda) r.copy(alumno4Ida = !r.alumno4Ida) else r.copy(alumno4Vuelta = !r.alumno4Vuelta)
            4 -> if (esIda) r.copy(profesorIda = !r.profesorIda) else r.copy(profesorVuelta = !r.profesorVuelta)
            else -> r
        }
    }

    fun guardarRegistro() {
        viewModelScope.launch { repo.guardarRegistro(_registro.value); _guardado.value = true }
    }

    fun agregarExtra(descripcion: String, monto: Double) {
        if (descripcion.isBlank() || monto <= 0.0) return
        viewModelScope.launch { repo.agregarExtra(Extra(fecha = _fecha.value, descripcion = descripcion, monto = monto)) }
    }

    fun eliminarExtra(extra: Extra) { viewModelScope.launch { repo.eliminarExtra(extra) } }

    fun setFecha(nuevaFecha: String) { _fecha.value = nuevaFecha; cargarRegistro(nuevaFecha) }
    fun irAHoy() = setFecha(FechaUtil.hoyISO())
}
