package com.motocontable.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.motocontable.app.data.dao.ConfiguracionDao
import com.motocontable.app.data.dao.ExtraDao
import com.motocontable.app.data.dao.RegistroDao
import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.entity.Extra
import com.motocontable.app.data.entity.RegistroDiario

@Database(
    entities = [RegistroDiario::class, Extra::class, Configuracion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registroDao(): RegistroDao
    abstract fun extraDao(): ExtraDao
    abstract fun configuracionDao(): ConfiguracionDao
}
