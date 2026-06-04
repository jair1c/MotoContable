package com.motocontable.app.data.dao

import androidx.room.*
import com.motocontable.app.data.entity.Configuracion
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfiguracionDao {
    @Query("SELECT * FROM configuracion WHERE id = 1")
    fun observar(): Flow<Configuracion?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(config: Configuracion)
}
