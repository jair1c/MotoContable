package com.motocontable.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Hoy           : Screen("hoy",           "Hoy",      Icons.Default.Today)
    object Semana        : Screen("semana",         "Semana",   Icons.Default.DateRange)
    object Historial     : Screen("historial",      "Historial",Icons.Default.History)
    object Configuracion : Screen("configuracion",  "Config",   Icons.Default.Settings)
}

private val bottomItems = listOf(Screen.Hoy, Screen.Semana, Screen.Historial, Screen.Configuracion)

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Hoy.route) {
        composable(Screen.Hoy.route) {
            PantallaPlaceholder(Icons.Default.Today, "Registro del dia",
                "Proximo: marca ida/vuelta de cada alumno y extras")
        }
        composable(Screen.Semana.route) {
            PantallaPlaceholder(Icons.Default.DateRange, "Resumen semanal",
                "Proximo: totales por dia de la semana")
        }
        composable(Screen.Historial.route) {
            PantallaPlaceholder(Icons.Default.History, "Historial",
                "Proximo: semanas anteriores")
        }
        composable(Screen.Configuracion.route) {
            PantallaPlaceholder(Icons.Default.Settings, "Configuracion",
                "Proximo: nombres y precios")
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    NavigationBar {
        bottomItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, screen.label) },
                label = { Text(screen.label, fontSize = 11.sp) },
                selected = rutaActual == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Hoy.route) { saveState = true }
                        launchSingleTop = true; restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun PantallaPlaceholder(icono: ImageVector, titulo: String, subtitulo: String) {
    Column(Modifier.fillMaxSize(), Alignment.CenterHorizontally, Arrangement.Center) {
        Icon(icono, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        Spacer(Modifier.height(12.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(subtitulo, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f))
    }
}
