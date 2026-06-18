package com.motocontable.app.data.entity

import androidx.room.Entity

/**
 * Cuanto pago una persona en una semana determinada.
 * personaIdx: 0-3 = alumnos, 4 = profesor.
 * Una sola fila por (semana, persona). Se reemplaza al actualizar.
 */
@Entity(
    tableName = "pagos_semana",
    primaryKeys = ["semanaISO", "personaIdx"],
)
data class PagoSemana(
    val semanaISO: String,    // ISO del lunes: "2026-06-02"
    val personaIdx: Int,
    val montoPagado: Double = 0.0,
)
