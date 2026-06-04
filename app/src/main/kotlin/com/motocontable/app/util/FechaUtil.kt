package com.motocontable.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object FechaUtil {
    private val ISO = DateTimeFormatter.ISO_LOCAL_DATE
    private val ES = Locale("es", "PE")

    fun hoyISO(): String = LocalDate.now().format(ISO)

    fun luneSemana(offsetSemanas: Int = 0): LocalDate =
        LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(offsetSemanas.toLong())

    fun rangoSemana(offsetSemanas: Int = 0): Pair<String, String> {
        val lunes = luneSemana(offsetSemanas)
        return lunes.format(ISO) to lunes.plusDays(4).format(ISO)
    }

    fun formatoCorto(iso: String): String {
        val d = LocalDate.parse(iso, ISO)
        val dia = d.dayOfWeek.getDisplayName(TextStyle.SHORT, ES).replaceFirstChar { it.uppercase() }.trimEnd('.')
        val mes = d.month.getDisplayName(TextStyle.SHORT, ES).replaceFirstChar { it.uppercase() }
        return "\$dia \${d.dayOfMonth} \$mes"
    }

    fun nombreDia(iso: String): String {
        val d = LocalDate.parse(iso, ISO)
        return d.dayOfWeek.getDisplayName(TextStyle.FULL, ES).replaceFirstChar { it.uppercase() }
    }

    fun rangoLegible(offsetSemanas: Int = 0): String {
        val lunes = luneSemana(offsetSemanas)
        val viernes = lunes.plusDays(4)
        val mes = lunes.month.getDisplayName(TextStyle.SHORT, ES).replaceFirstChar { it.uppercase() }
        return "\${lunes.dayOfMonth} - \${viernes.dayOfMonth} \$mes \${lunes.year}"
    }

    fun esHoy(iso: String): Boolean = iso == hoyISO()

    fun diasLaboralesSemana(offsetSemanas: Int = 0): List<String> {
        val lunes = luneSemana(offsetSemanas)
        return (0..4).map { lunes.plusDays(it.toLong()).format(ISO) }
    }
}
