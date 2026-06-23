package com.motocontable.app.backup

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.motocontable.app.data.repository.RegistroRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit

/**
 * Worker que ejecuta backups automáticos cada 6 horas.
 * Se ejecuta en background aunque la app esté cerrada.
 */
class BackupWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BackupEntryPoint {
        fun backupService(): BackupService
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                BackupEntryPoint::class.java
            )
            val backupService = entryPoint.backupService()
            
            // Hacer backup automático
            val result = backupService.hacerBackupAutomatico()
            
            if (result.isSuccess) {
                Result.success()
            } else {
                // Reintentar en 15 minutos si falla
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val BACKUP_WORK_NAME = "motocontable_backup_worker"

        /**
         * Programar backups automáticos cada 6 horas.
         * Llamar esto desde MainActivity al iniciar la app.
         */
        fun programarBackupsPeriodicos(context: Context) {
            val backupRequest = PeriodicWorkRequestBuilder<BackupWorker>(
                6, TimeUnit.HOURS,  // Cada 6 horas
                1, TimeUnit.HOURS,  // Ventana flexible de 1 hora
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                BACKUP_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                backupRequest
            )
        }

        /**
         * Cancelar backups automáticos (si el usuario lo desea).
         */
        fun cancelarBackupsPeriodicos(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(BACKUP_WORK_NAME)
        }
    }
}
