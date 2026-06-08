package com.motocontable.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.motocontable.app.ui.screens.PantallaConfiguracion
import com.motocontable.app.ui.screens.PantallaHistorial
import com.motocontable.app.ui.screens.PantallaHoy
import com.motocontable.app.ui.screens.PantallaSemana

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

// ── Grafo de navegacion ────────────────────────────────────────────
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Hoy.route) {
        composable(Screen.Hoy.route)           { PantallaHoy() }
        composable(Screen.Semana.route)        { PantallaSemana() }
        composable(Screen.Historial.route)     { PantallaHistorial() }
        composable(Screen.Configuracion.route) { PantallaConfiguracion() }
    }
}

// ── Barra de navegacion inferior ───────────────────────────────────
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
