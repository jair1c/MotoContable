package com.motocontable.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.motocontable.app.data.dao.*
import com.motocontable.app.data.entity.*

@Database(
    entities = [
        RegistroDiario::class,
        Extra::class,
        Configuracion::class,
        PagoSemana::class,
        GastoSemana::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registroDao(): RegistroDao
    abstract fun extraDao(): ExtraDao
    abstract fun configuracionDao(): ConfiguracionDao
    abstract fun pagoSemanaDao(): PagoSemanaDao
    abstract fun gastoSemanaDao(): GastoSemanaDao
}
