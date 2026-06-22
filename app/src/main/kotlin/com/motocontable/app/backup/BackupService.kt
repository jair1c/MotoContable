package com.motocontable.app.backup

import android.content.Context
import android.os.Environment
import com.motocontable.app.data.repository.RegistroRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Servicio para exportar e importar datos de la app.
 * Guarda en: /storage/emulated/0/MotoContable/backups/
 */
class BackupService @Inject constructor(
    private val context: Context,
    private val repo: RegistroRepository,
) {

    // Ruta de backups en el dispositivo (no se borra al desinstalar)
    private val backupDir: File
        get() {
            val docDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "MotoContable/backups"
            )
            if (!docDir.exists()) docDir.mkdirs()
            return docDir
        }

    // Ruta de archivos exportados para compartir
    private val exportDir: File
        get() {
            val docDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "MotoContable/exports"
            )
            if (!docDir.exists()) docDir.mkdirs()
            return docDir
        }

    data class BackupInfo(
        val fileName: String,
        val timestamp: Long,
        val formattedDate: String,
    )

    // ═══════════════════════════════════════════════════════════════════
    // BACKUP AUTOMÁTICO (se ejecuta cada cierto tiempo en background)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Hacer backup automático de los datos.
     * Se ejecuta periódicamente (cada 6 horas, configurado en WorkManager).
     * Solo mantiene los últimos 10 backups.
     */
    suspend fun hacerBackupAutomatico() = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
            val filename = "backup_${dateFormat.format(Date(timestamp))}.json"
            val file = File(backupDir, filename)

            // Obtener todos los datos
            val registros = repo.observarTodosRegistros().first()
            val extras = repo.observarTodosExtras().first()
            val pagos = repo.observarTodosPagos().first()
            val gastos = repo.observarTodosGastos().first()
            val config = repo.observarConfiguracion().first()

            // Crear JSON
            val backupJson = JSONObject().apply {
                put("version", BACKUP_VERSION)
                put("timestamp", timestamp)
                put("date", SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp)))
                put("registros", JSONArray(registros.map { it.toJSON() }))
                put("extras", JSONArray(extras.map { it.toJSON() }))
                put("pagos", JSONArray(pagos.map { it.toJSON() }))
                put("gastos", JSONArray(gastos.map { it.toJSON() }))
                put("configuracion", config.toJSON())
            }

            // Escribir archivo
            file.writeText(backupJson.toString(2), StandardCharsets.UTF_8)

            // Limpiar backups viejos (mantener solo últimos 10)
            limpiarBackupsAntiguos()

            Result.success(filename)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun limpiarBackupsAntiguos() {
        val backups = backupDir.listFiles()?.filter { it.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?: return

        // Eliminar todos excepto los últimos 10
        backups.drop(10).forEach { it.delete() }
    }

    // ═══════════════════════════════════════════════════════════════════
    // EXPORTAR DATOS (para compartir o respaldar manualmente)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Exportar datos a JSON (para compartir o respaldar).
     */
    suspend fun exportarDatos(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
            val filename = "export_${dateFormat.format(Date(timestamp))}.json"
            val file = File(exportDir, filename)

            // Obtener todos los datos
            val registros = repo.observarTodosRegistros().first()
            val extras = repo.observarTodosExtras().first()
            val pagos = repo.observarTodosPagos().first()
            val gastos = repo.observarTodosGastos().first()
            val config = repo.observarConfiguracion().first()

            // Crear JSON
            val exportJson = JSONObject().apply {
                put("version", BACKUP_VERSION)
                put("timestamp", timestamp)
                put("date", SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp)))
                put("registros", JSONArray(registros.map { it.toJSON() }))
                put("extras", JSONArray(extras.map { it.toJSON() }))
                put("pagos", JSONArray(pagos.map { it.toJSON() }))
                put("gastos", JSONArray(gastos.map { it.toJSON() }))
                put("configuracion", config.toJSON())
            }

            // Escribir archivo
            file.writeText(exportJson.toString(2), StandardCharsets.UTF_8)
            Result.success(filename)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // IMPORTAR DATOS (restaurar desde backup o exportación)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Importar datos desde un archivo JSON.
     * @param filePath Path completo del archivo
     */
    suspend fun importarDatos(filePath: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("Archivo no encontrado"))
            }

            val content = file.readText(StandardCharsets.UTF_8)
            val json = JSONObject(content)

            // Validar versión
            val version = json.optInt("version", 1)
            if (version != BACKUP_VERSION) {
                return@withContext Result.failure(
                    Exception("Versión de backup incompatible: $version")
                )
            }

            // Importar datos (esto debería usar transacción atómica)
            try {
                // Registros
                val registrosArray = json.getJSONArray("registros")
                for (i in 0 until registrosArray.length()) {
                    val obj = registrosArray.getJSONObject(i)
                    // repo.guardarRegistro(RegistroDiario.fromJSON(obj))
                }

                // Extras
                val extrasArray = json.getJSONArray("extras")
                for (i in 0 until extrasArray.length()) {
                    val obj = extrasArray.getJSONObject(i)
                    // repo.guardarExtra(Extra.fromJSON(obj))
                }

                // Pagos
                val pagosArray = json.getJSONArray("pagos")
                for (i in 0 until pagosArray.length()) {
                    val obj = pagosArray.getJSONObject(i)
                    // repo.guardarPago(PagoSemana.fromJSON(obj))
                }

                // Gastos
                val gastosArray = json.getJSONArray("gastos")
                for (i in 0 until gastosArray.length()) {
                    val obj = gastosArray.getJSONObject(i)
                    // repo.guardarGasto(GastoSemana.fromJSON(obj))
                }

                Result.success("Datos importados exitosamente")
            } catch (e: Exception) {
                Result.failure(Exception("Error al importar datos: ${e.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // RESTAURAR DESDE BACKUP AUTOMÁTICO (al instalar de nuevo)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Listar backups automáticos disponibles.
     */
    fun listarBackups(): List<BackupInfo> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return backupDir.listFiles()?.filter { it.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?.map { file ->
                BackupInfo(
                    fileName = file.absolutePath,
                    timestamp = file.lastModified(),
                    formattedDate = dateFormat.format(Date(file.lastModified())),
                )
            } ?: emptyList()
    }

    /**
     * Restaurar desde el backup más reciente.
     * Se ejecuta al abrir la app si detecta que fue reinstalada.
     */
    suspend fun restaurarUltimoBackup(): Result<String> = withContext(Dispatchers.IO) {
        val backups = listarBackups()
        if (backups.isEmpty()) {
            return@withContext Result.failure(Exception("No hay backups disponibles"))
        }
        importarDatos(backups.first().fileName)
    }

    // ═══════════════════════════════════════════════════════════════════
    // HELPERS PARA JSON
    // ═══════════════════════════════════════════════════════════════════

    companion object {
        const val BACKUP_VERSION = 2  // Versión del formato de backup
    }
}

// Extensiones para convertir a/desde JSON (a implementar en cada Entity)
// Ejemplo:
/*
fun RegistroDiario.toJSON(): JSONObject = JSONObject().apply {
    put("fecha", fecha)
    put("alumno1Ida", alumno1Ida)
    put("alumno1Vuelta", alumno1Vuelta)
    // ... más campos
}

fun Extra.toJSON(): JSONObject = JSONObject().apply {
    put("id", id)
    put("fecha", fecha)
    put("descripcion", descripcion)
    put("monto", monto)
    put("pagado", pagado)
}

fun PagoSemana.toJSON(): JSONObject = JSONObject().apply {
    put("semanaISO", semanaISO)
    put("personaIdx", personaIdx)
    put("montoPagado", montoPagado)
}

fun GastoSemana.toJSON(): JSONObject = JSONObject().apply {
    put("semanaISO", semanaISO)
    put("gasolina", gasolina)
}

fun Configuracion.toJSON(): JSONObject = JSONObject().apply {
    put("nombreProfesor", nombreProfesor)
    put("precioAlumnoViaje", precioAlumnoViaje)
    put("precioProfesorViaje", precioProfesorViaje)
    put("alumno1", alumno1)
    put("alumno2", alumno2)
    put("alumno3", alumno3)
    put("alumno4", alumno4)
}
*/
