package com.motocontable.app.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Agrega columna 'pagado' a extras (default = 0 = false)
        db.execSQL(
            "ALTER TABLE extras ADD COLUMN pagado INTEGER NOT NULL DEFAULT 0"
        )
        // 2. Tabla de pagos por persona por semana
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `pagos_semana` (
                `semanaISO`   TEXT    NOT NULL,
                `personaIdx`  INTEGER NOT NULL,
                `montoPagado` REAL    NOT NULL,
                PRIMARY KEY(`semanaISO`, `personaIdx`)
            )
        """.trimIndent())
        // 3. Tabla de gastos semanales (gasolina)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `gastos_semana` (
                `semanaISO` TEXT NOT NULL,
                `gasolina`  REAL NOT NULL,
                PRIMARY KEY(`semanaISO`)
            )
        """.trimIndent())
    }
}
