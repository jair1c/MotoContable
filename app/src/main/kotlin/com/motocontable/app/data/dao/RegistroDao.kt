package com.motocontable.app.data.dao

import androidx.room.*
import com.motocontable.app.data.entity.RegistroDiario
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {
    @Query("SELECT * FROM registros_diarios WHERE fecha = :fecha LIMIT 1")
    suspend fun getPorFecha(fecha: String): RegistroDiario?

    @Query("SELECT * FROM registros_diarios WHERE fecha BETWEEN :inicio AND :fin ORDER BY fecha ASC")
    fun observarRango(inicio: String, fin: String): Flow<List<RegistroDiario>>

    @Query("SELECT * FROM registros_diarios ORDER BY fecha DESC")
    fun observarTodos(): Flow<List<RegistroDiario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(registro: RegistroDiario)

    @Delete
    suspend fun eliminar(registro: RegistroDiario)
}
