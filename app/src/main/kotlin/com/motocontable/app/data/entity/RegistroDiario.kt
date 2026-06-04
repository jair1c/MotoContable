package com.motocontable.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_diarios")
data class RegistroDiario(
    @PrimaryKey val fecha: String,
    val alumno1Ida: Boolean = false,
    val alumno1Vuelta: Boolean = false,
    val alumno2Ida: Boolean = false,
    val alumno2Vuelta: Boolean = false,
    val alumno3Ida: Boolean = false,
    val alumno3Vuelta: Boolean = false,
    val alumno4Ida: Boolean = false,
    val alumno4Vuelta: Boolean = false,
    val profesorIda: Boolean = false,
    val profesorVuelta: Boolean = false,
) {
    val tieneAlgunViaje: Boolean get() = listOf(
        alumno1Ida, alumno1Vuelta, alumno2Ida, alumno2Vuelta,
        alumno3Ida, alumno3Vuelta, alumno4Ida, alumno4Vuelta,
        profesorIda, profesorVuelta
    ).any { it }

    fun alumnoIda(idx: Int) = when(idx) {
        0 -> alumno1Ida; 1 -> alumno2Ida
        2 -> alumno3Ida; 3 -> alumno4Ida
        else -> false
    }
    fun alumnoVuelta(idx: Int) = when(idx) {
        0 -> alumno1Vuelta; 1 -> alumno2Vuelta
        2 -> alumno3Vuelta; 3 -> alumno4Vuelta
        else -> false
    }
}
