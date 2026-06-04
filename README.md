# MotoContable

App Android para moto taxistas — registro de ingresos diarios.

## Funcionalidades
- Registro diario: **ida y vuelta** independientes (alumno: S/2 c/tramo, profesor: S/6 c/tramo)
- Viajes **extra** con descripcion y monto libre
- Resumen semanal (lunes-viernes) con navegacion entre semanas
- Historial de semanas anteriores
- Configuracion de nombres y precios

## Stack
| Capa | Tecnologia |
|------|-----------|
| UI | Jetpack Compose + Material 3 |
| Estado | ViewModel + StateFlow |
| BD | Room (SQLite local) |
| DI | Hilt |
| CI | GitHub Actions |

## Compilar APK
Cada push a `main` genera el APK en **Actions > Artifacts**.

## Roadmap
- [x] **Punto 1** — Base del proyecto (Gradle, CI, tema)
- [x] **Punto 2** — Room DB, Repository, ViewModels, Navegacion
- [ ] **Punto 3** — Pantalla diaria (ida/vuelta + extras)
- [ ] **Punto 4** — Pantalla de resumen semanal
- [ ] **Punto 5** — Historial y configuracion
