package com.motocontable.app.data.dao

import androidx.room.*
import com.motocontable.app.data.entity.Extra
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtraDao {
    @Query("SELECT * FROM extras WHERE fecha = :fecha ORDER BY id ASC")
    fun observarPorFecha(fecha: String): Flow<List<Extra>>

    @Query("SELECT * FROM extras WHERE fecha BETWEEN :inicio AND :fin ORDER BY fecha ASC, id ASC")
    fun observarRango(inicio: String, fin: String): Flow<List<Extra>>

    @Query("SELECT * FROM extras ORDER BY fecha DESC, id DESC")
    fun observarTodos(): Flow<List<Extra>>

    @Insert
    suspend fun insertar(extra: Extra)

    @Update
    suspend fun actualizar(extra: Extra)

    @Delete
    suspend fun eliminar(extra: Extra)
}
