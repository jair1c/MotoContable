package com.motocontable.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extras")
data class Extra(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: String,
    val descripcion: String,
    val monto: Double,
    val pagado: Boolean = false,   // true = ya cobrado
)
