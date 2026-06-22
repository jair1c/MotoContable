package com.motocontable.app.data.dao

import androidx.room.*
import com.motocontable.app.data.entity.GastoSemana
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoSemanaDao {
    @Query("SELECT * FROM gastos_semana WHERE semanaISO = :semanaISO")
    fun observar(semanaISO: String): Flow<GastoSemana?>
    
    @Query("SELECT * FROM gastos_semana ORDER BY semanaISO DESC")
    fun observarTodos(): Flow<List<GastoSemana>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(gasto: GastoSemana)
}
