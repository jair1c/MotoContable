package com.motocontable.app.data.dao

import androidx.room.*
import com.motocontable.app.data.entity.PagoSemana
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoSemanaDao {
    @Query("SELECT * FROM pagos_semana WHERE semanaISO = :semanaISO")
    fun observarPorSemana(semanaISO: String): Flow<List<PagoSemana>>
    
    @Query("SELECT * FROM pagos_semana ORDER BY semanaISO DESC")
    fun observarTodos(): Flow<List<PagoSemana>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(pago: PagoSemana)

    @Query("DELETE FROM pagos_semana WHERE semanaISO = :semanaISO AND personaIdx = :personaIdx")
    suspend fun eliminar(semanaISO: String, personaIdx: Int)
}
