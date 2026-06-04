package com.motocontable.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.repository.RegistroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val repo: RegistroRepository,
) : ViewModel() {

    val configuracion: StateFlow<Configuracion> = repo.observarConfiguracion()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Configuracion())

    fun guardar(config: Configuracion) {
        viewModelScope.launch { repo.guardarConfiguracion(config) }
    }

    fun guardarNombres(a1: String, a2: String, a3: String, a4: String, prof: String) {
        viewModelScope.launch {
            repo.guardarConfiguracion(configuracion.value.copy(
                nombreAlumno1 = a1.trim().ifBlank { "Alumno 1" },
                nombreAlumno2 = a2.trim().ifBlank { "Alumno 2" },
                nombreAlumno3 = a3.trim().ifBlank { "Alumno 3" },
                nombreAlumno4 = a4.trim().ifBlank { "Alumno 4" },
                nombreProfesor = prof.trim().ifBlank { "Profesor" },
            ))
        }
    }

    fun guardarPrecios(precioAlumno: Double, precioProfesor: Double) {
        viewModelScope.launch {
            repo.guardarConfiguracion(configuracion.value.copy(
                precioAlumnoViaje = precioAlumno.coerceAtLeast(0.0),
                precioProfesorViaje = precioProfesor.coerceAtLeast(0.0),
            ))
        }
    }
}
