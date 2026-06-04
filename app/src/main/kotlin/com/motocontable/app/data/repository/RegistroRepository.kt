package com.motocontable.app.data.repository

import com.motocontable.app.data.dao.ConfiguracionDao
import com.motocontable.app.data.dao.ExtraDao
import com.motocontable.app.data.dao.RegistroDao
import com.motocontable.app.data.entity.Configuracion
import com.motocontable.app.data.entity.Extra
import com.motocontable.app.data.entity.RegistroDiario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistroRepository @Inject constructor(
    private val registroDao: RegistroDao,
    private val extraDao: ExtraDao,
    private val configuracionDao: ConfiguracionDao,
) {
    fun observarConfiguracion(): Flow<Configuracion> =
        configuracionDao.observar().map { it ?: Configuracion() }

    suspend fun guardarConfiguracion(config: Configuracion) = configuracionDao.guardar(config)

    suspend fun getRegistroPorFecha(fecha: String): RegistroDiario? = registroDao.getPorFecha(fecha)
    suspend fun guardarRegistro(r: RegistroDiario) = registroDao.guardar(r)
    suspend fun eliminarRegistro(r: RegistroDiario) = registroDao.eliminar(r)
    fun observarRegistrosSemana(ini: String, fin: String): Flow<List<RegistroDiario>> = registroDao.observarRango(ini, fin)
    fun observarTodosRegistros(): Flow<List<RegistroDiario>> = registroDao.observarTodos()

    fun observarExtrasPorFecha(fecha: String): Flow<List<Extra>> = extraDao.observarPorFecha(fecha)
    fun observarExtrasSemana(ini: String, fin: String): Flow<List<Extra>> = extraDao.observarRango(ini, fin)
    fun observarTodosExtras(): Flow<List<Extra>> = extraDao.observarTodos()
    suspend fun agregarExtra(extra: Extra) = extraDao.insertar(extra)
    suspend fun actualizarExtra(extra: Extra) = extraDao.actualizar(extra)
    suspend fun eliminarExtra(extra: Extra) = extraDao.eliminar(extra)
}
