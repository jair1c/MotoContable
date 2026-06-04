package com.motocontable.app.util

import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.entity.Extra
import com.motocontable.app.data.entity.RegistroDiario

fun RegistroDiario.totalPersonas(config: Configuracion): Double {
    val p = config.precioAlumnoViaje
    val pP = config.precioProfesorViaje
    return listOf(alumno1Ida, alumno1Vuelta, alumno2Ida, alumno2Vuelta,
                  alumno3Ida, alumno3Vuelta, alumno4Ida, alumno4Vuelta)
        .count { it } * p +
        listOf(profesorIda, profesorVuelta).count { it } * pP
}

fun List<Extra>.totalExtras(): Double = sumOf { it.monto }

fun totalDia(registro: RegistroDiario, extras: List<Extra>, config: Configuracion): Double =
    registro.totalPersonas(config) + extras.totalExtras()

fun Double.formatoSoles(): String = "S/ %.2f".format(this)
