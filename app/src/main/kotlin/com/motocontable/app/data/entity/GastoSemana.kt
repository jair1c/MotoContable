package com.motocontable.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Gastos de la semana (gasolina, etc.). Un registro por semana. */
@Entity(tableName = "gastos_semana")
data class GastoSemana(
    @PrimaryKey val semanaISO: String,
    val gasolina: Double = 0.0,
)
