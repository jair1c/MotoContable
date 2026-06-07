package com.motocontable.app.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.motocontable.app.ui.screens.PantallaHoy

// ── Rutas ──────────────────────────────────────────────────────────
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Hoy           : Screen("hoy",          "Hoy",       Icons.Default.Today)
    object Semana        : Screen("semana",        "Semana",    Icons.Default.DateRange)
    object Historial     : Screen("historial",     "Historial", Icons.Default.History)
    object Configuracion : Screen("configuracion", "Config",    Icons.Default.Settings)
}

private val bottomItems = listOf(
    Screen.Hoy, Screen.Semana, Screen.Historial, Screen.Configuracion,
)

// ── NavGraph ───────────────────────────────────────────────────────
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Hoy.route) {

        composable(Screen.Hoy.route) {
            PantallaHoy()                                           // ← real
        }
        composable(Screen.Semana.route) {
            PantallaPlaceholder(Icons.Default.DateRange, "Resumen semanal",
                "Proximo: totales por dia de la semana (Punto 4)")
        }
        composable(Screen.Historial.route) {
            PantallaPlaceholder(Icons.Default.History, "Historial",
                "Proximo: semanas anteriores (Punto 5)")
        }
        composable(Screen.Configuracion.route) {
            PantallaPlaceholder(Icons.Default.Settings, "Configuracion",
                "Proximo: nombres y precios (Punto 5)")
        }
    }
}

// ── Bottom Navigation Bar ──────────────────────────────────────────
@Composable
fun BottomNavBar(navController: NavHostController) {
    val entrada    by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    NavigationBar {
        bottomItems.forEach { screen ->
            NavigationBarItem(
                icon     = { Icon(screen.icon, contentDescription = screen.label) },
                label    = { Text(screen.label, fontSize = 11.sp) },
                selected = rutaActual == screen.route,
                onClick  = {
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Hoy.route) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
            )
        }
    }
}

// ── Placeholder genérico (para puntos futuros) ─────────────────────
@Composable
private fun PantallaPlaceholder(icono: ImageVector, titulo: String, subtitulo: String) {
    Column(
        modifier             = Modifier.fillMaxSize(),
        verticalArrangement  = Arrangement.Center,
        horizontalAlignment  = Alignment.CenterHorizontally,
    ) {
        Icon(icono, null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        Spacer(Modifier.height(12.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(subtitulo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
    }
}
