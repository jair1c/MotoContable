package com.motocontable.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object FechaUtil {

    private val ISO = DateTimeFormatter.ISO_LOCAL_DATE
    private val ES  = Locale("es", "PE")

    fun hoyISO(): String = LocalDate.now().format(ISO)

    fun luneSemana(offsetSemanas: Int = 0): LocalDate =
        LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(offsetSemanas.toLong())

    fun rangoSemana(offsetSemanas: Int = 0): Pair<String, String> {
        val lunes = luneSemana(offsetSemanas)
        return lunes.format(ISO) to lunes.plusDays(4).format(ISO)
    }

    /** "Mar 3 Jun" */
    fun formatoCorto(iso: String): String {
        val d   = LocalDate.parse(iso, ISO)
        val dia = d.dayOfWeek.getDisplayName(TextStyle.SHORT, ES)
            .replaceFirstChar { it.uppercase() }.trimEnd('.')
        val mes = d.month.getDisplayName(TextStyle.SHORT, ES)
            .replaceFirstChar { it.uppercase() }
        return "$dia ${d.dayOfMonth} $mes"
    }

    /** "Lun", "Mar", ... (3 letras) */
    fun nombreDiaCorto(iso: String): String =
        LocalDate.parse(iso, ISO).dayOfWeek
            .getDisplayName(TextStyle.SHORT, ES)
            .replaceFirstChar { it.uppercase() }.trimEnd('.')

    /** "Martes", "Miercoles", ... */
    fun nombreDia(iso: String): String =
        LocalDate.parse(iso, ISO).dayOfWeek
            .getDisplayName(TextStyle.FULL, ES)
            .replaceFirstChar { it.uppercase() }

    /** "2 - 6 Jun 2026" usando el offset de semanas */
    fun rangoLegible(offsetSemanas: Int = 0): String {
        val lunes   = luneSemana(offsetSemanas)
        val viernes = lunes.plusDays(4)
        val mes = lunes.month.getDisplayName(TextStyle.SHORT, ES)
            .replaceFirstChar { it.uppercase() }
        return "${lunes.dayOfMonth} - ${viernes.dayOfMonth} $mes ${lunes.year}"
    }

    /** "2 - 6 Jun 2026" usando el ISO del lunes (para el historial) */
    fun rangoLegibleDesde(lunesISO: String): String {
        val lunes   = LocalDate.parse(lunesISO, ISO)
        val viernes = lunes.plusDays(4)
        val mes = lunes.month.getDisplayName(TextStyle.SHORT, ES)
            .replaceFirstChar { it.uppercase() }
        return "${lunes.dayOfMonth} - ${viernes.dayOfMonth} $mes ${lunes.year}"
    }

    fun esHoy(iso: String): Boolean = iso == hoyISO()

    fun esFuturo(iso: String): Boolean =
        LocalDate.parse(iso, ISO).isAfter(LocalDate.now())

    fun diasLaboralesSemana(offsetSemanas: Int = 0): List<String> {
        val lunes = luneSemana(offsetSemanas)
        return (0..4).map { lunes.plusDays(it.toLong()).format(ISO) }
    }

    /** Lunes de la semana a la que pertenece una fecha */
    fun lunesDeSemana(iso: String): String =
        LocalDate.parse(iso, ISO).with(DayOfWeek.MONDAY).format(ISO)

    /** Viernes de la semana de un lunes dado */
    fun viernesDesdeLunes(lunesISO: String): String =
        LocalDate.parse(lunesISO, ISO).plusDays(4).format(ISO)

    /** Los 5 dias laborales de la semana a la que pertenece lunesISO */
    fun diasDeSemana(lunesISO: String): List<String> {
        val lunes = LocalDate.parse(lunesISO, ISO)
        return (0..4).map { lunes.plusDays(it.toLong()).format(ISO) }
    }

    fun diaLaboralAnterior(iso: String): String {
        var d = LocalDate.parse(iso, ISO).minusDays(1)
        while (d.dayOfWeek == DayOfWeek.SATURDAY || d.dayOfWeek == DayOfWeek.SUNDAY)
            d = d.minusDays(1)
        return d.format(ISO)
    }

    fun diaLaboralSiguiente(iso: String): String {
        var d = LocalDate.parse(iso, ISO).plusDays(1)
        while (d.dayOfWeek == DayOfWeek.SATURDAY || d.dayOfWeek == DayOfWeek.SUNDAY)
            d = d.plusDays(1)
        return d.format(ISO)
    }
}
