package com.motocontable.app.data.repository

import com.motocontable.app.data.dao.*
import com.motocontable.app.data.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistroRepository @Inject constructor(
    private val registroDao: RegistroDao,
    private val extraDao: ExtraDao,
    private val configuracionDao: ConfiguracionDao,
    private val pagoSemanaDao: PagoSemanaDao,
    private val gastoSemanaDao: GastoSemanaDao,
) {
    // ── Config ────────────────────────────────────────────────────
    fun observarConfiguracion(): Flow<Configuracion> =
        configuracionDao.observar().map { it ?: Configuracion() }
    suspend fun guardarConfiguracion(c: Configuracion) = configuracionDao.guardar(c)

    // ── RegistroDiario ────────────────────────────────────────────
    suspend fun getRegistroPorFecha(fecha: String): RegistroDiario? = registroDao.getPorFecha(fecha)
    suspend fun guardarRegistro(r: RegistroDiario) = registroDao.guardar(r)
    suspend fun eliminarRegistro(r: RegistroDiario) = registroDao.eliminar(r)
    fun observarRegistrosSemana(ini: String, fin: String): Flow<List<RegistroDiario>> = registroDao.observarRango(ini, fin)
    fun observarTodosRegistros(): Flow<List<RegistroDiario>> = registroDao.observarTodos()

    // ── Extras ────────────────────────────────────────────────────
    fun observarExtrasPorFecha(fecha: String): Flow<List<Extra>> = extraDao.observarPorFecha(fecha)
    fun observarExtrasSemana(ini: String, fin: String): Flow<List<Extra>> = extraDao.observarRango(ini, fin)
    fun observarTodosExtras(): Flow<List<Extra>> = extraDao.observarTodos()
    suspend fun agregarExtra(extra: Extra) = extraDao.insertar(extra)
    suspend fun actualizarExtra(extra: Extra) = extraDao.actualizar(extra)
    suspend fun eliminarExtra(extra: Extra) = extraDao.eliminar(extra)

    // ── Pagos por persona ─────────────────────────────────────────
    fun observarPagosSemana(semanaISO: String): Flow<List<PagoSemana>> =
        pagoSemanaDao.observarPorSemana(semanaISO)
    
    fun observarTodosPagos(): Flow<List<PagoSemana>> =
        pagoSemanaDao.observarTodos()  // NUEVO: para historial
    
    suspend fun guardarPago(pago: PagoSemana) = pagoSemanaDao.guardar(pago)
    suspend fun eliminarPago(semanaISO: String, personaIdx: Int) =
        pagoSemanaDao.eliminar(semanaISO, personaIdx)

    // ── Gastos semanales ──────────────────────────────────────────
    fun observarGastoSemana(semanaISO: String): Flow<GastoSemana?> =
        gastoSemanaDao.observar(semanaISO)
    
    fun observarTodosGastos(): Flow<List<GastoSemana>> =
        gastoSemanaDao.observarTodos()  // NUEVO: para historial
    
    suspend fun guardarGasto(gasto: GastoSemana) = gastoSemanaDao.guardar(gasto)
}
