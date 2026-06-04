package com.motocontable.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuracion")
data class Configuracion(
    @PrimaryKey val id: Int = 1,
    val nombreAlumno1: String = "Alumno 1",
    val nombreAlumno2: String = "Alumno 2",
    val nombreAlumno3: String = "Alumno 3",
    val nombreAlumno4: String = "Alumno 4",
    val nombreProfesor: String = "Profesor",
    val precioAlumnoViaje: Double = 2.0,
    val precioProfesorViaje: Double = 6.0
) {
    fun nombresAlumnos(): List<String> =
        listOf(nombreAlumno1, nombreAlumno2, nombreAlumno3, nombreAlumno4)
}
